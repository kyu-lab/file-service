FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/file-service.jar file-service.jar
ENTRYPOINT ["java", "-jar", "file-service.jar", "--spring.profiles.active=prod"]