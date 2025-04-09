package kyulab.fileservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.codec.multipart.FilePart;

import java.time.LocalDateTime;

/**
 * 게시글에 첨부된 이미지
 */
@Getter
@NoArgsConstructor
@Document(collection = "post_img")
public class PostImg extends BaseFileEntity {

	private boolean isTemp;

	private LocalDateTime createdAt;

	public PostImg(String fileId, FilePart file, String filePath) {
		super(fileId, file, filePath, "/image/");
		isTemp = true;
		createdAt = LocalDateTime.now();
	}

	public void completeUpload() {
		this.isTemp = false;
	}

}
