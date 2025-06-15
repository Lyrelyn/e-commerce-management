package org.example.service;

import org.example.bean.Order;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    void update(Order order) ;

    PageResult<Order> page(Integer id, String customerName, Integer pageNum, Integer pageSize);

    void save(Order order);

    void delete(List<Integer> ids);

    Result getInfo(Integer id);
}
