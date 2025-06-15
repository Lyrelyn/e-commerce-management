package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.Commodity;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/commodities")
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping
    public Result page(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String minPrice,
                       @RequestParam(required = false) String maxPrice,
                       @RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        log.info("查询：",name,page,pageSize,minPrice,maxPrice);
        PageResult<Commodity> pageResult = commodityService.page(name, page, pageSize, minPrice, maxPrice);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询",id);
        //Commodity commodity = commodityService.getInfo(id);
        return commodityService.getInfo(id);
    }

    @PostMapping
    public Result save(@RequestBody Commodity commodity){
        //log.info("新增信息: {}", commodity);
        commodityService.save(commodity);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        //log.info("删除信息, ids={}", ids);
        commodityService.delete(ids);
        return Result.success();
    }


    @PutMapping
    public Result update(@RequestBody Commodity commodity){
        //log.info("更新信息: {}", employee);
        commodityService.update(commodity);
        return Result.success();
    }
}
