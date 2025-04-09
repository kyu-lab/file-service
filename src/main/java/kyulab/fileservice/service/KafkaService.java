package kyulab.fileservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

	private final ObjectMapper objectMapper;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final FileKafkaService fileKafkaService;

	public Mono<Void> sendMsg(String topic, Object msg) {
		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, msg);
		return Mono.fromFuture(future)
				.doOnSuccess(result -> log.info("Kafka 메시지 전송 성공: topic={}, msg={}", topic, msg))
				.doOnError(e -> {
					log.error("Kafka 전송 실패... topic={}, msg={}", topic, msg);
					log.error("에러 메시지: {}", e.getMessage());
				})
				.then()
				.onErrorMap(e -> new IllegalArgumentException("Kafka 전송 실패: " + e.getMessage(), e));
	}

	@KafkaListener(topics = "post-save", groupId = "file-group")
	public void consumeUserImg(ConsumerRecord<String, String> record) {
		try {
			List<String> tempImgList = objectMapper.readValue(
					record.value(),
					new TypeReference<>() {}
			);
			fileKafkaService.completeImageUpload(tempImgList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
