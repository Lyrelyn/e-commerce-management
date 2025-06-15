package org.example.service.Impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.Commodity;
import org.example.bean.Customer;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.dao.CommodityMapper;
import org.example.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CommodityServiceImpl implements CommodityService {

    @Autowired
    private CommodityMapper commodityMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<Commodity> page(String name, Integer pageNum, Integer pageSize, String minPrice, String maxPrice) {
        log.info("查询：{},{},{},{},{}", name, pageNum, pageSize, minPrice, maxPrice);
        //1. 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        //2. 执行查询
        List<Commodity> commodityList = commodityMapper.list(name, minPrice, maxPrice);
        Page<Commodity> p = (Page<Commodity>) commodityList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Commodity commodity) {
        commodity.setCreatedTime(LocalDateTime.now());
        commodity.setUpdatedTime(LocalDateTime.now());
        commodityMapper.save(commodity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        commodityMapper.delete(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Commodity commodity) {
        commodity.setUpdatedTime(LocalDateTime.now());
        commodityMapper.update(commodity);
        //删除缓存
        String key = "cathe:commodity:" + commodity.getId();
        stringRedisTemplate.delete(key);
    }

    //通过id查询商品信息
    @Override
    public Result getInfo(Integer id) {
        String key = "cathe:commodity:" + id;
        String commodityJson = stringRedisTemplate.opsForValue().get(key);

        if(StrUtil.isNotBlank(commodityJson)){
            Commodity commodity = JSONUtil.toBean(commodityJson,Commodity.class);
            return Result.success(commodity);
        }

        Commodity commodity = commodityMapper.getInfo(id);
        // 生成一个 1 到 5 分钟的随机数
        long randomMinutes = (long) (Math.random() * 5 + 1);
        // 将基础时间和随机时间相加
        long ttl = 15L + randomMinutes;
        //将查询结果写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(commodity), ttl, TimeUnit.MINUTES);
        return Result.success(commodity);
    }
}
