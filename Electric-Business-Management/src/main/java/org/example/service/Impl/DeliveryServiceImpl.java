package org.example.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.example.bean.Delivery;
import org.example.bean.Order;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.dao.DeliveryMapper;
import org.example.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public PageResult<Delivery> page(Integer page, String courierName, LocalDate begin, LocalDate end, Integer pageSize, Integer orderId) {
        PageHelper.startPage(page, pageSize);

        //2. 执行查询
        List<Delivery> empList = deliveryMapper.list(orderId, courierName, begin, end);
        Page<Delivery> p = (Page<Delivery>) empList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Delivery delivery) {
        deliveryMapper.insert(delivery);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        deliveryMapper.delete(ids);
    }

    @Cacheable(value = "deliveryCache", key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result getInfo(Integer id) {
        String key = "cathe:delivery:" + id;
        //连接
        String deliveryJson = stringRedisTemplate.opsForValue().get(key);
        //判断是否命中
        if(StrUtil.isNotBlank(deliveryJson)){
            Delivery delivery = JSONUtil.toBean(deliveryJson,Delivery.class);
            return Result.success(delivery);
        }
        //未命中，查询数据库
        Delivery delivery = deliveryMapper.getInfo(id);

        // 生成一个 1 到 5 分钟的随机数
        long randomMinutes = (long) (Math.random() * 5 + 1);
        // 将基础时间和随机时间相加
        long ttl = 15L + randomMinutes;
        //将查询结果写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(delivery), ttl, TimeUnit.MINUTES);
        return Result.success(delivery);
    }

    @Cacheable(value = "deliveryCache", key = "#delivery.id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Delivery delivery) {
        deliveryMapper.update(delivery);
        String key = "cathe:delivery:" +  delivery.getId();
        stringRedisTemplate.delete(key);
    }
}
