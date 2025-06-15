package org.example.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.example.bean.Brand;
import org.example.bean.Delivery;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.dao.BrandMapper;
import org.example.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult<Brand> page(Brand brand, Integer page, Integer pageSize) {
        //1. 设置分页参数
        PageHelper.startPage(page, pageSize);

        //2. 执行查询
        List<Brand> brandList = brandMapper.list(brand);
        Page<Brand> p = (Page<Brand>) brandList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Brand brand) {
        brandMapper.insert(brand);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        brandMapper.deleteByIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result getInfo(Integer id) {
        String key = "cathe:brand:" + id;
        //连接
        String brandJson = stringRedisTemplate.opsForValue().get(key);
        //判断是否命中
        if(StrUtil.isNotBlank(brandJson)){
            Brand brand = JSONUtil.toBean(brandJson,Brand.class);
            return Result.success(brand);
        }
        //未命中，查询数据库
        Brand brand = brandMapper.getById(id);

        // 生成一个 1 到 5 分钟的随机数
        long randomMinutes = (long) (Math.random() * 5 + 1);
        // 将基础时间和随机时间相加
        long ttl = 15L + randomMinutes;
        //将查询结果写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(brand), ttl, TimeUnit.MINUTES);
        return Result.success(brand);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Brand brand) {
        brandMapper.updateById(brand);
        String key = "cathe:brand:" + brand.getId();
        stringRedisTemplate.delete(key);
    }
}
