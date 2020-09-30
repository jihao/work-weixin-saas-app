package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 获取应用的管理员列表
 */
@Data
@Entity
@Table(name = "wx_auth_corp_admin_user")
public class AuthCorpAdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;
    private String userid;
    private Integer auth_type;
    private Long agentid;
}
