#!/bin/bash

# Instagram Clone 빠른 배포 스크립트
# 다른 컴퓨터에서 실행할 때 사용

DOCKERHUB_USERNAME="soongu"
COMPOSE_FILE="docker-compose.prod.yml"

echo "=== Instagram Clone 빠른 배포 ==="

# 도커 설치 확인
if ! command -v docker &> /dev/null; then
    echo "❌ Docker가 설치되어 있지 않습니다."
    echo "Docker Desktop을 설치해주세요: https://www.docker.com/products/docker-desktop"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose가 설치되어 있지 않습니다."
    echo "Docker Desktop을 설치해주세요: https://www.docker.com/products/docker-desktop"
    exit 1
fi

echo "✅ Docker 및 Docker Compose 확인됨"

# docker-compose.prod.yml 파일 확인
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "❌ $COMPOSE_FILE 파일이 없습니다."
    echo "배포 가이드를 확인해주세요."
    exit 1
fi

# 도커허브 사용자명 확인
if grep -q "soongu" "$COMPOSE_FILE"; then
    echo "❌ $COMPOSE_FILE 파일에서 YOUR_DOCKERHUB_USERNAME을 실제 도커허브 사용자명으로 변경해주세요."
    exit 1
fi

echo "✅ 설정 파일 확인됨"

# 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker-compose -f "$COMPOSE_FILE" down 2>/dev/null || true

# 최신 이미지 다운로드
echo "📥 최신 이미지 다운로드 중..."
docker-compose -f "$COMPOSE_FILE" pull

# 서비스 시작
echo "🚀 서비스 시작 중..."
docker-compose -f "$COMPOSE_FILE" up -d

# 서비스 상태 확인
echo "📊 서비스 상태 확인 중..."
sleep 5
docker-compose -f "$COMPOSE_FILE" ps

echo ""
echo "🎉 배포 완료!"
echo "🌐 웹 애플리케이션: http://localhost:8900"
echo "🗄️  MariaDB: localhost:3307"
echo ""
echo "📋 유용한 명령어:"
echo "  로그 확인: docker-compose -f $COMPOSE_FILE logs -f"
echo "  서비스 중지: docker-compose -f $COMPOSE_FILE down"
echo "  서비스 재시작: docker-compose -f $COMPOSE_FILE restart" 