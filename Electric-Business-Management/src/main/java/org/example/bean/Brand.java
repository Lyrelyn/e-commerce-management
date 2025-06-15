package org.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brand {
    private int id; // 编号
    private String brandName;    // 品牌名称
    private String companyName;  // 企业名称
    private String description;  // 描述信息
    private int pageSize;       // 每页显示条数
    private int pageNum;        // 当前页码
    private String image;             // 图片地址
    private List<Commodity> commodities;    // 商品集合
}
