package org.example.controller;

import lombok.extern.slf4j.Slf4j;
//import org.example.pojo.JobOption;
import org.example.bean.Result;
import org.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

//    @GetMapping("/empJobData")
//    public Result getEmpJobData() {
//        log.info("统计顾客人数");
//        JobOption jobOption = reportService.getEmpJobData();
//        return Result.success(jobOption);
//    }

    @GetMapping("/customerGenderData")
    public Result getCustomerGenderData() {
        log.info("统计性别人数");
        List<Map<String, Object>> list = reportService.getCustomerGenderData();
        return Result.success(list);
    }

    @GetMapping("/customerLevelData")
    public Result getCustomerLevelData() {
        log.info("统计顾客等级人数");
        List<Map<String, Object>> list = reportService.getCustomerLevelData();
        return Result.success(list);
    }

    @GetMapping("/commoditySaleData")
    public Result getCommoditySaleData() {
        log.info("统计商品销售数据");
        List<Map<String, Object>> list = reportService.getCommoditySaleData();
        return Result.success(list);
    }


}

