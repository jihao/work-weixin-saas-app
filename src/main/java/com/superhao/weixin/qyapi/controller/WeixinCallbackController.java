package com.superhao.weixin.qyapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.superhao.weixin.qyapi.config.WorkWxProperties;
import com.superhao.weixin.qyapi.entity.AuthCorp;
import com.superhao.weixin.qyapi.entity.AuthCorpPartyDetail;
import com.superhao.weixin.qyapi.entity.AuthCorpUser;
import com.superhao.weixin.qyapi.entity.AuthCorpUserDetail;
import com.superhao.weixin.qyapi.model.BizException;
import com.superhao.weixin.qyapi.model.WxCallbackXmlMessage;
import com.superhao.weixin.qyapi.repository.AuthCorpPartyDetailRepository;
import com.superhao.weixin.qyapi.repository.AuthCorpRepository;
import com.superhao.weixin.qyapi.repository.AuthCorpUserDetailRepository;
import com.superhao.weixin.qyapi.repository.AuthCorpUserRepository;
import com.superhao.weixin.qyapi.util.WxApiUtil;
import com.superhao.weixin.qyapi.util.DataConvertUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/callback")
public class WeixinCallbackController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WorkWxProperties workWxProperties;

    @Autowired
    private AuthCorpRepository authCorpRepository;

    @Autowired
    private AuthCorpUserRepository authCorpUserRepository;

    @Autowired
    private AuthCorpUserDetailRepository authCorpUserDetailRepository;

    @Autowired
    private AuthCorpPartyDetailRepository authCorpPartyDetailRepository;

    /**
     * 数据回调URL
     *
     * @param request
     * @param corpId
     * @return
     */
    @RequestMapping(path = "/datanotify/{corpId}", method = RequestMethod.GET)
    public String dataNotifyGet(HttpServletRequest request, @PathVariable String corpId) {
        String msgSignature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        logger.debug("GET数据回调加密签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
        try {
            //get请求是服务商的corpid来解密
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWxProperties.getToken(), workWxProperties.getAesKey(), workWxProperties.getCorpId());
            // 通对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);

            logger.debug("GET数据明文[{}]", sEchoStr);
            return sEchoStr;
        } catch (Exception e) {
            throw new BizException("GET数据回调验证URL失败", e);
        }
    }

    /**
     * 本方法处理“数据回调URL”推送(POST)的内容
     * <pre>
     * 用于接收托管企业微信应用的用户消息和用户事件。
     * URL支持使用$CORPID$模板参数表示corpid，推送事件时企业微信会自动将其替换为授权企业的corpid。
     * (关于如何回调，请参考接收消息 https://work.weixin.qq.com/api/doc/10514。
     *
     * 事件格式: https://work.weixin.qq.com/api/doc/90001/90143/90376
     *
     * 注意验证时$CORPID$模板参数会替换为当前服务商的corpid，校验时也应该使用corpid初始化解密库)
     * </pre>
     *
     * @param request
     * @param corpId
     * @return
     */
    @RequestMapping(path = "/datanotify/{corpId}", method = RequestMethod.POST)
    public String dataNotifyPost(HttpServletRequest request, @PathVariable String corpId, @RequestBody String body) {
        String msgSignature = request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        logger.debug("POST数据回调加密签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
        // corpId是坑,是由授权方企业微信应用发过来校验
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWxProperties.getToken(), workWxProperties.getAesKey(), corpId);
            String info = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, body);
            logger.debug("POST解密明文info[{}]", info);
            //XML文件解析
            Document documentInfo = DocumentHelper.parseText(info);
            Element root = documentInfo.getRootElement();
            //订阅消息
            String event = root.elementTextTrim("Event");

            // 事件格式 https://work.weixin.qq.com/api/doc/90001/90143/90376
            // 小程序在管理端开启接收消息配置后，也可收到关注/取消关注事件
            // 本事件触发时机为：
            //
            // 成员已经加入企业，管理员添加成员到应用可见范围(或移除可见范围)时
            // 成员已经在应用可见范围，成员加入(或退出)企业时
            if ("subscribe".equals(event)) {
                //授权方企业微信CorpID
                String toUserName = root.elementTextTrim("ToUserName");
                //授权方成员UserID
                String fromUserName = root.elementTextTrim("FromUserName");
                //授权方企业应用ID
                String agentID = root.elementTextTrim("AgentID");

                AuthCorpUser authCorpUser = new AuthCorpUser();
                authCorpUser.setAgentid(Long.valueOf(agentID));
                authCorpUser.setCorpid(toUserName);
                authCorpUser.setUserid(fromUserName);
                authCorpUserRepository.save(authCorpUser);
            } else if ("unsubscribe".equals(event)) {
                //授权方企业微信CorpID
                String toUserName = root.elementTextTrim("ToUserName");
                //授权方成员UserID
                String fromUserName = root.elementTextTrim("FromUserName");
                //授权方企业应用ID
                String agentID = root.elementTextTrim("AgentID");

                authCorpUserRepository.deleteByCorpidAndAgentidAndUserid(toUserName, Long.valueOf(agentID), fromUserName);
            }
        } catch (Exception e) {
            throw new BizException("POST数据回调异常", e);
        }
        return "success";
    }

    /*
     * @param request
     * @return
     * @throws AesException.java
     */
    @RequestMapping(path = "/suite/receive", method = RequestMethod.GET)
    public String suiteReceiveGet(
            @RequestParam(name = "msg_signature", required = false) String msgSignature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        //get请求是服务商本身的corpid来解密
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWxProperties.getToken(), workWxProperties.getAesKey(), workWxProperties.getCorpId());
            logger.debug("GET指令回调签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
            String url = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            logger.debug("GET指令明文[{}]", url);
            return url;
        } catch (Exception e) {
            throw new BizException("GET指令回调验证URL失败", e);
        }
    }

    /**
     * 本方法处理“第三方回调协议”推送(POST)的内容
     *
     * <pre>
     * 指令回调URL配置在企业微信服务商应用后台 eg. http://127.0.0.1/callback/suite/receive
     *
     * 第三方回调协议文档: https://work.weixin.qq.com/api/doc/10982
     *
     * 在发生授权、通讯录变更、ticket变化等事件时，企业微信服务器会向应用的“指令回调URL”推送相应的事件消息。消息结构体将使用创建应用时的EncodingAESKey进行加密（特别注意, 在第三方回调事件中使用加解密算法，receiveid的内容为suiteid），请参考接收消息解析数据包。
     *
     * 回调事件，服务商在收到推送后都必须直接返回字符串 “success”，若返回值不是 “success”，企业微信会把返回内容当作错误信息。
     * </pre>
     *
     * @return "success"
     */
    @RequestMapping("/suite/receive")
    public String suiteReceivePost(@RequestParam(name = "msg_signature", required = false) String msgSignature,
                                   @RequestParam(name = "timestamp", required = false) String timestamp,
                                   @RequestParam(name = "nonce", required = false) String nonce,
                                   @RequestParam(name = "echostr", required = false) String echostr,
                                   @RequestBody String body) {
        try {
            //指令回调url以第三方网页应用suiteid进行解密 ,
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWxProperties.getToken(), workWxProperties.getAesKey(), workWxProperties.getSuiteId());
            logger.debug("POST指令回调URL签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
            String info = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, body);
            logger.debug("POST解密明文[{}]", info);

            XStream xstream = new XStream(new DomDriver());
            xstream.processAnnotations(WxCallbackXmlMessage.class);
            WxCallbackXmlMessage wxMessage = (WxCallbackXmlMessage) xstream.fromXML(info);
            String infoType = wxMessage.getInfoType();

            String authCorpId;
            JSONObject resultJson;
            String suiteAccessToken = WxApiUtil.getCachedSuiteAccessToken();

            switch (infoType) {
                case "suite_ticket":
                    // 企业微信服务器会定时（每十分钟）推送ticket。ticket会实时变更，并用于后续接口的调用。
                    logger.debug("推送suite_ticket");
                    String suiteTicket = wxMessage.getSuiteTicket();
                    String suiteId = workWxProperties.getSuiteId();
                    String suiteSecret = workWxProperties.getSuiteSecret();
                    // 1. 获取第三方应用凭证
                    suiteAccessToken = WxApiUtil.getSuiteAccessToken(suiteId, suiteSecret, suiteTicket);
                    // 2. 获取预授权码
                    String preAuthCode = WxApiUtil.getPreAuthCode(suiteAccessToken);
                    // 3. 设置授权配置，一般由服务商界面触发此调用流程
                    break;
                case "create_auth":
                    // 从企业微信应用市场发起授权时，企业微信后台会推送授权成功通知。
                    logger.debug("授权成功通知");
                    String authCode = wxMessage.getAuthCode();
                    // 4. 获取企业的永久授权码
                    resultJson = WxApiUtil.getPermanentCode(suiteAccessToken, authCode);
                    // 保存企业授权信息
                    AuthCorp authCorp = DataConvertUtil.permanentCodeRespJson2AuthCorpEntity(resultJson);
                    authCorpRepository.save(authCorp);
                    break;
                case "change_auth":
                    // 当授权方（即授权企业）在企业微信管理端的授权管理中，修改了对应用的授权后，企业微信服务器推送变更授权通知。
                    // 服务商接收到变更通知之后，需自行调用获取企业授权信息进行授权内容变更比对。
                    logger.debug("变更授权通知");
                    authCorpId = wxMessage.getAuthCorpId();

                    Optional<AuthCorp> authCorpOptional = authCorpRepository.findFirstByCorpid(authCorpId);
                    String permanentCode = WxApiUtil.getCachedPermanentCode(authCorpId);
                    if (permanentCode.isEmpty() && authCorpOptional.isPresent()) {
                        permanentCode = authCorpOptional.get().getPermanent_code();
                    }
                    resultJson = WxApiUtil.getAuthInfo(suiteAccessToken, authCorpId, permanentCode);
                    // 保存企业授权信息
                    AuthCorp authCorpResp = DataConvertUtil.permanentCodeRespJson2AuthCorpEntity(resultJson);
                    if (authCorpOptional.isPresent()) {
                        AuthCorp authCorpFromDB = authCorpOptional.get();
                        BeanUtils.copyProperties(authCorpResp, authCorpFromDB);

                        authCorpFromDB.setUpdated_at(new Date());
                        authCorpRepository.save(authCorpFromDB);
                    }
                    break;
                case "cancel_auth":
                    // 当授权方（即授权企业）在企业微信管理端的授权管理中，取消了对应用的授权托管后，企业微信后台会推送取消授权通知。
                    logger.debug("取消授权通知");
                    authCorpId = wxMessage.getAuthCorpId();
                    authCorpRepository.deleteByCorpid(authCorpId);
                    authCorpUserRepository.deleteByCorpid(authCorpId);
                    WxApiUtil.removeCachedPermanentCode(authCorpId);
                    WxApiUtil.removeCachedAccessToken(authCorpId);
                    break;
                case "change_contact":
                    logger.debug("通讯录变更事件通知");
                    String changeType = wxMessage.getChangeType();
                    String userID = wxMessage.getUserID();
                    Long partyId = wxMessage.getId(); // 部门id，仅在推扫部门事件时有用
                    switch (changeType) {
                        case "create_user":
                            AuthCorpUserDetail authCorpUserDetail = new AuthCorpUserDetail();
                            BeanUtils.copyProperties(wxMessage, authCorpUserDetail, "id");
                            authCorpUserDetailRepository.save(authCorpUserDetail);
                            break;
                        case "update_user":
                            authCorpId = wxMessage.getAuthCorpId();
                            Optional<AuthCorpUserDetail> optionalAuthCorpUserDetail = authCorpUserDetailRepository.findFirstByAuthCorpIdAndUserID(authCorpId, userID);
                            if (optionalAuthCorpUserDetail.isPresent()) {
                                AuthCorpUserDetail authCorpUserDetailDB = optionalAuthCorpUserDetail.get();
                                BeanUtils.copyProperties(wxMessage, authCorpUserDetailDB, "id");
                                String newUserID = wxMessage.getNewUserID();
                                if (!StringUtils.isEmpty(newUserID)) {
                                    logger.debug("新的UserID=[{}]，变更时推送", newUserID);

                                }
                                authCorpUserDetailDB.setUpdated_at(new Date());
                                authCorpUserDetailRepository.save(authCorpUserDetailDB);
                            }
                            break;
                        case "delete_user":
                            userID = wxMessage.getUserID();
                            authCorpId = wxMessage.getAuthCorpId();
                            authCorpUserDetailRepository.deleteByAuthCorpIdAndUserID(authCorpId, userID);
                            break;
                        case "create_party":
                            partyId = wxMessage.getId();
                            AuthCorpPartyDetail authCorpPartyDetail = new AuthCorpPartyDetail();
                            BeanUtils.copyProperties(wxMessage, authCorpPartyDetail, "id");
                            authCorpPartyDetail.setPartyId(partyId);
                            authCorpPartyDetailRepository.save(authCorpPartyDetail);
                            break;
                        case "update_party":
                            partyId = wxMessage.getId();
                            authCorpId = wxMessage.getAuthCorpId();
                            Optional<AuthCorpPartyDetail> optionalAuthCorpPartyDetail = authCorpPartyDetailRepository.findFirstByAuthCorpIdAndPartyId(authCorpId, partyId);
                            if(optionalAuthCorpPartyDetail.isPresent()) {
                                AuthCorpPartyDetail authCorpPartyDetailDB = optionalAuthCorpPartyDetail.get();
                                //Name	部门名称，仅当该字段发生变更时传递
                                //ParentId	父部门id，仅当该字段发生变更时传递
                                String name = wxMessage.getName();
                                if(!StringUtils.isEmpty(name)) {
                                    authCorpPartyDetailDB.setName(name);
                                }
                                String parentId = wxMessage.getParentId();
                                if(!StringUtils.isEmpty(parentId)) {
                                    authCorpPartyDetailDB.setParentId(parentId);
                                }
                                authCorpPartyDetailDB.setUpdated_at(new Date());
                                authCorpPartyDetailRepository.save(authCorpPartyDetailDB);
                            }
                            break;
                        case "delete_party":
                            partyId = wxMessage.getId();
                            authCorpId = wxMessage.getAuthCorpId();
                            authCorpPartyDetailRepository.deleteByAuthCorpIdAndPartyId(authCorpId, partyId);
                            break;
                        case "update_tag":
                            // TODO: 暂未处理
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new BizException("POST指令回调异常", e);
        }
        return "success";
    }

}
