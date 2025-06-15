package org.example.controller;

import org.example.bean.Order;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result page(@RequestParam(required = false) Integer orderId,
                       @RequestParam(required = false) String customerName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Order> pageResult = orderService.page(orderId, customerName, page, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Order order) {
        orderService.save(order);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        orderService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        //Order order = orderService.getInfo(id);
        return orderService.getInfo(id);
    }

    @PutMapping
    public Result update(@RequestBody Order order) {
        orderService.update(order);
        return Result.success();
    }
}
