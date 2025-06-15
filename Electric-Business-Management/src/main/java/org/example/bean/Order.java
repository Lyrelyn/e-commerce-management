package org.example.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;            // 主键ID
    private Integer orderId;     // 客户ID
    private String customerName;    // 客户姓名
    private String customerPhone;   // 客户电
    private Double totalPrice;      // 总价
    private Integer status;          // 订单状态（待支付、已支付、已取消）
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String remark;          // 备注

    private List<OrderItem> orderItemList;
}
