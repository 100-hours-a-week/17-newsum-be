package com.akatsuki.newsum.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebtoonViewLogRepository extends
	JpaRepository<com.akatsuki.newsum.domain.log.entity.WebtoonViewLog, Long> {

}
