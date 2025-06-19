package com.akatsuki.newsum.domain.user.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.NotFoundException;
import com.akatsuki.newsum.common.util.DateUtil;
import com.akatsuki.newsum.domain.user.dto.UpdateUserRequestDto;
import com.akatsuki.newsum.domain.user.dto.UpdateUserResponseDto;
import com.akatsuki.newsum.domain.user.entity.User;
import com.akatsuki.newsum.domain.user.repository.UserRepository;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonCardDto;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.RecentView;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final WebtoonRepository webtoonRepository;
	private final UserRepository userRepository;

	public Map<String, List<WebtoonCardDto>> findRecentWebtoonList(Long id) {
		List<RecentView> recentViews = webtoonRepository.findRecentWebtoons(id);
		return recentViews.stream()
			.collect(Collectors.groupingBy(
				recentView -> DateUtil.localDateTimeToDateString(recentView.getViewedAt()),
				() -> new TreeMap<String, List<WebtoonCardDto>>(Comparator.reverseOrder()),
				Collectors.mapping(this::mapToWebtoonCardDto, Collectors.toList())
			));
	}

	private WebtoonCardDto mapToWebtoonCardDto(RecentView recentView) {
		return new WebtoonCardDto(
			recentView.getWebtoon().getId(),
			recentView.getWebtoon().getTitle(),
			recentView.getWebtoon().getThumbnailImageUrl(),
			recentView.getWebtoon().getCreatedAt(),
			recentView.getWebtoon().getViewCount()
		);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCodeAndMessage.USER_NOT_FOUND));
	}

	@Transactional
	public UpdateUserResponseDto updateUser(Long userId, UpdateUserRequestDto dto) {
		User user = findUserById(userId);
		user.updateNickname(dto.nickname());
		user.profileImageUrl(dto.profileImage());

		return new UpdateUserResponseDto(
			user.getNickname(),
			user.getProfileImageUrl()
		);
	}

}
