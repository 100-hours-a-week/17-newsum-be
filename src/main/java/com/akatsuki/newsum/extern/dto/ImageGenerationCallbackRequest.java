package com.akatsuki.newsum.extern.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageGenerationCallbackRequest(
	Long requestId,

	@JsonProperty(value = "imagelink")
	List<String> imageLinks
) {
}
