package com.superhao.weixin.qyapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 微信部门
 */
@Data
@Entity
public class WeChatDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Date created_at = new Date();
    private Date updated_at = new Date();

    private String corpid;

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
    private Integer permissions = 7;

    @Column(name="dept_order")
    private Long order;


}
