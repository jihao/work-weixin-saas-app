package com.superhao.weixin.qyapi.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 企业授权信息 - auth_info - 中间处理数据 -无需关心
 */
@Data
public class AuthInfoAgent {
    private Long agentid;
    private String name;
    private String round_logo_url;
    private String square_logo_url;
    private Long appid;
    private String privilege;
}
