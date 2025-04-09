package kyulab.fileservice.dto;

import kyulab.fileservice.entity.PostImg;
import kyulab.fileservice.entity.UsersImg;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class FileData {
	private final Path path;

	private final String contentType;

	private String fileName;

	private FileData(Path path, String contentType) {
		this.path = path;
		this.contentType = contentType;
	}

	private FileData(Path path, String contentType, String fileName) {
		this.path = path;
		this.contentType = contentType;
		this.fileName = fileName;
	}

	public static FileData of(UsersImg usersImg) {
		return new FileData(Path.of(usersImg.getFilePath()), usersImg.getContentType());
	}

	public static FileData of(PostImg postImg) {
		return new FileData(Path.of(postImg.getFilePath()), postImg.getContentType(), postImg.getFileName());
	}

}
