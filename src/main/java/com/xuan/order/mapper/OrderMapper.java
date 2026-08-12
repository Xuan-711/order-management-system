package com.xuan.order.mapper;

import com.xuan.order.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    int insert(Order order);

    Order selectByOrderNo(@Param("orderNo") String orderNo);

    List<Order> selectAll();

    int updateStatus(@Param("orderNo") String orderNo,
                     @Param("fromStatus") int fromStatus,
                     @Param("toStatus") int toStatus);
}
