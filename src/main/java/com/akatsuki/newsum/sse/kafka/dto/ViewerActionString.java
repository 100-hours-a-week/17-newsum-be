package com.akatsuki.newsum.sse.kafka.dto;

import com.akatsuki.newsum.common.enums.EnumString;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ViewerActionString extends EnumString<ViewerAction> {
	public ViewerActionString(String action) {
		super(action);
	}

	@JsonCreator
	public static ViewerActionString from(String action) {
		return new ViewerActionString(action);
	}
}
