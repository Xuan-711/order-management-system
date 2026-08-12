package com.xuan.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long productId;
    private Integer quantity;
    private BigDecimal amount;
    /** 0待支付 1已支付 2已发货 3已关闭 */
    private Integer status;
    private Date createTime;
    private Date payTime;
    private Date shipTime;
    private Date closeTime;
}
