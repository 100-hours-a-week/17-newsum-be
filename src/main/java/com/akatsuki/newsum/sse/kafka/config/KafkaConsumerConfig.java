package com.akatsuki.newsum.sse.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<String, WebtoonViewerEvent> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "webtoon-viewer-group");
		//가장 오래된 메세지부터 소비
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		JsonDeserializer<WebtoonViewerEvent> valueDeserializer =
			new JsonDeserializer<>(WebtoonViewerEvent.class, false);

		return new DefaultKafkaConsumerFactory<>(
			config,
			new StringDeserializer(),
			valueDeserializer
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, WebtoonViewerEvent> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, WebtoonViewerEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
