package kyulab.fileservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.http.codec.multipart.FilePart;

import java.io.File;

@Getter
@NoArgsConstructor
public abstract class BaseFileEntity {

	@Id
	private String id;
	private String fileName;
	private String fileRealName;
	private String filePath;
	private String fileUrl;
	private String contentType;

	public BaseFileEntity(String fileId, FilePart file, String filePath, String fileUrl) {
		this.id = fileId;
		this.fileName = file.filename();
		this.fileRealName = fileId + "_" + file.filename();
		this.filePath = "/uploads" + filePath + File.separator + this.fileRealName;
		this.fileUrl = fileUrl + fileId;
		this.contentType = file.headers().getContentType().toString();
	}

}
