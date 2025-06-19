package com.akatsuki.newsum.domain.webtoon.exception;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;

public class ImageGenerationException extends BusinessException {

	public ImageGenerationException(ErrorCodeAndMessage errorCodeAndMessage) {
		super(errorCodeAndMessage);
	}
}
