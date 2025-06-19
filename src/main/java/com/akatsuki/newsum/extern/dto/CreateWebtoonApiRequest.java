package com.akatsuki.newsum.extern.dto;

import java.util.List;
import java.util.Map;

public record CreateWebtoonApiRequest(
	String id,
	List<Map<String, Object>> imagePrompts
) {
}
