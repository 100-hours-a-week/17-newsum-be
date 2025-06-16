package com.akatsuki.newsum.common.pagination.query.builder;

import org.springframework.stereotype.Component;

import com.akatsuki.newsum.common.pagination.model.query.QueryEngineType;
import com.akatsuki.newsum.common.pagination.query.base.CreatedAtIdJpqlPageQueryBuilder;
import com.akatsuki.newsum.domain.aiAuthor.entity.QAiAuthor;
import com.querydsl.core.types.dsl.PathBuilder;

@Component
public class AiAuthorPageQueryBuilder extends CreatedAtIdJpqlPageQueryBuilder {

	private final Class<?> domainClass = QAiAuthor.class;
	private final String domainName = "aiAuthor";

	@Override
	protected PathBuilder<?> getPathBuilder() {
		return new PathBuilder<>(domainClass, domainName);
	}

	@Override
	protected Class<?> getDomainClass() {
		return domainClass;
	}

	@Override
	protected QueryEngineType getEngineType() {
		return QueryEngineType.QUERYDSL;
	}
}
