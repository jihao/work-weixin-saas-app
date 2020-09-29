package com.superhao.weixin.qyapi.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 第三方API（即接口）如下：
 *
 * 功能	                API名称
 * 获取第三方应用凭证	    get_suite_token
 * 获取预授权码	        get_pre_auth_code
 * 设置授权配置	        set_session_info
 * 获取企业的永久授权码	    get_permanent_code
 * 获取企业access_token	get_corp_token
 * 获取企业授权信息	    get_auth_info
 * 获取应用的管理员列表	    get_admin_list
 * 第三方根据code获取企业成员信息	    getuserinfo3rd
 * 第三方使用user_ticket获取成员详情	getuserdetail3rd
 *
 * 第三方应用在获得企业授权后，服务商可以获取企业access_token，调用企业微信相关的API进行发消息、管理通讯录和应用等操作。
 *
 * 文档: https://work.weixin.qq.com/api/doc/10975
 * </pre>
 */
public class WxApiUtil {
    private static final Logger logger = LoggerFactory.getLogger(WxApiUtil.class);

    private static Map<String, String> CACHE_SUITE_TICKET = Collections.synchronizedMap(new PassiveExpiringMap<String, String>(30, TimeUnit.MINUTES));
    private static Map<String, String> CACHE_SUITE_ACCESS_TOKEN = Collections.synchronizedMap(new PassiveExpiringMap<String, String>(7200, TimeUnit.SECONDS));
    private static Map<String, String> CACHE_PRE_AUTH_TOKEN = Collections.synchronizedMap(new PassiveExpiringMap<String, String>(1200, TimeUnit.SECONDS));

    /**
     * key is corpid (授权方企业微信id)
     */
    private static Map<String, String> CACHE_ACCESS_TOKEN = Collections.synchronizedMap(new PassiveExpiringMap<String, String>(7200, TimeUnit.SECONDS));
    /**
     * key is corpid (授权方企业微信id)
     */
    private static Map<String, String> CACHE_PERMANENT_CODE = Collections.synchronizedMap(new HashMap<String, String>());

    /**
     * 获取第三方应用凭证
     *
     * <pre>
     *     由于第三方服务商可能托管了大量的企业，其安全问题造成的影响会更加严重，故API中除了合法来源IP校验之外，还额外增加了suite_ticket作为安全凭证。
     * 获取suite_access_token时，需要suite_ticket参数。suite_ticket由企业微信后台定时推送给“指令回调URL”，每十分钟更新一次，见推送suite_ticket。
     * suite_ticket实际有效期为30分钟，可以容错连续两次获取suite_ticket失败的情况，但是请永远使用最新接收到的suite_ticket。
     * 通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。
     * </pre>
     *
     * @param suiteId
     * @param suiteSecret
     * @param suiteTicket
     */
    public static String getSuiteAccessToken(String suiteId, String suiteSecret, String suiteTicket) {
        String suiteAccessToken = CACHE_SUITE_ACCESS_TOKEN.getOrDefault("suite_access_token", "");

        if (suiteAccessToken.isEmpty()) {
            JSONObject body = new JSONObject();
            body.put("suite_id", suiteId);
            body.put("suite_secret", suiteSecret);
            body.put("suite_ticket", suiteTicket);

            String result = doPost("https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token", body.toJSONString());
            if (!result.isEmpty()) {
                JSONObject resultJson = JSONObject.parseObject(result);
                suiteAccessToken = resultJson.getString("suite_access_token");
                CACHE_SUITE_ACCESS_TOKEN.put("suite_access_token", suiteAccessToken);

            }
        }

        return suiteAccessToken;
    }

    /**
     * 获取预授权码
     * <p>
     * 该API用于获取预授权码。预授权码用于企业授权时的第三方服务商安全验证。
     *
     * @param suiteAccessToken
     * @return
     */
    public static String getPreAuthCode(String suiteAccessToken) {
        String preAuthCode = CACHE_PRE_AUTH_TOKEN.getOrDefault("pre_auth_code", "");

        if (preAuthCode.isEmpty()) {
            String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=%s";
            String url = String.format(urlTemplate, suiteAccessToken);
            String result = doGet(url);
            if (!result.isEmpty()) {
                JSONObject resultJson = JSONObject.parseObject(result);
                preAuthCode = resultJson.getString("pre_auth_code");
                CACHE_PRE_AUTH_TOKEN.put("pre_auth_code", preAuthCode);
            }
        }
        return preAuthCode;
    }


    /**
     * 获取企业永久授权码
     * <p>
     * 该API用于使用临时授权码换取授权方的永久授权码，并换取授权信息、企业access_token，临时授权码一次有效。
     * <p>
     * 建议第三方以userid为主键，来建立自己的管理员账号。
     *
     * @param suiteAccessToken 第三方应用凭证
     * @param authCode         临时授权码，会在授权成功时附加在redirect_uri中跳转回第三方服务商网站，或通过回调推送给服务商。长度为64至512个字节
     * @return JSONObject
     */
    public static JSONObject getPermanentCode(String suiteAccessToken, String authCode) {
        JSONObject body = new JSONObject();
        body.put("auth_code", authCode);

        String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=%s";
        String url = String.format(urlTemplate, suiteAccessToken);

        String result = doPost(url, body.toJSONString());
        if (!result.isEmpty()) {
            JSONObject resultJson = JSONObject.parseObject(result);
            String permanentCode = resultJson.getString("permanent_code");
            logger.debug("getPermanentCode = [{}]", permanentCode);

            String accessToken = resultJson.getString("access_token");
            JSONObject authCorpInfoJson = resultJson.getJSONObject("auth_corp_info");
            String corpId = authCorpInfoJson.getString("corpid");
            // 获取企业永久授权码 时 保存 access_token 等信息
            CACHE_ACCESS_TOKEN.put(corpId, accessToken);
            CACHE_PERMANENT_CODE.put(corpId, permanentCode);
            return resultJson;
        }
        return new JSONObject(); // avoid null
    }

