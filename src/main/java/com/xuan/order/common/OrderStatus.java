package com.xuan.order.common;

/**
 * 订单状态机
 * 0待支付 → 1已支付 → 2已发货
 *     └→ 3已关闭(超时未支付自动关闭)
 */
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    CLOSED(3, "已关闭");

    public final int code;
    public final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus of(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知订单状态: " + code);
    }
}
