package com.xuan.order.service.impl;

import com.xuan.order.common.OrderStatus;
import com.xuan.order.entity.Order;
import com.xuan.order.entity.Product;
import com.xuan.order.mapper.OrderMapper;
import com.xuan.order.mapper.ProductMapper;
import com.xuan.order.mq.OrderDelayQueue;
import com.xuan.order.service.OrderService;
import com.xuan.order.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private StockService stockService;
    @Autowired
    private OrderDelayQueue delayQueue;

    /**
     * 下单流程:
     * 1. 扣库存(Redis+Lua / DB降级)
     * 2. 创建订单(状态=待支付)
     * 3. 投递延迟关单任务
     */
    @Override
    @Transactional
    public Order createOrder(Long productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("购买数量必须大于0");
        }
        // 1. 扣库存
        boolean ok = stockService.deduct(productId, qty);
        if (!ok) {
            throw new RuntimeException("库存不足，秒杀失败");
        }
        // 2. 查商品算金额
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(qty));

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setQuantity(qty);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING.code);
        order.setCreateTime(new Date());
        orderMapper.insert(order);

        // 3. 投递延迟关单
        delayQueue.offer(order.getOrderNo());

        return order;
    }

    /** 支付回调: 待支付 → 已支付 */
    @Override
    public boolean pay(String orderNo) {
        return orderMapper.updateStatus(orderNo,
                OrderStatus.PENDING.code, OrderStatus.PAID.code) > 0;
    }

    /** 发货: 已支付 → 已发货 */
    @Override
    public boolean ship(String orderNo) {
        return orderMapper.updateStatus(orderNo,
                OrderStatus.PAID.code, OrderStatus.SHIPPED.code) > 0;
    }

    /**
     * 超时关闭: 待支付 → 已关闭
     * 关闭时回补库存(此处用数据库直接加回)
     */
    @Override
    @Transactional
    public boolean closeTimeout(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getStatus() != OrderStatus.PENDING.code) {
            return false;
        }
        int rows = orderMapper.updateStatus(orderNo,
                OrderStatus.PENDING.code, OrderStatus.CLOSED.code);
        if (rows > 0) {
            // 回补库存
            productMapper.deductStock(order.getProductId(), -order.getQuantity());
            return true;
        }
        return false;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> listAll() {
        return orderMapper.selectAll();
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
