package com.akatsuki.newsum.extern.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.akatsuki.newsum.extern.dto.CreateWebtoonApiRequest;

import feign.Response;

@FeignClient(name = "ai-server", url = "${ai-server.base-url}")
public interface AiServerApi {

	@PostMapping("/api/v2/images/generate/batch")
	Response createWebtoon(@RequestBody CreateWebtoonApiRequest request);

	@GetMapping("/api/v2/images/health")
	Response healthCheck();
}
