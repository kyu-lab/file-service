package kyulab.fileservice.dto.kafka;

import java.io.Serial;
import java.io.Serializable;

public record UserImgDto(long userId, String userImgPath) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
}
