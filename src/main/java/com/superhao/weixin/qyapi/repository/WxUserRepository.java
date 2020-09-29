package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.AuthCorpUser;
import com.superhao.weixin.qyapi.entity.WxUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WxUserRepository extends CrudRepository<WxUser, Long> {

    @Transactional
    void deleteByCorpidAndAgentidAndUserid(String corpid, Long agentid, String userid);

    @Transactional
    void deleteByCorpidAndUserid(String corpid, String userid);

    @Transactional
    void deleteByCorpid(String corpid);
}
