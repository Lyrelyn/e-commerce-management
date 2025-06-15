package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.bean.Delivery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DeliveryMapper {

    List<Delivery> list(Integer orderId, String courierName, LocalDate begin, LocalDate end);

    void insert(Delivery delivery);

    void delete(List<Integer> ids);

    Delivery getInfo(Integer id);

    void update(Delivery delivery);
}
