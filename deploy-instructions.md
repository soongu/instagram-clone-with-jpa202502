# Instagram Clone 도커허브 배포 가이드

이 가이드는 Instagram Clone 애플리케이션을 도커허브에 업로드하고 다른 컴퓨터에서 실행하는 방법을 설명합니다.

## 1. 도커허브 계정 준비

### 1.1 도커허브 계정 생성

- [Docker Hub](https://hub.docker.com)에서 계정을 생성하세요.

### 1.2 도커 로그인

```bash
docker login
# 사용자명과 비밀번호를 입력하세요
```

## 2. 이미지 빌드 및 업로드

### 2.1 설정 파일 수정

`docker-compose.prod.yml` 파일에서 `YOUR_DOCKERHUB_USERNAME`을 본인의 도커허브 사용자명으로 변경하세요:

```yaml
app:
  image: your-username/instagram-clone:latest
```

### 2.2 이미지 빌드 및 푸시

```bash
# 스크립트 실행 권한 부여 (이미 완료됨)
chmod +x build-and-push.sh

# 이미지 빌드 및 도커허브 업로드
./build-and-push.sh
```

또는 수동으로 실행:

```bash
# 이미지 빌드
docker build -t your-username/instagram-clone:latest .

# 도커허브에 푸시
docker push your-username/instagram-clone:latest
```

## 3. 다른 컴퓨터에서 실행

### 3.1 필요한 파일 준비

다른 컴퓨터에 다음 파일들을 복사하세요:

- `docker-compose.prod.yml`
- `src/main/resources/sql/ddl.sql`

### 3.2 디렉토리 구조

```
instagram-clone/
├── docker-compose.prod.yml
└── src/
    └── main/
        └── resources/
            └── sql/
                └── ddl.sql
```

### 3.3 설정 파일 수정

`docker-compose.prod.yml` 파일에서 이미지 이름을 업로드한 사용자명으로 변경:

```yaml
app:
  image: your-username/instagram-clone:latest
```

### 3.4 애플리케이션 실행

```bash
# 애플리케이션 실행
docker-compose -f docker-compose.prod.yml up -d

# 로그 확인
docker-compose -f docker-compose.prod.yml logs -f

# 브라우저에서 접속: http://localhost:8900
```

## 4. 관리 명령어

### 4.1 서비스 상태 확인

```bash
docker-compose -f docker-compose.prod.yml ps
```

### 4.2 서비스 중지

```bash
docker-compose -f docker-compose.prod.yml down
```

### 4.3 데이터 삭제 (주의!)

```bash
docker-compose -f docker-compose.prod.yml down -v
```

### 4.4 이미지 업데이트

```bash
# 최신 이미지 다운로드
docker-compose -f docker-compose.prod.yml pull

# 서비스 재시작
docker-compose -f docker-compose.prod.yml up -d
```

## 5. 문제 해결

### 5.1 이미지 다운로드 실패

```bash
# 이미지 존재 여부 확인
docker search your-username/instagram-clone

# 수동으로 이미지 다운로드
docker pull your-username/instagram-clone:latest
```

### 5.2 포트 충돌

`docker-compose.prod.yml`에서 포트를 변경:

```yaml
ports:
  - '8901:8900' # 호스트의 8901 포트 사용
```

### 5.3 데이터베이스 연결 실패

```bash
# MariaDB 컨테이너 상태 확인
docker-compose -f docker-compose.prod.yml logs mariadb

# MariaDB에 직접 접속
docker exec -it instagram-mariadb mysql -u instagram_user -p instagram_clone
```

## 6. 보안 고려사항

### 6.1 환경 변수 관리

프로덕션 환경에서는 민감한 정보를 환경 변수로 관리하세요:

```bash
# .env 파일 생성
DATABASE_PASSWORD=your_secure_password
JWT_SECRET_KEY=your_secure_jwt_key
```

### 6.2 네트워크 보안

프로덕션 환경에서는 외부 접근을 제한하세요:

- 방화벽 설정
- 리버스 프록시 사용
- HTTPS 적용

## 7. 모니터링

### 7.1 로그 확인

```bash
# 실시간 로그
docker-compose -f docker-compose.prod.yml logs -f app

# 특정 시간 이후 로그
docker-compose -f docker-compose.prod.yml logs --since="2024-01-01T00:00:00" app
```

### 7.2 리소스 사용량 확인

```bash
docker stats instagram-app instagram-mariadb
```
