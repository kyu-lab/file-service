package kyulab.fileservice.controller;

import kyulab.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@GetMapping("/image/{imageId}")
	public Mono<ResponseEntity<Flux<DataBuffer>>> getPostImage(@PathVariable String imageId) {
		return fileService.getImage(fileService.getPostImage(imageId));
	}

	@GetMapping("/user/image/{imageId}")
	public Mono<ResponseEntity<Flux<DataBuffer>>> getUserImage(@PathVariable String imageId) {
		return fileService.getImage(fileService.getUserImgPath(imageId));
	}

	@PostMapping("/upload/user/image")
	public Mono<ResponseEntity<String>> uploadUserImage(
			@RequestPart("file") Mono<FilePart> file,
			@RequestParam("userId") String userId) {
		return fileService.uploadResponse(fileService.saveUserImg(userId, file));

	}

	@PostMapping("/upload/post/image")
	public Mono<ResponseEntity<String>> uploadPostImage(
			@RequestPart("file") Mono<FilePart> file) {
		return fileService.uploadResponse(fileService.savePostImg(file));
	}

}
