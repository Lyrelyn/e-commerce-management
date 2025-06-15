package org.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Integer id;       // 客户ID
    private String username;    // 用户名
    private String password;    // 密码
    private Integer gender;     // 性别（0：男，1：女）
    private String image;       // 头像路径
    private String remark;       // 备注
    private String phone;       // 手机号
    private String email;       // 邮箱
    private String address;     // 地址
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
    private Integer level;       // 等级（0：普通用户，1：VIP用户）
}
