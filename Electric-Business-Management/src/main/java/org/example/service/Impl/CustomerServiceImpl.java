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
import org.example.dao.CustomerMapper;
import org.example.dao.OrderMapper;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public PageResult<Customer> page(String username, Integer gender, Integer page, Integer pageSize) {
        //1. 设置分页参数
        PageHelper.startPage(page, pageSize);

        //2. 执行查询
        List<Customer> customerList = customerMapper.list(username, gender);
        Page<Customer> p = (Page<Customer>) customerList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result update(Customer customer) {
        customer.setUpdatedTime(LocalDateTime.now());
        customerMapper.update(customer);
        //删除缓存
        String key = "cathe:customer:" + customer.getId();
        stringRedisTemplate.delete(key);

        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        customerMapper.delete(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Customer customer) {
        customer.setCreatedTime(LocalDateTime.now());
        customer.setUpdatedTime(LocalDateTime.now());

        customerMapper.save(customer);
    }

    @Override
    public Result getInfo(Integer id) {
        String key = "cathe:customer:" + id;
        //连接
        String customerJson = stringRedisTemplate.opsForValue().get(key);
        //判断是否命中
        if(StrUtil.isNotBlank(customerJson)){
            Customer customer = JSONUtil.toBean(customerJson,Customer.class);
            return Result.success(customer);
        }
        //未命中，查询数据库
        Customer customer = customerMapper.getInfo(id);

        // 生成一个 1 到 5 分钟的随机数
        long randomMinutes = (long) (Math.random() * 5 + 1);
        // 将基础时间和随机时间相加
        long ttl = 15L + randomMinutes;
        //将查询结果写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(customer), ttl, TimeUnit.MINUTES);
        return Result.success(customer);
    }
}
