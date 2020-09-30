package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 企业授权信息
 */
@Data
@Entity
@Table(name = "wx_auth_corp")
public class AuthCorp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String access_token;
    private Long expires_in;
    private String permanent_code;

    @Column(unique = true)
    private String corpid;
    private String corp_name;
    private String corp_type;
    private String corp_square_logo_url;
    private Integer corp_user_max;
    private Integer corp_agent_max;
    private String corp_full_name;
    private Long verified_end_time;
    private Integer subject_type;
    private String corp_wxqrcode;
    private String corp_scale;
    private String corp_industry;
    private String corp_sub_industry;
    private String location;

    // private String auth_info;

    private Long agentid;
    private String name;
    private String round_logo_url;
    private String square_logo_url;
    private Long appid;
    private String privilege;

    private String auth_user_info;
}
