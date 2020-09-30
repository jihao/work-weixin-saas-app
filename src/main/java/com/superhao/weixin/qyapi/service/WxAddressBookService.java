package com.superhao.weixin.qyapi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.superhao.weixin.qyapi.entity.WeChatDepartment;
import com.superhao.weixin.qyapi.entity.WeChatUser;
import com.superhao.weixin.qyapi.repository.WxDepartmentRepository;
import com.superhao.weixin.qyapi.repository.WxUserRepository;
import com.superhao.weixin.qyapi.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WxAddressBookService {
    @Autowired
    private WxUserRepository wxUserRepository;
    @Autowired
    private WxDepartmentRepository wxDepartmentRepository;

    public void deleteWxDepartmentByCorpId(String corpId) {
        wxDepartmentRepository.deleteByCorpid(corpId);
    }

    public void deleteWxUserByCorpId(String corpId) {
        wxUserRepository.deleteByCorpid(corpId);
    }

    public Iterable<WeChatDepartment> fetchDepartmentList(String corpId, String accessToken) {
        log.info("fetchDepartmentList corpId = {}, accessToken = {}", corpId, accessToken);
        List<WeChatDepartment> departmentList = new ArrayList<>();
        JSONObject jsonObject = WxApiUtil.getDepartmentList(accessToken);
        JSONArray jsonArray = jsonObject.getJSONArray("department");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            WeChatDepartment weChatDepartment = obj.toJavaObject(WeChatDepartment.class);
            weChatDepartment.setDeptid(weChatDepartment.getId()); // 数据库字段 deptid 表示部门id，这切换一下
            weChatDepartment.setId(null);
            weChatDepartment.setCorpid(corpId);
            departmentList.add(weChatDepartment);
        }
        log.info("departmentList = {}", departmentList);
        return wxDepartmentRepository.saveAll(departmentList);
    }

    public Iterable<WeChatUser> fetchUserSimpleList(String corpId, String accessToken, String departmentId) {
        log.info("fetchUserSimpleList corpId = {}, accessToken = {}, departmentId = {}", corpId, accessToken, departmentId);
        List<WeChatUser> userList = new ArrayList<>();
        JSONObject jsonObject = WxApiUtil.getUserSimpleList(accessToken, departmentId);
        JSONArray jsonArray = jsonObject.getJSONArray("userlist");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            WeChatUser weChatUser = obj.toJavaObject(WeChatUser.class);
            weChatUser.setCorpid(corpId);
            userList.add(weChatUser);
        }
        log.info("userList = {}", userList);
        return wxUserRepository.saveAll(userList);
    }
}
