package org.example.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.*;
import org.example.dao.DeliveryMapper;
import org.example.dao.OrderItemMapper;
import org.example.dao.OrderMapper;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DeliveryMapper deliveryMapper; // 注入 DeliveryMapper

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Order order) {
        order.setUpdatedTime(LocalDateTime.now());
        orderMapper.updateById(order);

        orderItemMapper.deleteByOrderIds(Arrays.asList(order.getOrderId()));

        List<OrderItem> orderList = order.getOrderItemList();
        if(!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(orderItem -> orderItem.setOrderId(order.getOrderId()));
            orderItemMapper.insertBatch(orderList);
        }

        // 检查订单状态是否变更为 "已支付"
        if (order.getStatus() == 1) {
            Delivery delivery = new Delivery();
            delivery.setOrderId(order.getOrderId());
            delivery.setStatus(0);
            // 插入配送单信息
            deliveryMapper.insert(delivery);
            log.info("订单 {} 状态变更为已支付，已生成配送单", order.getOrderId());
        }

        String key = "cathe:order" + order.getId();
        stringRedisTemplate.delete(key);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult<Order> page(Integer orderId, String customerName, Integer page, Integer pageSize) {
        //1. 设置分页参数
        PageHelper.startPage(page, pageSize);

        //2. 执行查询
        List<Order> orderList = orderMapper.list(orderId, customerName);
        Page<Order> p = (Page<Order>) orderList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Order order) {
        log.info("新增订单：{}", order);
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        orderMapper.insert(order);

        List<OrderItem> orderList = order.getOrderItemList();
        if(!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(orderItem -> orderItem.setOrderId(order.getOrderId()));
            orderItemMapper.insertBatch(orderList);
        }

        // 检查订单状态是否变更为 "已支付"
        if (order.getStatus() == 1) {
            Delivery delivery = new Delivery();
            delivery.setOrderId(order.getOrderId());
            delivery.setStatus(0);
            // 插入配送单信息
            deliveryMapper.insert(delivery);
            log.info("订单 {} 状态变更为已支付，已生成配送单", order.getOrderId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        orderItemMapper.deleteByOrderIds(ids);
        orderMapper.deleteByIds(ids);

    }

    @Override
    public Result getInfo(Integer id) {
        String key = "cathe:order:" + id;
        //连接
        String orderJson = stringRedisTemplate.opsForValue().get(key);
        //判断是否命中
        if(StrUtil.isNotBlank(orderJson)){
            Order order = JSONUtil.toBean(orderJson,Order.class);
            return Result.success(order);
        }
        //未命中，查询数据库
        Order order = orderMapper.getById(id);

        // 生成一个 1 到 5 分钟的随机数
        long randomMinutes = (long) (Math.random() * 5 + 1);
        // 将基础时间和随机时间相加
        long ttl = 15L + randomMinutes;
        //将查询结果写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(order), ttl, TimeUnit.MINUTES);
        return Result.success(order);
    }
}
