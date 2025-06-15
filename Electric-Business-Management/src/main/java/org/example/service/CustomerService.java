package org.example.service;


import org.example.bean.Commodity;
import org.example.bean.Customer;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {

    public PageResult<Customer> page(String username, Integer gender, Integer page, Integer pageSize) ;


    Result update(Customer customer);

    void delete(List<Integer> ids);

    void save(Customer customer);

    Result getInfo(Integer id);
}
