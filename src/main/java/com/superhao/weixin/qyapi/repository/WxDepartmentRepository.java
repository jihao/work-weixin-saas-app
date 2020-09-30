package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.WeChatDepartment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WxDepartmentRepository extends CrudRepository<WeChatDepartment, Long> {

    @Transactional
    void deleteByCorpidAndAgentidAndId(String corpid, Long agentid, String id);

    @Transactional
    void deleteByCorpidAndId(String corpid, String id);

    @Transactional
    void deleteByCorpid(String corpid);
}
