package com.akatsuki.newsum.domain.notification.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.domain.notification.domain.Notification;
import com.akatsuki.newsum.domain.notification.infrastructure.persistence.dto.NotificationReadModel;
import com.akatsuki.newsum.domain.user.entity.User;

public interface NotificationRepository {
	List<NotificationReadModel> findByUserWithCursorAndSize(User user, Cursor cursor, int size);

	Notification save(Notification notification);

	void deleteAllByUser(User user);

	void delete(Notification notification);

	Optional<Notification> findNotificationByIdAndUserById(Long id, Long userId);

	Boolean existByUserId(Long userId);

	int deleteBefore(LocalDateTime threshold);
}
