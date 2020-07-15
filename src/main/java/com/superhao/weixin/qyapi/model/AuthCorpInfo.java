package com.superhao.weixin.qyapi.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 企业授权信息 - auth_corp_info - 中间处理数据 -无需关心
 */
@Data
public class AuthCorpInfo {
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
}
