package com.xuan.order.mapper;

import com.xuan.order.entity.Product;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
    Product selectById(@Param("id") Long id);

    /** 乐观锁方式扣库存，返回影响行数 */
    int deductStock(@Param("id") Long id, @Param("qty") int qty);
}
