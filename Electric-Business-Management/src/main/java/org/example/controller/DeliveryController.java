package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.Delivery;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping
    public Result page(@RequestParam(required = false) Integer orderId,
                       @RequestParam(required = false) String courierName,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize){

        PageResult<Delivery> pageResult = deliveryService.page(page, courierName, begin, end, pageSize, orderId);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Delivery delivery) {
        //log.info("新增员工： {}");
        deliveryService.save(delivery);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        deliveryService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询",id);
        //Delivery delivery = deliveryService.getInfo(id);
        return deliveryService.getInfo(id);
    }

    @PutMapping
    public Result update(@RequestBody Delivery delivery) {
        deliveryService.update(delivery);
        return Result.success();
    }
}
