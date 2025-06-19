package com.akatsuki.newsum.domain.notification.domain;

public enum NotificationType {
	TOP3("실시간 TOP3", "실시간 뉴스가 변경되었습니다!"),
	FAVORITE_KEYWORD("%s", "[%s] 키워드가 포함된 뉴스가 발매되었습니다!"),
	FAVORITE_AI_AUTHOR("%s", "[%s] 작가의 신작이 발표되었습니다!"),
	REPLY("답글이 추가되었습니다!", "%s"),
	COMMENT_LIKE("인기 댓글 알림", "많은 사람들이 댓글에 공감해요!");

	private final String titleFormat;
	private final String descriptionFormat;

	NotificationType(String titleFormat, String descriptionFormat) {
		this.titleFormat = titleFormat;
		this.descriptionFormat = descriptionFormat;
	}

	public String getTitle(Object... elements) {
		return String.format(this.titleFormat, elements);
	}

	public String getDescription(Object... elements) {
		return String.format(this.descriptionFormat, elements);
	}
}
