#!/usr/bin/env bash
# EC2 인스턴스 최초 부팅 시 자동 실행되는 스크립트.
#
# 사람이 SSH로 접속해서 커맨드를 하나씩 치는 단계는 언젠가 빠뜨립니다
# (Java 미설치로 인한 재시작 루프 → CPU 크레딧 고갈 장애가 실제로 있었음).
# 그래서 "새 인스턴스를 띄울 때마다 반드시 필요한, repo 상태와 무관한" 설정만
# 여기 담아서 EC2 콘솔의 User data에 그대로 붙여넣습니다 (EC2 시작 시 root로 1회 실행됨).
#
# 이미 떠 있는 인스턴스에도 그대로 다시 실행 가능합니다 (모든 단계가 idempotent).
# 사용법: sudo bash deploy/ec2-bootstrap.sh
#
# 여기 포함 안 된 것들(레포가 있어야 하거나 도메인/시크릿 등 외부 상태가 필요해서
# 자동화하지 않음 — deploy/README.md 참고): git clone, docker compose up,
# systemd 유닛 설치, nginx/certbot 설정.

set -euo pipefail

apt-get update -y
apt-get install -y docker.io docker-compose-plugin openjdk-21-jre-headless nginx certbot python3-certbot-nginx

systemctl enable --now docker
usermod -aG docker ubuntu || true

# 스왑 2GiB. t3.micro/small처럼 메모리가 빠듯한 인스턴스에서 OOM 대신 버티게 해줍니다.
# "문서에는 있는데 아무도 실제로 안 돌림" 사고가 났던 부분이라 여기서 강제로 보장합니다.
if [ ! -f /swapfile ]; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
fi
grep -q '^/swapfile ' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
