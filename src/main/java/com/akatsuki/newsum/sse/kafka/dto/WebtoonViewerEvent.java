package com.akatsuki.newsum.sse.kafka.dto;

//카프카 메세지로 전송될 DTO, 어떤 웹툰에 누가 입장/이탈했는지 나타냄
public record WebtoonViewerEvent(
	Long webtoonId,
	String clientId,
	ViewerAction action
) {
}
