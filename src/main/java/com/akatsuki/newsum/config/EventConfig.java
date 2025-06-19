package com.akatsuki.newsum.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.akatsuki.newsum.common.event.Events;

@Configuration
public class EventConfig {

	@Bean
	public InitializingBean initializingBean(ApplicationEventPublisher publisher) {
		return () -> Events.setPublisher(publisher);
	}
}
