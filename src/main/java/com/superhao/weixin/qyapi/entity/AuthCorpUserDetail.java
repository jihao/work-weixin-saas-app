package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 授权方企业的用户详细信息，来自 通讯录变更事件通知 XML
 */
@Data
@Entity
public class AuthCorpUserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    /**
     * 第三方应用ID
     */
    private String suiteId;

    /**
     * 授权企业的CorpID
     */
    private String authCorpId;

    /**
     * 成员UserID
     */
    private String userID;
//    private String newUserID;

    private String name;

    private String department;

    private String mobile;

    private String position;

    private Integer gender;

    private String email;

    private Integer status;

    private String avatar;

    private String englishName;

    private Integer isLeader;

    private String telephone;


}
