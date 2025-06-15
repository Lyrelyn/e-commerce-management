package org.example.service;

import org.example.bean.Delivery;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface DeliveryService {

    PageResult<Delivery> page(Integer page, String courierName, LocalDate begin, LocalDate end, Integer pageSize, Integer orderId);

    void save(Delivery delivery);

    void delete(List<Integer> ids);

    Result getInfo(Integer id);

    void update(Delivery delivery);
}
