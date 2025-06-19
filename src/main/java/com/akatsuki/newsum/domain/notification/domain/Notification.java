package com.akatsuki.newsum.domain.notification.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.akatsuki.newsum.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "content", nullable = false, length = 100)
	private String content;

	@Column(name = "reference_id", nullable = false)
	private Long referenceId;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "notification_type", nullable = false, length = 20)
	private NotificationType notificationType;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	public Notification(User user, String title, String content, Long referenceId, NotificationType notificationType) {
		this.user = user;
		this.title = title;
		this.content = content;
		this.referenceId = referenceId;
		this.notificationType = notificationType;
		this.isRead = false;
	}

	public static Notification fromReply(User user, String title, String content, Long referenceId) {
		return new Notification(user, title, content, referenceId, NotificationType.REPLY);
	}

	public String getNotificationType() {
		return this.notificationType.name();
	}

	public void read() {
		this.isRead = true;
	}
}
