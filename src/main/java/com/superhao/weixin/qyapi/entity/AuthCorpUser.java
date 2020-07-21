package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 授权方企业的用户，来自 成员关注及取消关注事件
 */
@Data
@Entity
public class AuthCorpUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;
    private String userid;
    private Long agentid;
}
