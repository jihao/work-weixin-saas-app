package com.superhao.weixin.qyapi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.superhao.weixin.qyapi.entity.WxDepartment;
import com.superhao.weixin.qyapi.entity.WxUser;
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

    public Iterable<WxDepartment> fetchDepartmentList(String corpId, String accessToken) {
        log.info("fetchDepartmentList corpId = {}, accessToken = {}", corpId, accessToken);
        List<WxDepartment> departmentList = new ArrayList<>();
        JSONObject jsonObject = WxApiUtil.getDepartmentList(accessToken);
        JSONArray jsonArray = jsonObject.getJSONArray("department");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            WxDepartment wxDepartment = obj.toJavaObject(WxDepartment.class);
            wxDepartment.setDeptid(wxDepartment.getId()); // 数据库字段 deptid 表示部门id，这切换一下
            wxDepartment.setId(null);
            wxDepartment.setCorpid(corpId);
            departmentList.add(wxDepartment);
        }
        log.info("departmentList = {}", departmentList);
        return wxDepartmentRepository.saveAll(departmentList);
    }

    public Iterable<WxUser> fetchUserSimpleList(String corpId, String accessToken, String departmentId) {
        log.info("fetchUserSimpleList corpId = {}, accessToken = {}, departmentId = {}", corpId, accessToken, departmentId);
        List<WxUser> userList = new ArrayList<>();
        JSONObject jsonObject = WxApiUtil.getUserSimpleList(accessToken, departmentId);
        JSONArray jsonArray = jsonObject.getJSONArray("userlist");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            WxUser wxUser = obj.toJavaObject(WxUser.class);
            wxUser.setCorpid(corpId);
            userList.add(wxUser);
        }
        log.info("userList = {}", userList);
        return wxUserRepository.saveAll(userList);
    }
}
