package org.example.service.Impl;

import org.example.dao.CommodityMapper;
import org.example.dao.CustomerMapper;
import org.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CommodityMapper commodityMapper;

    @Override
    public List<Map<String, Object>> getCustomerGenderData() {
        return customerMapper.getCustomerGenderData();
    }

    @Override
    public List<Map<String, Object>> getCustomerLevelData() {
        return customerMapper.getCustomerLevelData();
    }

    @Override
    public List<Map<String, Object>> getCommoditySaleData() {
        return commodityMapper.getCommoditySaleData();
    }
}
