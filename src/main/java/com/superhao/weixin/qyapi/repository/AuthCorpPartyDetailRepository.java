package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.AuthCorpPartyDetail;
import com.superhao.weixin.qyapi.entity.AuthCorpUserDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthCorpPartyDetailRepository extends CrudRepository<AuthCorpPartyDetail, Long> {

    Optional<AuthCorpPartyDetail> findFirstByAuthCorpIdAndPartyId(String authCorpId, Long partyId);

    void deleteByAuthCorpIdAndPartyId(String authCorpId, Long partyId);
}
