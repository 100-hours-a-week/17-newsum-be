package com.akatsuki.newsum.fixture.extern;

import java.util.ArrayList;
import java.util.Map;

import com.akatsuki.newsum.extern.dto.CreateWebtoonApiRequest;

public class AiServerApiFixture {

	public static CreateWebtoonApiRequest createBasicWebtoonRequest() {
		return new CreateWebtoonApiRequest("1", new ArrayList<Map<String, Object>>());
	}
}
