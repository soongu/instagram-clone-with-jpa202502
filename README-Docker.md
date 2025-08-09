# Instagram Clone Docker 실행 가이드

이 가이드는 Instagram Clone 애플리케이션을 Docker를 사용하여 실행하는 방법을 설명합니다.

## 사전 요구사항

- Docker Desktop 설치
- Docker Compose 설치 (Docker Desktop에 포함됨)

## 빠른 시작

### 1. 애플리케이션 실행

```bash
# 애플리케이션과 MariaDB를 함께 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### 2. 애플리케이션 접속

- 웹 애플리케이션: http://localhost:8900
- MariaDB: localhost:3307

### 3. 애플리케이션 중지

```bash
# 애플리케이션 중지
docker-compose down

# 데이터까지 모두 삭제 (주의: 모든 데이터가 삭제됩니다)
docker-compose down -v
```

## 서비스 구성

### MariaDB

- **포트**: 3307
- **데이터베이스**: instagram_clone
- **사용자**: instagram_user
- **비밀번호**: instagram_password
- **루트 비밀번호**: rootpassword

### Spring Boot 애플리케이션

- **포트**: 8900
- **업로드 디렉토리**: `/app/uploads` (도커 볼륨으로 마운트)

## 데이터베이스 접속

```bash
# MariaDB 컨테이너에 접속
docker exec -it instagram-mariadb mysql -u instagram_user -p instagram_clone

# 또는 루트로 접속
docker exec -it instagram-mariadb mysql -u root -p
```

## 파일 업로드

업로드된 파일들은 도커 볼륨 `app_uploads`에 저장되며, 컨테이너가 재시작되어도 유지됩니다.

## 문제 해결

### 포트 충돌

만약 8900번 포트가 이미 사용 중이라면, `docker-compose.yml` 파일에서 포트 매핑을 변경하세요:

```yaml
ports:
  - '8901:8900' # 호스트의 8901 포트를 컨테이너의 8900 포트에 매핑
```

### 데이터베이스 연결 실패

MariaDB가 완전히 시작될 때까지 기다리도록 healthcheck가 설정되어 있습니다. 만약 여전히 연결 문제가 있다면:

```bash
# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs mariadb
```

### 애플리케이션 재빌드

코드 변경 후 애플리케이션을 재빌드하려면:

```bash
# 이미지 재빌드
docker-compose build app

# 서비스 재시작
docker-compose up -d app
```

## 개발 환경

개발 중에는 다음 명령어를 사용하여 실시간 로그를 확인할 수 있습니다:

```bash
# 실시간 로그 확인
docker-compose logs -f app

# 특정 서비스의 로그만 확인
docker-compose logs -f mariadb
```
