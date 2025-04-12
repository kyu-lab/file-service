package kyulab.fileservice.service;

import kyulab.fileservice.entity.PostImg;
import kyulab.fileservice.entity.UsersImg;
import kyulab.fileservice.dto.FileData;
import kyulab.fileservice.dto.kafka.UserImgDto;
import kyulab.fileservice.repository.PostImgRepository;
import kyulab.fileservice.repository.UsersImgRepository;
import kyulab.fileservice.service.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final UsersImgRepository usersImgRepository;
	private final PostImgRepository postImgRepository;
	private final KafkaService kafkaService;

	@Value("${file.base-path:/uploads}")
	String baseFilePath;

	public Mono<FileData> getPostImage(String id) {
		return postImgRepository.findById(id)
				.map(FileData::of)
				.switchIfEmpty(Mono.error(new RuntimeException("User image not found")));
	}

	public Mono<FileData> getUserImgPath(String id) {
		return usersImgRepository.findById(id)
				.map(FileData::of)
				.switchIfEmpty(Mono.error(new RuntimeException("User image not found")));
	}

	public Mono<ResponseEntity<Flux<DataBuffer>>> getImage(Mono<FileData> fileDataMono) {
		return fileDataMono.map(imageInfo -> {
			Flux<DataBuffer> data = DataBufferUtils.read(
					imageInfo.getPath(),
					DefaultDataBufferFactory.sharedInstance,
					4096
			);
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(imageInfo.getContentType()))
					.body(data);
		});
	}

	/**
	 * 게시글에 등록된 이미지를 임시저장한다.
	 * @param file 임시 저장할 파일
	 * @return 임시 저장된 파일 경로
	 */
	public Mono<String> savePostImg(Mono<FilePart> file) {
		String fileId = String.valueOf(UUID.randomUUID());
		String filePath = baseFilePath + "/post";
		return saveImage(file, filePath, fileId)
				.flatMap(filePart -> {
					// 파일 물리적 저장 및 메이정보 DB에 저장 후 파일 url 반환
					PostImg postImg = new PostImg(fileId, filePart, filePath);
					return postImgRepository.save(postImg)
							.thenReturn(postImg.getFileUrl());
				})
				.doOnError(e -> log.error("Error : {}", e.getMessage()));
	}

	public Mono<String> saveUserImg(String userId, Mono<FilePart> file) {
		String fileId = String.valueOf(UUID.randomUUID());
		String filePath = baseFilePath + "/user/" + userId;
		return usersImgRepository.deleteByUserId(userId)
				.flatMap(deleted -> {
					// 0. 기존 이미지가 있다면 삭제
					Path delFilePath = Path.of(deleted.getFilePath());
					return Mono.fromCallable(() -> {
						Files.deleteIfExists(delFilePath);
						return deleted;
					}).subscribeOn(Schedulers.boundedElastic());
				})
				.then(saveImage(file, filePath, fileId)
				.flatMap(filePart -> {
					// 1. 파일 물리적 저장 및 메이정보 DB에 저장
					UsersImg usersImg = new UsersImg(
							fileId, userId, filePart, filePath, "/user/image/"
					);
					return usersImgRepository.save(usersImg);
				})
				.flatMap(saved -> {
					// 2. users-service에게 이미지 uri 전달
					long uid = Long.parseLong(saved.getUserId());
					String imgUrl = saved.getFileUrl();
					return kafkaService.sendMsg("user-image-url", new UserImgDto(uid, imgUrl))
							.thenReturn(imgUrl);
				})
				.doOnError(e -> log.error("Error : {}", e.getMessage()))
		).doOnError(e -> log.error("User Image delete Fail : {}", e.getMessage()));
	}

	public Mono<ResponseEntity<String>> uploadResponse(Mono<String> fileUrl) {
		return fileUrl.map(ResponseEntity::ok)
				.onErrorResume(e -> {
					log.debug("uploadResponse error : {}", e.getMessage());
					if (e instanceof IllegalArgumentException) {
						return Mono.just(ResponseEntity
								.status(HttpStatus.BAD_REQUEST)
								.body("Error: " + e.getMessage()));
					}
					return Mono.just(ResponseEntity
							.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Server Error: " + e.getMessage()));
				});
	}

	/**
	 * 파일을 저장한다.
	 * 폴더가 없을 경우 생성한다.
	 * @param file   파일 정보
	 * @param path   파일 경로 (기본값 "/upload")
	 * @param fileId 파일 아이디
	 * @return 파일정보
	 */
	private Mono<FilePart> saveImage(Mono<FilePart> file, String path, String fileId) {
		return file.switchIfEmpty(Mono.error(new IllegalArgumentException("File is required")))
				.flatMap(filePart -> {
					Path dest = Path.of(path);

					// 디렉토리가 없으면 생성, 있으면 무시
					Mono<Void> createDir = Mono.fromCallable(() -> {
						Files.createDirectories(dest);
						return null;
					})
					.subscribeOn(Schedulers.boundedElastic())
					.then()
					.onErrorMap(e -> new RuntimeException("Failed to create directory: " + dest, e));

					Path filePath = dest.resolve(fileId + "_" + filePart.filename());
					return createDir.then(
							filePart.transferTo(filePath).thenReturn(filePart)
					);
				});
	}


}
