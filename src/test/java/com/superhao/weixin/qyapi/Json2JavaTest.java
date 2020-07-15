package com.superhao.weixin.qyapi;

import com.alibaba.fastjson.JSONObject;
import com.superhao.weixin.qyapi.entity.AuthCorp;
import com.superhao.weixin.qyapi.model.AuthCorpInfo;
import com.superhao.weixin.qyapi.model.AuthInfoAgent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

public class Json2JavaTest {
    @Test
    public void testJson2JavaCase1() {
        String jsonStr = "{\n" +
                "    \"errcode\":0 ,\n" +
                "    \"errmsg\":\"ok\" ,\n" +
                "    \"access_token\": \"xxxxxx\", \n" +
                "    \"expires_in\": 7200, \n" +
                "    \"permanent_code\": \"xxxx\", \n" +
                "    \"auth_corp_info\": \n" +
                "    {\n" +
                "        \"corpid\": \"xxxx\",\n" +
                "        \"corp_name\": \"name\",\n" +
                "        \"corp_type\": \"verified\",\n" +
                "        \"corp_square_logo_url\": \"yyyyy\",\n" +
                "        \"corp_user_max\": 50,\n" +
                "        \"corp_agent_max\": 30,\n" +
                "        \"corp_full_name\":\"full_name\",\n" +
                "        \"verified_end_time\":1431775834,\n" +
                "        \"subject_type\": 1,\n" +
                "        \"corp_wxqrcode\": \"zzzzz\",\n" +
                "        \"corp_scale\": \"1-50人\",\n" +
                "        \"corp_industry\": \"IT服务\",\n" +
                "        \"corp_sub_industry\": \"计算机软件/硬件/信息服务\",\n" +
                "        \"location\":\"广东省广州市\"\n" +
                "    },\n" +
                "    \"auth_info\":\n" +
                "    {\n" +
                "        \"agent\" :\n" +
                "        [\n" +
                "            {\n" +
                "                \"agentid\":1,\n" +
                "                \"name\":\"NAME\",\n" +
                "                \"round_logo_url\":\"xxxxxx\",\n" +
                "                \"square_logo_url\":\"yyyyyy\",\n" +
                "                \"appid\":1,\n" +
                "                \"privilege\":\n" +
                "                {\n" +
                "                    \"level\":1,\n" +
                "                    \"allow_party\":[1,2,3],\n" +
                "                    \"allow_user\":[\"zhansan\",\"lisi\"],\n" +
                "                    \"allow_tag\":[1,2,3],\n" +
                "                    \"extra_party\":[4,5,6],\n" +
                "                    \"extra_user\":[\"wangwu\"],\n" +
                "                    \"extra_tag\":[4,5,6]\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"agentid\":2,\n" +
                "                \"name\":\"NAME2\",\n" +
                "                \"round_logo_url\":\"xxxxxx\",\n" +
                "                \"square_logo_url\":\"yyyyyy\",\n" +
                "                \"appid\":5\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"auth_user_info\":\n" +
                "    {\n" +
                "        \"userid\":\"aa\",\n" +
                "        \"name\":\"xxx\",\n" +
                "        \"avatar\":\"http://xxx\"\n" +
                "    }\n" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        AuthCorp authCorp = jsonObject.toJavaObject(AuthCorp.class);
        System.out.println(authCorp.toString());

        JSONObject authCorpInfoJson = jsonObject.getJSONObject("auth_corp_info");
        AuthCorpInfo authCorpInfo = authCorpInfoJson.toJavaObject(AuthCorpInfo.class);
        System.out.println(authCorpInfo.toString());

        JSONObject authInfoJson = jsonObject.getJSONObject("auth_info");
        JSONObject authInfoAgentJson = authInfoJson.getJSONArray("agent").getJSONObject(0);
        AuthInfoAgent authInfoAgent = authInfoAgentJson.toJavaObject(AuthInfoAgent.class);
        System.out.println(authInfoAgent.toString());

        BeanUtils.copyProperties(authCorpInfo, authCorp);
        BeanUtils.copyProperties(authInfoAgent, authCorp);

        System.out.println(authCorp.toString());

    }
}
