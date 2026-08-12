package com.xuan.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private BigDecimal price;
    private Date createTime;
}
