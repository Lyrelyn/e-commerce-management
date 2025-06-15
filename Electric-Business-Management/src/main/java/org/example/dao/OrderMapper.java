package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.bean.Order;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> list(Integer orderId, String customerName);

    void insert(Order order);

    void deleteByIds(List<Integer> ids);

    Order getById(Integer id);

    void updateById(Order order);
}
