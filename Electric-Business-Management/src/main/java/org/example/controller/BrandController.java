package org.example.controller;

import org.example.bean.Brand;
import org.example.bean.PageResult;
import org.example.bean.Result;
import org.example.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public Result page(@RequestParam(required = false, defaultValue = "") String brandName,
                       @RequestParam(required = false, defaultValue = "") String companyName,
                       @RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Brand brand = new Brand();
        brand.setBrandName(brandName);
        brand.setCompanyName(companyName);
        PageResult<Brand> pageResult = brandService.page(brand, page, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Brand brand) {
        brandService.save(brand);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        brandService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        //Brand brand = brandService.getInfo(id);
        return brandService.getInfo(id);
    }

    @PutMapping
    public Result update(@RequestBody Brand brand) {
        brandService.update(brand);
        return Result.success();
    }

}
