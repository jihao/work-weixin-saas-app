package com.superhao.weixin.qyapi.sevice;

import com.alibaba.fastjson.JSONObject;
import com.superhao.weixin.qyapi.entity.AuthCorp;
import com.superhao.weixin.qyapi.model.AuthCorpInfo;
import com.superhao.weixin.qyapi.model.AuthInfoAgent;
import org.springframework.beans.BeanUtils;

public class DataConvertUtil {
    public static AuthCorp permanentCodeRespJson2AuthCorpEntity(JSONObject jsonObject) {
        AuthCorp authCorp = jsonObject.toJavaObject(AuthCorp.class);
        //System.out.println(authCorpEntity.toString());

        JSONObject authCorpInfoJson = jsonObject.getJSONObject("auth_corp_info");
        AuthCorpInfo authCorpInfo = authCorpInfoJson.toJavaObject(AuthCorpInfo.class);
        //System.out.println(authCorpInfo.toString());

        JSONObject authInfoJson = jsonObject.getJSONObject("auth_info");
        // 	授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
        JSONObject authInfoAgentJson = authInfoJson.getJSONArray("agent").getJSONObject(0);
        AuthInfoAgent authInfoAgent = authInfoAgentJson.toJavaObject(AuthInfoAgent.class);
        //System.out.println(authInfoAgent.toString());

        BeanUtils.copyProperties(authCorpInfo, authCorp);
        BeanUtils.copyProperties(authInfoAgent, authCorp);
        return authCorp;
    }
}
