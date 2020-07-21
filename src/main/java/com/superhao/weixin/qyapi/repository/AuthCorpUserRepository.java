package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.AuthCorpUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCorpUserRepository extends CrudRepository<AuthCorpUser, Long> {

    void deleteByCorpidAndAgentidAndUserid(String corpid, Long agentid, String userid);
}
