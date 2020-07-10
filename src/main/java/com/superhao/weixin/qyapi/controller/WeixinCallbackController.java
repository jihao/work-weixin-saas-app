package com.superhao.weixin.qyapi.controller;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.superhao.weixin.qyapi.WxApiUtil;
import com.superhao.weixin.qyapi.config.WorkWeixinProperties;
import com.superhao.weixin.qyapi.model.BizException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callback")
public class WeixinCallbackController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${wechat.cp.suiteId}")
    private String suite_id;

    @Autowired
    private WorkWeixinProperties workWeixinProperties;

    @Autowired
    private WorkWeixinService workWeixinService;
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
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWeixinProperties.getToken(), workWeixinProperties.getAesKey(), workWeixinProperties.getCorpId());
            // 通对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);

            logger.debug("GET数据明文[{}]", sEchoStr);
            return sEchoStr;
        } catch (Exception e) {
            throw new BizException("GET数据回调验证URL失败", e);
        }
    }

    /**
     * 数据回调URL
     * <pre>
     * 用于接收托管企业微信应用的用户消息和用户事件。
     * URL支持使用$CORPID$模板参数表示corpid，推送事件时企业微信会自动将其替换为授权企业的corpid。
     * (关于如何回调，请参考接收消息 https://work.weixin.qq.com/api/doc/10514。
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
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWeixinProperties.getToken(), workWeixinProperties.getAesKey(), corpId);
            String info = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, body);
            // logger.info( "POST解密明文info[{}]", info );
            //XML文件解析
            Document documentInfo = DocumentHelper.parseText(info);
            Element root = documentInfo.getRootElement();
            //订阅消息
            String event = root.elementTextTrim("Event");

            if ("subscribe".equals(event)) {
                //第三方服务商id
                String toUserName = root.elementTextTrim("ToUserName");
                //账号
                String fromUserName = root.elementTextTrim("FromUserName");
                //应用ID
                String agentID = root.elementTextTrim("AgentID");

                workWeixinService.createAuthorizedCorp(toUserName, fromUserName, agentID);
            } else if ("unsubscribe".equals(event)) {
                //第三方服务商id
                String toUserName = root.elementTextTrim("ToUserName");
                //账号
                String fromUserName = root.elementTextTrim("FromUserName");
                //应用ID
                String agentID = root.elementTextTrim("AgentID");
                workWeixinService.deleteAuthorizedCorp(toUserName, fromUserName, agentID);
            }
        } catch (Exception e) {
            throw new BizException("POST数据回调异常", e);
        }
        return "success";
    }

    /*
     * @param request
     * @return
     * @throws AesException
     */
    @RequestMapping(path = "/suite/receive", method = RequestMethod.GET)
    public String suiteReceiveGet(
            @RequestParam(name = "msg_signature", required = false) String msgSignature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        //get请求是服务商本身的corpid来解密
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWeixinProperties.getToken(), workWeixinProperties.getAesKey(), workWeixinProperties.getCorpId());
            logger.debug("GET指令回调签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
            String url = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
            logger.debug("GET指令明文[{}]", url);
            return url;
        } catch (Exception e) {
            throw new BizException("GET指令回调验证URL失败", e);
        }
    }

    /**
     * 指令回调URL 第三方回调协议文档: https://work.weixin.qq.com/api/doc/10982 <br/>
     *
     * <pre>
     * 在发生授权、通讯录变更、ticket变化等事件时，企业微信服务器会向应用的“指令回调URL”推送相应的事件消息。消息结构体将使用创建应用时的EncodingAESKey进行加密（特别注意, 在第三方回调事件中使用加解密算法，receiveid的内容为suiteid），请参考接收消息解析数据包。
     *
     * 本章节的回调事件，服务商在收到推送后都必须直接返回字符串 “success”，若返回值不是 “success”，企业微信会把返回内容当作错误信息。
     *
     * 以下各个事件皆假设指令回调URL设置为：https://127.0.0.1/suite/receive
     *
     * 收到的数据包中ToUserName为产生事件的SuiteId，AgentID为空
     *
     * 以下各个事件的xml包仅是接收的数据包中的Encrypt参数解密后的内容说明
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
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(workWeixinProperties.getToken(), workWeixinProperties.getAesKey(), workWeixinProperties.getSuiteId());
            logger.debug("POST指令回调URL签名[{}], 时间戳[{}], 随机数[{}], 随机字符串[{}]", msgSignature, timestamp, nonce, echostr);
            String info = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, body);
            logger.debug("POST解密明文[{}]", info);
            //XML文件解析
            Document documentInfo = DocumentHelper.parseText(info);
            Element root = documentInfo.getRootElement();
            String suiteTicketConfirm = root.elementTextTrim("InfoType");

            if ("suite_ticket".equals(suiteTicketConfirm)) {
                String suiteTicket = root.elementTextTrim("SuiteTicket");
                /* 第三方凭证 */
                WxApiUtil.getSuiteToken(suiteTicket);
                logger.info("Refresh ticket.");

            } else if ("create_auth".equals(suiteTicketConfirm)) {
                String suiteAuthCode = root.elementTextTrim("AuthCode");
                // logger.info( "授权通知事件,获取企业的永久授权码[{}]", suiteAuthCode );
                //登录,只能获取一次,要用里面的值要保存数据库
                if (!suiteAuthCode.isEmpty()) {
                    RootPermanentAuthorizationCode code = WxApiUtil.permanentRequest(suiteAuthCode);
                    // logger.info( "企业授权通知[{}]", code.getDealer_corp_info().getCorp_name() );
                    workWeixinService.saveRootPermanentAuthorizationCode(code);
                }

            } else if ("cancel_auth".equals(suiteTicketConfirm)) {
                String authCorpId = root.elementTextTrim("AuthCorpId");
                workWeixinService.deleteAuthorizedCorp(authCorpId);
                workWeixinService.deleteAuthorizedCorpEmployees(authCorpId);
            }
        } catch (Exception e) {
            throw new BizException("POST指令回调异常", e);
        }
        return "success";

    }

}
