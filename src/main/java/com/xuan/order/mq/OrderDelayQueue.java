package com.xuan.order.mq;

import com.xuan.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 延迟队列实现订单超时自动关闭
 *
 * 生产环境可替换为 Redis zset / RocketMQ 延迟消息 / 时间轮。
 * 这里用 JDK DelayQueue 做本地实现，零依赖即可验证流程。
 */
@Component
public class OrderDelayQueue {

    /** 订单超时时间(毫秒)，默认30秒便于测试 */
    private static final long TIMEOUT_MS = 30_000L;

    @Autowired
    private OrderService orderService;

    private final DelayQueue<DelayTask> queue = new DelayQueue<>();

    @PostConstruct
    public void start() {
        // 守护线程消费延迟任务
        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    DelayTask task = queue.take();
                    orderService.closeTimeout(task.orderNo);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    // 单个任务异常不影响队列继续运行
                }
            }
        }, "order-delay-consumer");
        consumer.setDaemon(true);
        consumer.start();
    }

    /** 投递一个延迟关单任务 */
    public void offer(String orderNo) {
        queue.offer(new DelayTask(orderNo, TIMEOUT_MS));
    }

    /** 延迟任务 */
    static class DelayTask implements Delayed {
        final String orderNo;
        final long executeTime;

        DelayTask(String orderNo, long delayMs) {
            this.orderNo = orderNo;
            this.executeTime = System.currentTimeMillis() + delayMs;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = executeTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.executeTime, ((DelayTask) o).executeTime);
        }
    }
}
