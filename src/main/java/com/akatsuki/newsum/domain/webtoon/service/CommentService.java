package com.akatsuki.newsum.domain.webtoon.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.event.Events;
import com.akatsuki.newsum.common.pagination.CursorPaginationService;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.page.CursorPage;
import com.akatsuki.newsum.domain.notification.presentation.dto.ReplyNotificationEvent;
import com.akatsuki.newsum.domain.webtoon.dto.CommentAndSubComments;
import com.akatsuki.newsum.domain.webtoon.dto.CommentCreateRequest;
import com.akatsuki.newsum.domain.webtoon.dto.CommentEditRequest;
import com.akatsuki.newsum.domain.webtoon.dto.CommentListResult;
import com.akatsuki.newsum.domain.webtoon.dto.CommentReadDto;
import com.akatsuki.newsum.domain.webtoon.dto.CommentResult;
import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.Comment;
import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.CommentLike;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.exception.CommentForbiddenException;
import com.akatsuki.newsum.domain.webtoon.exception.CommentNotFoundException;
import com.akatsuki.newsum.domain.webtoon.exception.WebtoonNotFoundException;
import com.akatsuki.newsum.domain.webtoon.repository.CommentLikeRepository;
import com.akatsuki.newsum.domain.webtoon.repository.CommentRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final WebtoonRepository webtoonRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final CursorPaginationService cursorPaginationService;

	public CommentListResult findCommentsByWebtoon(Long webtoonId, Cursor cursor, Integer size,
		Long id) {
		List<CommentReadDto> allParentComments = commentRepository.findParentCommentsByCursorAndSize(webtoonId, cursor,
			size);
		List<Long> parentCommentIds = getParentCommentIds(allParentComments);
		List<CommentReadDto> allSubComments = commentRepository.findByWebtoonIdAndParentCommentIdIn(webtoonId,
			parentCommentIds);

		List<Long> allCommentIds = Stream.concat(
			allParentComments.stream().map(CommentReadDto::getId),
			allSubComments.stream().map(CommentReadDto::getId)
		).toList();

		Set<Long> likedCommentIds = commentLikeRepository.findLikedCommentIdsByUserIdAndCommentIds(id, allCommentIds);
		List<CommentResult> parentCommentResult = getParentCommentResults(id, allParentComments, likedCommentIds);
		Map<Long, List<CommentResult>> subCommentsGroupByParentId = collectSubCommentResultByParentId(allSubComments,
			id, likedCommentIds);
		List<CommentAndSubComments> commentAndSubComments = mergeParentAndSubComments(parentCommentResult,
			subCommentsGroupByParentId);
		Long commentCount = commentRepository.countCommentsByWebtoonId(webtoonId);
		CursorPage<CommentAndSubComments> cursorPage = cursorPaginationService.create(commentAndSubComments, size,
			cursor);
		return new CommentListResult(cursorPage, commentCount);
	}

	@Transactional
	public void addComment(CommentCreateRequest request, Long webtoonId, Long id) {
		Webtoon webtoon = findWebtoonById(webtoonId);
		if (!webtoon.getId().equals(webtoonId)) {
			throw new WebtoonNotFoundException(ErrorCodeAndMessage.WEBTOON_NOT_FOUND);
		}
		Long parentId = setParentId(request.parentId());
		Comment newComment = new Comment(id, webtoonId, parentId, request.content());
		newComment = commentRepository.save(newComment);

		publishReplyNotification(newComment);
	}

	private void publishReplyNotification(Comment comment) {
		if (comment.isParent()) {
			return;
		}
		Long userId = commentRepository.findCommentUserIdById(comment.getId());
		Events.publish(new ReplyNotificationEvent(userId, comment.getId()));
	}

	@Transactional
	public void editComment(CommentEditRequest request, Long webtoonId, Long commentId, Long id) {
		Comment comment = findCommentById(commentId);
		checkAuthorOfComment(id, comment);
		checkWebtoonOfComment(webtoonId, comment);
		comment.editComment(request.content());
	}

	@Transactional
	public void deleteComment(Long webtoonId, Long commentId, Long id) {
		Comment comment = findCommentById(commentId);
		checkAuthorOfComment(id, comment);
		checkWebtoonOfComment(webtoonId, comment);
		commentRepository.delete(comment);
	}

	@Transactional
	public void toggleCommentLike(Long userId, Long commentId) {
		Comment comment = findCommentById(commentId);
		commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
			.ifPresentOrElse(
				existing -> {
					commentLikeRepository.delete(existing);
					comment.decrementLikeCount();
				},
				() -> {
					commentLikeRepository.save(new CommentLike(userId, commentId));
					comment.incrementLikeCount();
				}
			);
	}

	private List<Long> getParentCommentIds(List<CommentReadDto> parentComments) {
		return parentComments.stream()
			.map(CommentReadDto::getId)
			.toList();
	}

	private List<CommentResult> getParentCommentResults(Long id, List<CommentReadDto> parentComments,
		Set<Long> likedCommentIds) {
		return parentComments.stream()
			.map(commentReadDto -> mapToCommentResultWithOwnerAndLiked(id, commentReadDto, likedCommentIds))
			.toList();
	}

	private Map<Long, List<CommentResult>> collectSubCommentResultByParentId(List<CommentReadDto> allSubComments,
		Long userId, Set<Long> likedCommentIds) {
		return allSubComments.stream()
			.collect(Collectors.groupingBy(
				CommentReadDto::getParentId,
				Collectors.mapping(sub -> mapToCommentResultWithOwnerAndLiked(userId, sub, likedCommentIds),
					Collectors.toList())
			));
	}

	private List<CommentAndSubComments> mergeParentAndSubComments(List<CommentResult> parentCommentResult,
		Map<Long, List<CommentResult>> subCommentsGroupByParentId) {
		return parentCommentResult.stream()
			.map(parent -> collectCommentAndSubCommentsByParent(parent, subCommentsGroupByParentId))
			.toList();
	}

	private CommentAndSubComments collectCommentAndSubCommentsByParent(CommentResult parent,
		Map<Long, List<CommentResult>> subCommentsGroupByParentId) {
		List<CommentResult> childComments = subCommentsGroupByParentId.getOrDefault(parent.id(), List.of());
		return CommentAndSubComments.from(parent, childComments);
	}

	private CommentResult mapToCommentResultWithOwnerAndLiked(Long id, CommentReadDto commentReadDto,
		Set<Long> likedCommentIds) {
		boolean isOwner = commentReadDto.getAuthorId().equals(id);
		boolean isLiked = likedCommentIds.contains(commentReadDto.getId());
		long likecount = commentReadDto.getLikeCount();
		return CommentResult.of(commentReadDto, isLiked, isOwner, likecount);
	}

	private long getCommentLikeCount(Long commentId) {
		return commentLikeRepository.countByCommentId(commentId);
	}

	private Long setParentId(Long id) {
		return id == null || id == 0 ? null : id;
	}

	private void checkAuthorOfComment(Long id, Comment comment) {
		if (!comment.getUserId().equals(id)) {
			throw new CommentForbiddenException();
		}
	}

	private void checkWebtoonOfComment(Long webtoonId, Comment comment) {
		if (!comment.getWebtoonId().equals(webtoonId)) {
			throw new CommentNotFoundException();
		}
	}

	private Comment findCommentById(Long id) {
		return commentRepository.findById(id)
			.orElseThrow(CommentNotFoundException::new);
	}

	private Webtoon findWebtoonById(Long webtoonId) {
		return webtoonRepository.findById(webtoonId)
			.orElseThrow(WebtoonNotFoundException::new);
	}
}
