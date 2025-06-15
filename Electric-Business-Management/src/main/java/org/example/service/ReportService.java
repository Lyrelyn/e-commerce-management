package org.example.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ReportService {

    List<Map<String, Object>> getCustomerGenderData();

    List<Map<String, Object>> getCustomerLevelData();

    List<Map<String, Object>> getCommoditySaleData();
}
