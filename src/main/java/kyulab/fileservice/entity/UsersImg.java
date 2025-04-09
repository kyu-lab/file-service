package kyulab.fileservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.codec.multipart.FilePart;

/**
 * 사용자 이미지
 */
@Getter
@NoArgsConstructor
@Document(collection = "users_img")
public class UsersImg extends BaseFileEntity {
	private String userId;

	public UsersImg(String fileId, String userId, FilePart file, String filePath, String fileUrl) {
		super(fileId, file, filePath, fileUrl);
		this.userId = userId;
	}
}
