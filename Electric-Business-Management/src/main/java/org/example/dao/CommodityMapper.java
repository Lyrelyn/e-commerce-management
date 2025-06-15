package org.example.dao;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.example.bean.Commodity;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommodityMapper {

    List<Commodity> list(String name, String minPrice, String maxPrice);

    Commodity getInfo(Integer id);

    void save(Commodity commodity);

    void update(Commodity commodity);

    void delete(List<Integer> ids);

    @MapKey("sales")
    List<Map<String, Object>> getCommoditySaleData();
}
