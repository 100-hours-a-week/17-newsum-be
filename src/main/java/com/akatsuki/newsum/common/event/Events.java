package com.akatsuki.newsum.common.event;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;

public class Events {

	public static ApplicationEventPublisher publisher;

	public static void publish(Object event) {
		if (Objects.nonNull(event)) {
			publisher.publishEvent(event);
		}
	}

	public static void setPublisher(ApplicationEventPublisher publisher) {
		Events.publisher = publisher;
	}
}
