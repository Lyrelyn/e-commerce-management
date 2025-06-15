package org.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Integer id;            // ID
    private Integer orderId;       // 订单ID
    private String commodityName;   // 商品名称
    private Integer number;         // 购买数量
    private Double price;           // 单价
}
