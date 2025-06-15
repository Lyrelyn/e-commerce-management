package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.bean.OrderItem;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    void insertBatch(List<OrderItem> orderList);

    void deleteByOrderIds(List<Integer> orderIds);
}
