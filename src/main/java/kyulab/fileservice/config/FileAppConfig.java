package kyulab.fileservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileAppConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
