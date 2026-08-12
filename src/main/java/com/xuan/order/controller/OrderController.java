package com.xuan.order.controller;

import com.xuan.order.common.Result;
import com.xuan.order.entity.Order;
import com.xuan.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 订单接口
 *
 * 测试流程:
 * 1. POST /order/create?productId=1&qty=1        下单(扣库存, 进入待支付, 投递30秒延迟关单)
 * 2. POST /order/pay?orderNo=xxx                  支付(待支付→已支付)
 * 3. POST /order/ship?orderNo=xxx                 发货(已支付→已发货)
 * 4. 若30秒未支付, 延迟队列自动关闭订单并回补库存
 * 5. GET  /order/list                            查看全部订单
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Order> create(@RequestParam Long productId,
                                @RequestParam(defaultValue = "1") int qty) {
        return Result.ok(orderService.createOrder(productId, qty));
    }

    @PostMapping("/pay")
    public Result<Boolean> pay(@RequestParam String orderNo) {
        boolean ok = orderService.pay(orderNo);
        return ok ? Result.ok(true) : Result.fail("支付失败: 订单不存在或已不在待支付状态");
    }

    @PostMapping("/ship")
    public Result<Boolean> ship(@RequestParam String orderNo) {
        boolean ok = orderService.ship(orderNo);
        return ok ? Result.ok(true) : Result.fail("发货失败: 订单不存在或未支付");
    }

    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.ok(orderService.listAll());
    }

    @GetMapping("/{orderNo}")
    public Result<Order> detail(@PathVariable String orderNo) {
        Order o = orderService.getByOrderNo(orderNo);
        return o != null ? Result.ok(o) : Result.fail("订单不存在");
    }
}
