# 베이스 이미지: OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너로 복사
COPY build/libs/users-service.jar users-service.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "users-service.jar", "--spring.profiles.active=prod"]