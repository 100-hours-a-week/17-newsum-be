package com.akatsuki.newsum.domain.notification.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.akatsuki.newsum.domain.notification.domain.Notification;
import com.akatsuki.newsum.domain.user.entity.User;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long>, NotificationQueryRepository {

	void deleteAllByUser(User user);

	@Query("select n from Notification n where n.id = :id and n.user.id=:userId")
	Optional<Notification> findByIdAndUserId(Long id, Long userId);

	Boolean existsByUserId(Long userId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM Notification n WHERE n.createdAt < :threshold")
	int deleteBefore(@Param("threshold") LocalDateTime threshold);
}
