package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 获取应用的管理员列表
 */
@Data
@Entity
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
