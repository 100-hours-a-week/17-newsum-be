package com.akatsuki.newsum.domain.notification.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akatsuki.newsum.common.dto.ApiResponse;
import com.akatsuki.newsum.common.dto.ResponseCodeAndMessage;
import com.akatsuki.newsum.common.pagination.annotation.CursorParam;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.security.UserDetailsImpl;
import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListCommand;
import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListResult;
import com.akatsuki.newsum.domain.notification.application.usecase.NotificationUseCase;
import com.akatsuki.newsum.domain.notification.presentation.dto.NotificationListPageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/notifications")
public class NotificationController {

	private final NotificationUseCase notificationUseCase;

	@GetMapping
	public ResponseEntity<ApiResponse> getNotifications(
		@CursorParam Cursor cursor,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = userDetails.getUserId();

		NotificationPageListResult result = notificationUseCase.lookupNotificationsByUserId(
			new NotificationPageListCommand(cursor, size, userId));

		NotificationListPageResponse response = NotificationListPageResponse.of(result.cursorPage());
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.NOTI_LIST_SUCCESS, response)
		);
	}

	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<ApiResponse> readNotification(
		@PathVariable("notificationId") Long notificationId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = userDetails.getUserId();
		notificationUseCase.readNotification(userId, notificationId);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.NOTI_READ_SUCCESS, null)
		);
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse> deleteAllNotifications(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = userDetails.getUserId();
		notificationUseCase.removeAllNotifications(userId);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{notificationId}")
	public ResponseEntity<ApiResponse> deleteNotification(
		@PathVariable("notificationId") Long notificationId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = userDetails.getUserId();
		notificationUseCase.removeNotification(notificationId, userId);

		return ResponseEntity.noContent().build();
	}
}
