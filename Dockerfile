# 베이스 이미지: OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너로 복사
COPY build/libs/file-service.jar file-service.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "file-service.jar", "--spring.profiles.active=prod"]