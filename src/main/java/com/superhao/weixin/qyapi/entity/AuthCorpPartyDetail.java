package com.superhao.weixin.qyapi.entity;

import com.superhao.weixin.qyapi.util.xml.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 授权方企业的部门详细信息，来自 通讯录变更事件通知 XML
 */
@Data
@Entity
@Table(name = "wx_auth_corp_party_detail")
public class AuthCorpPartyDetail {
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
     * 部门名称
     */
    private String name;

    /**
     * 部门Id
     */
    public Long partyId;

    /**
     * 父部门id.
     */
    private String parentId;

    /**
     * 部门排序.
     */
    @Column(name = "party_order")
    private Long order;


}
