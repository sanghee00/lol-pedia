#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin {ECR_URL}
docker stop lol-pedia-server || true
docker rm lol-pedia-server || true
docker pull {ECR_URL}/lol-pedia:latest
docker run -d --name lol-pedia-server -p 80:8080 --env-file /home/ubuntu/lol-pedia-server/.env {ECR_URL}/lol-pedia:latest
echo "--------------- 서버 배포 끝 ------------------"
