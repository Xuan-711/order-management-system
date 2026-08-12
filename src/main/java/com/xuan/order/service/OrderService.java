package com.xuan.order.service;

import com.xuan.order.entity.Order;
import java.util.List;

public interface OrderService {

    /** 下单: 扣库存 + 创建订单 + 投递延迟关单 */
    Order createOrder(Long productId, int qty);

    /** 支付回调: 待支付 → 已支付 */
    boolean pay(String orderNo);

    /** 发货: 已支付 → 已发货 */
    boolean ship(String orderNo);

    /** 超时关闭: 待支付 → 已关闭(回补库存) */
    boolean closeTimeout(String orderNo);

    Order getByOrderNo(String orderNo);

    List<Order> listAll();
}
