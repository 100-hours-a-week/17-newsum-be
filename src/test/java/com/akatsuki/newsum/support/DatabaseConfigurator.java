package com.akatsuki.newsum.support;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.aiAuthor.repository.AiAuthorRepository;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.NewsSource;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonDetail;
import com.akatsuki.newsum.domain.webtoon.repository.NewsSourceRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonDetailRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseConfigurator {

	private static final Logger log = LoggerFactory.getLogger(DatabaseConfigurator.class);

	private final WebtoonRepository webtoonRepository;
	private final AiAuthorRepository aiAuthorRepository;
	private final NewsSourceRepository newsSourceRepository;
	private final WebtoonDetailRepository webtoonDetailRepository;
	private final JdbcTemplate jdbcTemplate;
	private final DataSource dataSource;

	private List<String> tableNames;

	public DatabaseConfigurator(
		WebtoonRepository webtoonRepository,
		AiAuthorRepository aiAuthorRepository,
		NewsSourceRepository newsSourceRepository,
		WebtoonDetailRepository webtoonDetailRepository,
		DataSource dataSource,
		JdbcTemplate jdbcTemplate
	) {
		this.webtoonRepository = webtoonRepository;
		this.aiAuthorRepository = aiAuthorRepository;
		this.newsSourceRepository = newsSourceRepository;
		this.webtoonDetailRepository = webtoonDetailRepository;
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	public void afterPropertiesSet() {
		this.tableNames = new ArrayList<>();
		extractTableNames();
	}

	/**
	 * 각 테스트 수행 후 저장된 데이터를 전부 삭제하는 메서드
	 * 테스트 케이스별 독립 환경 유지를 위한 용도
	 */
	public void clear() {
		try {
			clearAllTables();
			log.debug("[DatabaseConfigurator] 모든 테이블 데이터 삭제 완료");
		} catch (Exception e) {
			log.error("[DatabaseConfigurator] 테이블 데이터 삭제 중 오류 발생", e);
			throw new RuntimeException("테스트 데이터 초기화 실패", e);
		}
	}

	/**
	 * DB를 사용하는 테스트에서 초기 데이터 삽입용 메서드
	 */
	@Transactional
	public void loadData() {
		try {
			log.debug("[DatabaseConfigurator] 테스트 데이터 초기화 시작");

			initAiAuthor();
			initWebtoon();

			log.info("[DatabaseConfigurator] 테스트 데이터 초기화 완료");
		} catch (Exception e) {
			log.error("[DatabaseConfigurator] 테스트 데이터 초기화 중 오류 발생", e);
			throw new RuntimeException("테스트 데이터 생성 실패", e);
		}
	}

	/**
	 * PostgreSQL용 테이블 데이터 삭제
	 * RESTART IDENTITY를 사용하여 시퀀스도 함께 초기화
	 */
	private void clearAllTables() {
		// PostgreSQL에서는 RESTART IDENTITY와 CASCADE 옵션 사용
		for (String tableName : tableNames) {
			try {
				jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
				log.debug("[DatabaseConfigurator] 테이블 초기화 완료: {}", tableName);
			} catch (Exception e) {
				log.warn("[DatabaseConfigurator] 테이블 초기화 실패: {} - {}", tableName, e.getMessage());
				// 일부 테이블 초기화 실패해도 계속 진행
			}
		}
	}

	/**
	 * 데이터베이스에서 테이블 목록을 추출하는 메서드
	 * PostgreSQL의 public 스키마에서 일반 테이블만 추출
	 */
	private void extractTableNames() {
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();

			// PostgreSQL의 public 스키마에서 TABLE 타입만 조회
			try (ResultSet tables = metaData.getTables(
				connection.getCatalog(),
				"public",
				null,
				new String[] {"TABLE"}
			)) {
				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					// 시스템 테이블 제외
					if (!tableName.startsWith("pg_") && !tableName.startsWith("information_schema")) {
						this.tableNames.add(tableName);
						log.debug("[DatabaseConfigurator] 테이블 발견: {}", tableName);
					}
				}
			}
		} catch (Exception e) {
			log.error("[DatabaseConfigurator] 테이블 목록 추출 실패", e);
			throw new RuntimeException("데이터베이스 메타데이터 조회 실패", e);
		}
	}

	/**
	 * AI 작가 테스트 데이터 초기화
	 */
	private void initAiAuthor() {
		AiAuthor aiAuthor1 = new AiAuthor(
			"테스트 작가1",
			"유머러스",
			"재미있는 웹툰을 그립니다",
			"https://example.com/author1.jpg",
			LocalDateTime.now()
		);

		AiAuthor aiAuthor2 = new AiAuthor(
			"테스트 작가2",
			"진지함",
			"진지한 웹툰을 그립니다",
			"https://example.com/author2.jpg",
			LocalDateTime.now()
		);

		AiAuthor aiAuthor3 = new AiAuthor(
			"테스트 작가3",
			"창의적",
			"창의적인 웹툰을 그립니다",
			"https://example.com/author3.jpg",
			LocalDateTime.now()
		);

		List<AiAuthor> authors = aiAuthorRepository.saveAll(List.of(aiAuthor1, aiAuthor2, aiAuthor3));
		log.debug("[DatabaseConfigurator] AI 작가 {} 명 생성 완료", authors.size());
	}

	/**
	 * 웹툰 테스트 데이터 초기화
	 */
	private void initWebtoon() {
		List<AiAuthor> aiAuthors = aiAuthorRepository.findAll();
		if (aiAuthors.isEmpty()) {
			log.warn("[DatabaseConfigurator] AI 작가가 없어 웹툰 생성을 건너뜁니다.");
			return;
		}

		List<Webtoon> webtoons = new ArrayList<>();
		List<NewsSource> allNewsSources = new ArrayList<>();
		List<WebtoonDetail> allWebtoonDetails = new ArrayList<>();

		for (int i = 0; i < aiAuthors.size(); i++) {
			AiAuthor author = aiAuthors.get(i);

			for (Category category : Category.values()) {
				Webtoon webtoon = new Webtoon(
					author,
					category,
					category.name() + " 웹툰 제목" + (i + 1),
					category.name() + " 관련 내용입니다",
					"https://example.com/" + category.name().toLowerCase() + (i + 1) + ".jpg"
				);

				// NewsSource 2개 생성
				for (byte j = 1; j <= 2; j++) {
					NewsSource news = new NewsSource(
						webtoon,
						category.name() + " 관련 기사 제목 " + j,
						"https://news.example.com/" + category.name().toLowerCase() + "/" + i + "/" + j
					);
					allNewsSources.add(news);
				}

				// WebtoonDetail 4개 생성
				for (byte j = 1; j <= 4; j++) {
					WebtoonDetail detail = new WebtoonDetail(
						webtoon,
						"https://images.example.com/" + category.name().toLowerCase() + "/" + i + "/" + j + ".jpg",
						category.name() + " 상세 내용 " + j,
						j
					);
					allWebtoonDetails.add(detail);
				}

				webtoons.add(webtoon);
			}
		}

		webtoonRepository.saveAll(webtoons);
		newsSourceRepository.saveAll(allNewsSources);
		webtoonDetailRepository.saveAll(allWebtoonDetails);
		log.debug("[DatabaseConfigurator] 웹툰 {}개, 뉴스 {}개, 상세 {}개 생성 완료",
			webtoons.size(), allNewsSources.size(), allWebtoonDetails.size());
	}

	/**
	 * 테스트 환경 상태 확인
	 */
	public void verifyTestEnvironment() {
		try {
			long aiAuthorCount = aiAuthorRepository.count();
			long webtoonCount = webtoonRepository.count();

			log.info("[DatabaseConfigurator] 테스트 환경 상태 - AI 작가: {} 명, 웹툰: {} 개",
				aiAuthorCount, webtoonCount);

		} catch (Exception e) {
			log.error("[DatabaseConfigurator] 테스트 환경 상태 확인 실패", e);
		}
	}
}
