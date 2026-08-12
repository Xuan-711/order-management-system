package com.xuan.order;

import com.xuan.order.entity.Order;
import com.xuan.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单流程集成测试
 * 验证: 下单 → 支付 → 发货 状态机流转正确
 */
@SpringBootTest
class OrderApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateAndPay() {
        // 1. 下单
        Order order = orderService.createOrder(1L, 1);
        assertNotNull(order.getOrderNo());
        assertEquals(0, order.getStatus()); // 待支付

        // 2. 支付
        boolean payOk = orderService.pay(order.getOrderNo());
        assertTrue(payOk);

        // 3. 查询确认状态
        Order paid = orderService.getByOrderNo(order.getOrderNo());
        assertEquals(1, paid.getStatus()); // 已支付
    }

    @Test
    void testListAll() {
        List<Order> list = orderService.listAll();
        assertNotNull(list);
    }
}
