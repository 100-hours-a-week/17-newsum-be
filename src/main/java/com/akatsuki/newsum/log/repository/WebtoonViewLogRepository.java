package com.akatsuki.newsum.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.log.entity.WebtoonViewLog;

public interface WebtoonViewLogRepository extends
	JpaRepository<WebtoonViewLog, Long> {

}
