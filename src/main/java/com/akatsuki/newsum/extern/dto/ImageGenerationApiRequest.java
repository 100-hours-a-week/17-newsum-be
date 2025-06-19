package com.akatsuki.newsum.extern.dto;

import java.util.List;
import java.util.Map;

public record ImageGenerationApiRequest(
	String workId,
	Long aiAuthorId,
	List<String> keyword,
	String category,
	String title,
	String reportUrl,
	String content,
	String description1,
	String description2,
	String description3,
	String description4,
	List<Map<String, Object>> imagePrompts
) {
}
