package kyulab.fileservice.service;

import kyulab.fileservice.entity.PostImg;
import kyulab.fileservice.repository.PostImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileKafkaService {

	private final PostImgRepository postImgRepository;

	public Mono<Void> completeImageUpload(List<String> tempImgList) {
		return Flux.fromIterable(tempImgList)
				.flatMap(id ->
					postImgRepository.findById(id)
					.doOnNext(PostImg::completeUpload)
					.flatMap(postImgRepository::save)
				).then()
				.doOnError(e -> log.error("Error : {}", e.getMessage()));
	}

}
