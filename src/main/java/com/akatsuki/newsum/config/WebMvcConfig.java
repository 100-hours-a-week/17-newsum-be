package com.akatsuki.newsum.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.akatsuki.newsum.common.pagination.resolver.CursorArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final CursorArgumentResolver cursorArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(cursorArgumentResolver);
	}

	// 정적 리소스 처리: /static, /favicon.ico 등
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/article/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/comment/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/oauth2/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/login/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/recent/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/category/**")
			.setViewName("forward:/index.html");
		registry.addViewController("/users/**")
			.setViewName("forward:/index.html");
	}
}