    /**
     * 获取企业授权信息
     * <p>
     * 该API用于通过永久授权码换取企业微信的授权信息。 永久code的获取，是通过临时授权码使用get_permanent_code 接口获取到的permanent_code。
     * <p>
     * https://work.weixin.qq.com/api/doc/10975#%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9A%E6%8E%88%E6%9D%83%E4%BF%A1%E6%81%AF
     *  @param suiteAccessToken
     * @param authCorpId
     * @param permanentCode
     * @return
     */
    public static JSONObject getAuthInfo(String suiteAccessToken, String authCorpId, String permanentCode) {
        JSONObject body = new JSONObject();
        body.put("auth_corpid", authCorpId);
        body.put("permanent_code", permanentCode);

        String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=%s";
        String url = String.format(urlTemplate, suiteAccessToken);

        String result = doPost(url, body.toJSONString());
        if (!result.isEmpty()) {
            JSONObject resultJson = JSONObject.parseObject(result);

            return resultJson;
        }
        return new JSONObject(); // avoid null
    }

    /**
     * 获取企业授权信息
     * <p>
     * 第三方服务商在取得企业的永久授权码后，通过此接口可以获取到企业的access_token。
     * 获取后可通过通讯录、应用、消息等企业接口来运营这些应用。
     *
     * 此处获得的企业access_token与企业获取access_token拿到的token，本质上是一样的，只不过获取方式不同。获取之后，就跟普通企业一样使用token调用API接口
     * <p>
     * https://open.work.weixin.qq.com/api/doc/10975#%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9Aaccess_token
     *  @param suiteAccessToken
     * @param authCorpId
     * @param permanentCode
     * @return
     */
    public static String getAccessToken(String suiteAccessToken, String authCorpId, String permanentCode) {
        JSONObject body = new JSONObject();
        body.put("auth_corpid", authCorpId);
        body.put("permanent_code", permanentCode);

        String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=%s";
        String url = String.format(urlTemplate, suiteAccessToken);

        String result = doPost(url, body.toJSONString());
        if (!result.isEmpty()) {
            JSONObject resultJson = JSONObject.parseObject(result);
            String accessToken = resultJson.getString("access_token");

            // 获取企业永久授权码 时 保存 access_token 等信息
            CACHE_ACCESS_TOKEN.put(authCorpId, accessToken);
            return accessToken;
        }
        return CACHE_ACCESS_TOKEN.getOrDefault(authCorpId, "");
    }

    /**
     * 获取部门列表
     * @param accessToken
     * @return
     */
    public static JSONObject getDepartmentList(String accessToken) {
        String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=";
        String url = String.format(urlTemplate, accessToken);
        String result = doGet(url);
        if (!result.isEmpty()) {
            JSONObject resultJson = JSONObject.parseObject(result);
            return resultJson;
        }
        return new JSONObject(); // avoid null
    }

    /**
     * 获取部门列表
     * @param accessToken
     * @return
     */
    public static JSONObject getUserSimpleList(String accessToken, String departmentId) {
        // fetch_child	否	是否递归获取子部门下面的成员：1-递归获取，0-只获取本部门
        String urlTemplate = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=%s&department_id=%s&fetch_child=0";
        String url = String.format(urlTemplate, accessToken, departmentId);
        String result = doGet(url);
        if (!result.isEmpty()) {
            JSONObject resultJson = JSONObject.parseObject(result);
            return resultJson;
        }
        return new JSONObject(); // avoid null
    }


    /**
     * @param url
     * @param body
     * @return '' or '{"errcode":0 , "errmsg":"ok" , "expires_in":1200, ... }'
     */
    private static String doPost(String url, String body) {
        String result = "";
        try {
            result = Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param url
     * @return '' or '{"errcode":0 , "errmsg":"ok" , "expires_in":1200, ... }'
     */
    private static String doGet(String url) {
        String result = "";
        try {
            result = Request.Get(url).addHeader("content-type", ContentType.APPLICATION_JSON.toString())
                    .execute().returnContent().asString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 获取缓存的 suite_access_token
     *
     * @return suite_access_token
     */
    public static String getCachedSuiteAccessToken() {
        return CACHE_SUITE_ACCESS_TOKEN.getOrDefault("suite_access_token", "");
    }

    public static String getCachedPermanentCode(String keyAuthCorpId) {
        return CACHE_PERMANENT_CODE.getOrDefault(keyAuthCorpId, "");
    }

    public static void removeCachedPermanentCode(String keyAuthCorpId) {
        CACHE_PERMANENT_CODE.remove(keyAuthCorpId);
    }

    public static void removeCachedAccessToken(String keyAuthCorpId) {
        CACHE_ACCESS_TOKEN.remove(keyAuthCorpId);
    }
}
