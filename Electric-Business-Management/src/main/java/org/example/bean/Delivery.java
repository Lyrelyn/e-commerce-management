package org.example.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
    private Integer id; // 主键ID
    private Integer orderId; // 订单ID
    private String courierName; // 快递员姓名
    private String courierPhone; // 快递员电话
    private String courierCompany; // 快递公司
    private String deliveryAddress; // 收货地址
    private String location; // 位置信息
    private Integer status;     // 状态 0:未发货 1:已发货 2:已到达
    private LocalDate deliveryTime; // 发货时间
    private LocalDate arrivalTime; // 预计到达时间
}
