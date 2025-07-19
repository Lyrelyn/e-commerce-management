package org.example.controller;

import org.example.bean.Customer;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result page(@RequestParam(required = false) String username,
                       @RequestParam(required = false) Integer gender,
                       @RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        //log.info("查询信息, page={}, pageSize={}", page, pageSize);
        PageResult<Customer> pageResult = customerService.page(username, gender, page, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Customer customer){

        customerService.save(customer);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        customerService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        //log.info("根据id查询",id);
        //Customer customer = customerService.getInfo(id);
        //return Result.success(customer);
        return customerService.getInfo(id);
    }

    @PutMapping
    public Result update(@RequestBody Customer customer){

        return customerService.update(customer);
        //return Result.success();
    }
}
