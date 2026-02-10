#!/bin/bash

# Base URL
BASE_URL="http://localhost:8090/api/auth"

echo "=== 1. 회원가입 (Sign Up) ==="
curl -X POST "$BASE_URL/signup" \
-H "Content-Type: application/json" \
-d '{
  "username": "hong_gildong",
  "password": "Password123!",
  "emailOrPhone": "hong@example.com",
  "name": "홍길동"
}' | json_pp
echo -e "\n"

echo "=== 2. 중복 확인 (Duplicate Check - Username) ==="
curl -X GET "$BASE_URL/check-duplicate?type=username&value=hong_gildong" | json_pp
echo -e "\n"

echo "=== 3. 중복 확인 (Duplicate Check - Email) ==="
curl -X GET "$BASE_URL/check-duplicate?type=email&value=hong@example.com" | json_pp
echo -e "\n"

echo "=== 4. 중복 확인 (Duplicate Check - Phone) ==="
curl -X GET "$BASE_URL/check-duplicate?type=phone&value=01012345678" | json_pp
echo -e "\n"
