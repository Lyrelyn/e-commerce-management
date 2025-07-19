package org.example.dao;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.example.bean.Commodity;
import org.example.bean.Customer;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMapper {

    List<Customer> list(String username, Integer gender);

    void delete(List<Integer> ids);

    void save(Customer customer);

    void update(Customer customer);

    Customer getInfo(Integer id);

    @MapKey("pos")
    List<Map<String, Object>> getCustomerGenderData();

    @MapKey("gender")
    List<Map<String, Object>> getCustomerLevelData();
}
