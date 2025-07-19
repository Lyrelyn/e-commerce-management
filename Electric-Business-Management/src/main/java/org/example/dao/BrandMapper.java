package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.bean.Brand;

import java.util.List;

@Mapper
public interface BrandMapper {

    List<Brand> list(Brand brand);

    Brand getById(Integer id);

    void insert(Brand brand);

    void deleteByIds(List<Integer> ids);

    void updateById(Brand brand);
}
