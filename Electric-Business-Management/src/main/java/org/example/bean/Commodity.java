package org.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commodity {
      private Integer id;          // 商品ID
      private String name;         // 商品名称
      private Double price;       // 价格
      private Integer stock;       // 库存
      private String description;  // 描述
      private String image;        // 图片路径
      private String status;       // 状态（上架/下架）
      private Integer sales;       // 销量
      private LocalDateTime createdTime;   // 创建时间
      private LocalDateTime updatedTime;   // 更新时间

      private Brand brand;
      private int brandId;

}
