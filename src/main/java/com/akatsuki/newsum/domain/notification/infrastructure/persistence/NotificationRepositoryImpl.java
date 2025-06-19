package com.akatsuki.newsum.domain.notification.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.domain.notification.domain.Notification;
import com.akatsuki.newsum.domain.notification.infrastructure.persistence.dto.NotificationReadModel;
import com.akatsuki.newsum.domain.notification.infrastructure.persistence.jpa.NotificationJpaRepository;
import com.akatsuki.newsum.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

	private final NotificationJpaRepository notificationJpaRepository;

	@Override
	public List<NotificationReadModel> findByUserWithCursorAndSize(User user, Cursor cursor, int size) {
		return notificationJpaRepository.findByUserWithCursorAndSize(user, cursor, size);
	}

	@Override
	public Notification save(Notification notification) {
		return notificationJpaRepository.save(notification);
	}

	@Override
	public void deleteAllByUser(User user) {
		notificationJpaRepository.deleteAllByUser(user);
	}

	@Override
	public void delete(Notification notification) {
		notificationJpaRepository.delete(notification);
	}

	@Override
	public Optional<Notification> findNotificationByIdAndUserById(Long id, Long userId) {
		return notificationJpaRepository.findByIdAndUserId(id, userId);
	}

	@Override
	public Boolean existByUserId(Long userId) {
		return notificationJpaRepository.existsByUserId(userId);
	}

	@Override
	public int deleteBefore(LocalDateTime threshold) {
		return notificationJpaRepository.deleteBefore(threshold);
	}
}
