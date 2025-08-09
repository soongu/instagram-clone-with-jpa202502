# Java 17 베이스 이미지 사용
FROM openjdk:17-jdk-slim

# MariaDB 클라이언트 설치 (mysqladmin 명령어 사용을 위해)
RUN apt-get update && apt-get install -y mariadb-client && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 build.gradle 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼에 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 레이어 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# 업로드 디렉토리 생성
RUN mkdir -p /app/uploads

# 포트 노출
EXPOSE 8900

# 애플리케이션 실행 (도커 프로파일 사용)
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "build/libs/instagram-clone-0.0.1-SNAPSHOT.jar"] 