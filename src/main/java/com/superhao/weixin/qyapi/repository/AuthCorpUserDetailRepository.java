package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.AuthCorpUser;
import com.superhao.weixin.qyapi.entity.AuthCorpUserDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AuthCorpUserDetailRepository extends CrudRepository<AuthCorpUserDetail, Long> {

    Optional<AuthCorpUserDetail> findFirstByAuthCorpIdAndUserID(String authCorpId, String userID);

    @Transactional
    void deleteByAuthCorpIdAndUserID(String authCorpId, String userID);
}
