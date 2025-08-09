#!/bin/bash

# 도커허브 사용자명 설정 (이 부분을 본인의 도커허브 사용자명으로 변경하세요)
DOCKERHUB_USERNAME="soongu"
IMAGE_NAME="instagram-clone"
TAG="latest"

echo "=== Instagram Clone Docker 이미지 빌드 및 푸시 ==="

# 도커허브 사용자명 확인
if [ "$DOCKERHUB_USERNAME" = "YOUR_DOCKERHUB_USERNAME" ]; then
    echo "❌ 오류: docker-compose.prod.yml 파일에서 YOUR_DOCKERHUB_USERNAME을 본인의 도커허브 사용자명으로 변경해주세요."
    exit 1
fi

# 도커 로그인 확인
echo "🔐 도커허브 로그인 상태 확인 중..."
if ! docker info | grep -q "Username"; then
    echo "❌ 도커허브에 로그인되어 있지 않습니다."
    echo "다음 명령어로 로그인해주세요:"
    echo "docker login"
    exit 1
fi

echo "✅ 도커허브 로그인 확인됨"

# 이미지 빌드
echo "🔨 도커 이미지 빌드 중..."
docker build -t $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG .

if [ $? -ne 0 ]; then
    echo "❌ 이미지 빌드 실패"
    exit 1
fi

echo "✅ 이미지 빌드 완료: $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

# 도커허브에 푸시
echo "📤 도커허브에 이미지 푸시 중..."
docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

if [ $? -ne 0 ]; then
    echo "❌ 이미지 푸시 실패"
    exit 1
fi

echo "✅ 이미지 푸시 완료!"
echo ""
echo "🎉 성공적으로 도커허브에 업로드되었습니다!"
echo "이미지: $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
echo ""
echo "다른 컴퓨터에서 실행하려면:"
echo "1. docker-compose.prod.yml 파일을 복사"
echo "2. docker-compose -f docker-compose.prod.yml up -d 실행" 