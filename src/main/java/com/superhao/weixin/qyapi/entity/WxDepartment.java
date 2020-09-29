package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 微信部门
 */
@Data
@Entity
public class WxDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;
    private Long agentid;

    /**
     * <pre>
     *        {
     *            "id": 3,
     *            "name": "邮箱产品部",
     *            "name_en": "mail",
     *            "parentid": 2,
     *            "order": 40
     *        }
     * </pre>
     */
    public Long deptid;
    private String name;
    private String name_en;
    private Long parentid;

    @Column(name="dept_order")
    private Long order;


}
