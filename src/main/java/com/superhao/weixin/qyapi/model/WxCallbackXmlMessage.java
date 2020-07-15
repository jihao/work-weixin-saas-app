package com.superhao.weixin.qyapi.model;

import com.superhao.weixin.qyapi.util.xml.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信第三方服务商应用 “指令回调URL”推送相应的事件消息
 * 文档: https://work.weixin.qq.com/api/doc/10982
 */
@Data
@XStreamAlias("xml")
public class WxCallbackXmlMessage implements Serializable {

    /**
     * 第三方应用ID
     */
    @XStreamAlias("SuiteId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String suiteId;

    /**
     * Ticket内容，最长为512字节
     */
    @XStreamAlias("SuiteTicket")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String suiteTicket;

    /**
     * 授权的auth_code,最长为512字节。用于获取企业的永久授权码。5分钟内有效
     */
    @XStreamAlias("AuthCode")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authCode;

    @XStreamAlias("AuthCorpId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authCorpId;

    /**
     * 事件类型
     * <pre>
     * 推送suite_ticket suite_ticket
     * 授权成功通知 create_auth
     * 变更授权通知 change_auth
     * 取消授权通知 cancel_auth
     * 通讯录变更事件通知
     *   - 新增成员事件
     *   - 更新成员事件
     *   - 删除成员事件
     *   - 新增部门事件
     *   - 更新部门事件
     *   - 删除部门事件
     *   - 标签成员变更事件
     * </pre>
     */
    @XStreamAlias("InfoType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String infoType;

    /**
     * 时间戳
     */
    @XStreamAlias("TimeStamp")
    private Long timeStamp;

    /**
     * 通讯录变更事件通知
     */
    @XStreamAlias("ChangeType")
    private String changeType;

    //////////////////////////////////////////////////////////////
    // 成员事件
    //////////////////////////////////////////////////////////////
    @XStreamAlias("UserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String userID;
    @XStreamAlias("NewUserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String newUserID;
    @XStreamAlias("Name")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String name;
    @XStreamAlias("Department")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String department;
    @XStreamAlias("Mobile")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String mobile;
    @XStreamAlias("Position")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String position;
    @XStreamAlias("Gender")
    private Integer gender;
    @XStreamAlias("Email")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String email;
    @XStreamAlias("Avatar")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String avatar;
    @XStreamAlias("EnglishName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String englishName;
    @XStreamAlias("IsLeader")
    private Integer isLeader;
    @XStreamAlias("Telephone")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String telephone;

    //////////////////////////////////////////////////////////////
    // 部门事件
    //////////////////////////////////////////////////////////////
    /**
     * 部门Id.
     */
    @XStreamAlias("Id")
    private Long id;

    /**
     * 父部门id.
     */
    @XStreamAlias("ParentId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String parentId;

    /**
     * 部门排序.
     */
    @XStreamAlias("Order")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String order;

    //////////////////////////////////////////////////////////////
    // 标签成员变更事件
    //////////////////////////////////////////////////////////////
    /**
     * 标签Id.
     */
    @XStreamAlias("TagId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String tagId;

    /**
     * 标签中新增的成员userid列表，用逗号分隔.
     */
    @XStreamAlias("AddUserItems")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String addUserItems;

    /**
     * 标签中删除的成员userid列表，用逗号分隔.
     */
    @XStreamAlias("DelUserItems")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String delUserItems;

    /**
     * 标签中新增的部门id列表，用逗号分隔.
     */
    @XStreamAlias("AddPartyItems")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String addPartyItems;

    /**
     * 标签中删除的部门id列表，用逗号分隔.
     */
    @XStreamAlias("DelPartyItems")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String delPartyItems;

    /**
     * 扩展属性.
     */
    @XStreamAlias("ExtAttr")
    private ExtAttr extAttrs = new ExtAttr();


    @Data
    public static class ExtAttr {

        @XStreamImplicit(itemFieldName = "Item")
        protected final List<Item> items = new ArrayList<>();

        @XStreamAlias("Item")
        @Data
        public static class Item {
            @XStreamAlias("Name")
            @XStreamConverter(value = XStreamCDataConverter.class)
            private String name;

            @XStreamAlias("Value")
            @XStreamConverter(value = XStreamCDataConverter.class)
            private String value;
        }
    }
}
