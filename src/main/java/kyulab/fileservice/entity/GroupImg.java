package kyulab.fileservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.codec.multipart.FilePart;

/**
 * 게시글에 첨부된 이미지
 */
@Getter
@NoArgsConstructor
@Document(collection = "group_img")
public class GroupImg extends BaseFileEntity {

	private String type;

	private boolean isTemp;

	public GroupImg(String fileId, FilePart file, String filePath, String type) {
		super(fileId, file, filePath, "/image/group/");
		this.isTemp = true;
		this.type = type;
	}

	public void completeUpload() {
		this.isTemp = false;
	}

}
