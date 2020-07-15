package com.superhao.weixin.qyapi.repository;

import com.superhao.weixin.qyapi.entity.AuthCorp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthCorpRepository extends CrudRepository<AuthCorp, Long> {

    @Query("select a.permanent_code from AuthCorp a where a.corpid = :corpid")
    List<String> getPermanentCodeByAuthCorpId(@Param("corpid") String authCorpId);

    Optional<AuthCorp> findFirstByCorpid(String corpid);

    void deleteByCorpid(String corpid);
}
