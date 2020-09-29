package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 获取部门成员
 */
@Data
@Entity
public class WxUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;
    private Long agentid;

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
    private String name;
    /**
     * 逗号分隔的id字符串
     */
    private String department;
    private String open_userid;

}
