package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 获取部门成员
 */
@Data
@Entity
public class WeChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;

    /**
     * <pre>
     *      {
     *                   "userid": "zhangsan",
     *                   "name": "李四",
     *                   "department": [1, 2],
     *                   "open_userid": "xxxxxx"
     *            }
     * </pre>
     */

    private String userid;
    /**
     * 打印机场景 企业微信扫码传递，非获取部门成员simple list参数
     */
    private Integer userType = 0;
    private String name;
    /**
     * 逗号分隔的id字符串
     */
    private String department;
    private String open_userid;

    /**
     * 当前扫描的哪一台机器，数据库值仅代表最后扫的那一台
     */
    private Long currentDeviceInfoId;

    private Integer permissions = 7;

}
