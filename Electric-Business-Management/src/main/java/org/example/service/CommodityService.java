package org.example.service;

import org.example.bean.Commodity;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommodityService {

    public PageResult<Commodity> page(String name, Integer pageNum, Integer pageSize, String minPrice, String maxPrice);

    void save(Commodity commodity);

    void delete(List<Integer> ids);

    void update(Commodity commodity);

    Result getInfo(Integer id);
}
