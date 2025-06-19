package com.akatsuki.newsum.domain.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.akatsuki.newsum.common.entity.BaseTimeEntity;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Keyword;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.KeywordFavorite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", nullable = false, length = 100)
	private String email;

	@Column(name = "nickname", nullable = false, length = 20)
	private String nickname;

	@Column(name = "profile_image_url", nullable = false, length = 1000)
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private Status status;

	private LocalDateTime deletedAt;

	@Builder
	public User(String email, String nickname, String profileImageUrl, UserRole role, Status status) {
		this.email = email;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.role = role;
		this.status = status;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void profileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public User(Long id) {
		this.id = id;
	}

	public KeywordFavorite subscribeKeyword(Keyword keyword) {
		return new KeywordFavorite(this.id, keyword);
	}
}
