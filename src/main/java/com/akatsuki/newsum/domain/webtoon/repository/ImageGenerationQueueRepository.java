package com.akatsuki.newsum.domain.webtoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.ImageGenerationQueue;

public interface ImageGenerationQueueRepository extends JpaRepository<ImageGenerationQueue, Long> {
	List<ImageGenerationQueue> findImageGenerationQueueByCompletedAtIsNull();
}
