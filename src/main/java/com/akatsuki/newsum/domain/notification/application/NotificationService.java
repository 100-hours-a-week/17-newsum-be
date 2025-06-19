package com.akatsuki.newsum.domain.notification.application;

import static com.akatsuki.newsum.common.dto.ErrorCodeAndMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.event.Events;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.exception.NotFoundException;
import com.akatsuki.newsum.common.pagination.CursorPaginationService;
import com.akatsuki.newsum.common.pagination.model.page.CursorPage;
import com.akatsuki.newsum.common.util.DateUtil;
import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListCommand;
import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListResult;
import com.akatsuki.newsum.domain.notification.application.dto.ReplyNotificationCommand;
import com.akatsuki.newsum.domain.notification.application.dto.WebPushSendEvent;
import com.akatsuki.newsum.domain.notification.application.usecase.NotificationUseCase;
import com.akatsuki.newsum.domain.notification.domain.Notification;
import com.akatsuki.newsum.domain.notification.infrastructure.persistence.NotificationRepository;
import com.akatsuki.newsum.domain.notification.infrastructure.persistence.dto.NotificationReadModel;
import com.akatsuki.newsum.domain.notification.presentation.dto.NotificationResult;
import com.akatsuki.newsum.domain.user.entity.User;
import com.akatsuki.newsum.domain.user.repository.UserRepository;
import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.Comment;
import com.akatsuki.newsum.domain.webtoon.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService implements NotificationUseCase {

	private final NotificationRepository notificationRepository;
	private final CursorPaginationService cursorPaginationService;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

	private final String REPLY_NOTI_DESCRIPTION = "답글이 추가되었습니다!";

	@Override
	public NotificationPageListResult lookupNotificationsByUserId(
		NotificationPageListCommand notificationPageListCommand) {
		User user = findUserByIdOrThrow(notificationPageListCommand.userId());

		List<NotificationReadModel> notifications = notificationRepository.findByUserWithCursorAndSize(
			user,
			notificationPageListCommand.cursor(),
			notificationPageListCommand.size());

		List<NotificationResult> notificationResults = notifications.stream()
			.map(this::mapNotificationReadModelToResult)
			.toList();

		CursorPage<NotificationResult> cursorPage = cursorPaginationService.create(
			notificationResults,
			notificationPageListCommand.size(),
			notificationPageListCommand.cursor());
		return new NotificationPageListResult(cursorPage);
	}

	@Override
	@Transactional
	public void notify(ReplyNotificationCommand command) {
		User user = findUserByIdOrThrow(command.userId());
		Comment comment = findCommentByIdOrThrow(command.commentId());

		Notification notification = notificationRepository.save(
			Notification.fromReply(user, comment.getContent(), REPLY_NOTI_DESCRIPTION, command.commentId()));

		// Web 알림 발송
		Events.publish(new WebPushSendEvent(
			command.userId(),
			notification.getId(),
			notification.getTitle(),
			notification.getContent(),
			notification.getNotificationType(),
			notification.getCreatedAt()
		));

		// Email 알림 발송
		//TODO Email발송 이벤트 발행 필요
		Events.publish(null);
	}

	@Override
	@Transactional
	public void removeAllNotifications(Long userId) {
		User user = findUserByIdOrThrow(userId);
		notificationRepository.deleteAllByUser(user);
	}

	@Override
	@Transactional
	public void removeNotification(Long notificationId, Long userId) {
		Notification notification = findNotificationByIdAndUserById(notificationId, userId);
		notificationRepository.delete(notification);
	}

	@Override
	@Transactional
	public void readNotification(Long userId, Long notificationId) {
		Notification notification = findNotificationByIdAndUserById(notificationId, userId);
		notification.read();
	}

	@Override
	public Boolean hasNotReadNotification(Long userId) {
		return notificationRepository.existByUserId(userId);
	}

	@Override
	@Transactional
	public int deleteBefore7Days() {
		LocalDateTime threshold = DateUtil.beforeDaysFromToday(6);
		return notificationRepository.deleteBefore(threshold);
	}

	private Notification findNotificationByIdAndUserById(Long notificationId, Long userId) {
		return notificationRepository.findNotificationByIdAndUserById(notificationId, userId)
			.orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));
	}

	private NotificationResult mapNotificationReadModelToResult(NotificationReadModel model) {
		return new NotificationResult(
			model.getId(),
			model.getNotificationType().name(),
			model.getTitle(),
			model.getContent(),
			model.getCreatedAt(),
			model.getIsRead()
		);
	}

	private Comment findCommentByIdOrThrow(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException(COMMENT_NOT_FOUND));
	}

	private User findUserByIdOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}
}
