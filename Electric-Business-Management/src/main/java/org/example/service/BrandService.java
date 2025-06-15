package org.example.service;

import org.example.bean.Brand;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BrandService {
    PageResult<Brand> page(Brand brand, Integer page, Integer pageSize);

    void save(Brand brand);

    void delete(List<Integer> ids);

    Result getInfo(Integer id);

    void update(Brand brand);
}
