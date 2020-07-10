package com.superhao.weixin.qyapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "work.weixin")
public class WorkWeixinProperties {

    /**
     * 服务商的corpid
     */
    private String corpId;
    /**
     * 服务商的secret
     */
    private String providerSecret;
    /**
     * 服务商的token
     */
    private String token;
    /**
     * 服务商的EncodingAESKey
     */
    private String aesKey;
    /**
     * 服务商的应用Id
     */
    private String suiteId;
    /**
     * 服务商的应用secret
     */
    private String suiteSecret;

    // 就是应用的secret
    private String corpSecret;

    // 应用id
    private Integer agentId;

}