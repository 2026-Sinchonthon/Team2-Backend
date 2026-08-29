# 배포 서버(EC2) 설정

`.github/workflows/deploy.yml`이 성공하려면 EC2 인스턴스에 아래가 미리 준비되어 있어야 합니다.
인스턴스가 새로 뜨거나 담당자가 없을 때 복구할 수 있도록 여기에 정리합니다.

## 0. 코드 최초 배치 + Docker

```bash
sudo apt update && sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker ubuntu   # 이후 재접속 필요

cd /home/ubuntu
git clone <레포주소> Team2-Backend
```

## 1. DB - EC2 위 Docker Compose (RDS 아님)

레포의 `docker-compose.yml`을 EC2에서 그대로 최초 1회 띄웁니다. **CI/CD에는 포함되지 않는 수동
작업**이라, 새 인스턴스를 띄울 때마다 SSH로 직접 실행해야 합니다.

```bash
cd /home/ubuntu/Team2-Backend
docker compose up -d
```

- `127.0.0.1:3306`으로만 바인딩되어 외부에서 DB 포트로 직접 접근 불가 (앱만 접근 가능)
- `mysql_data` 볼륨에 데이터가 남아 재기동해도 유지됨

## 2. systemd 서비스

최초 1회만 아래로 설치하면, 그 다음부터는 **매 배포마다 `deploy.yml`이 리포의
[`team2-backend.service`](./team2-backend.service) 내용을 그대로 서버에 다시 반영**합니다
(`sudo cp` + `daemon-reload`). 리포에서 유닛 파일을 고쳤는데 서버에는 안 퍼진 채로 배포가
"성공"해버리는 사고를 방지합니다.

```bash
sudo mkdir -p /etc/team2-backend
sudo cp deploy/team2-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable team2-backend   # 배포마다 다시 할 필요 없음 — 재부팅 시 자동 시작 설정
```

- jar 경로: `/home/ubuntu/Team2-Backend/build/libs/app.jar`
  (`build.gradle`의 `bootJar.archiveFileName`으로 고정 — `version`이 바뀌어도 안 바뀜)
- `--spring.profiles.active=prod`를 커맨드라인 인자로 명시함. 이게 빠지면
  `application.yml`의 `spring.profiles.default: local`이 적용되어 `application-local.yml`의
  로컬 전용 값으로 뜰 수 있음 — **절대 빼면 안 됨.**

## 3. 환경변수 (`/etc/team2-backend/app.env`)

`EnvironmentFile=/etc/team2-backend/app.env`로 주입합니다. `application-prod.yml`에 기본값이
있어 파일이 비어 있어도 부팅은 되지만(도커 mysql 기본 계정 기준), 운영 값이 다르면 여기서
덮어씁니다.

```
DB_URL=jdbc:mysql://127.0.0.1:3306/sinchonthon?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=sinchonthon
DB_PASSWORD=sinchonthon
```

## 4. GitHub Secrets (Repository → Settings → Secrets and variables → Actions)

| Secret | 용도 |
|---|---|
| `EC2_HOST` | 탄력적 IP (`13.124.0.95`) |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_KEY` | EC2 접속용 PEM 프라이빗 키 |

## 5. sudoers 전제

AWS 기본 Ubuntu AMI는 `/etc/sudoers.d/90-cloud-init-users`(cloud-init 기본 생성 파일)에
`ubuntu ALL=(ALL) NOPASSWD:ALL`이 이미 들어 있어서, 별도 sudoers 설정 없이도
`deploy.yml`의 `sudo systemctl restart team2-backend`가 비밀번호 없이 동작합니다. 다른 AMI로
바꾸면 이 전제가 깨질 수 있습니다.

## 6. 디스크/메모리 (t3.micro라면)

기본 8GiB 디스크로 빠듯할 수 있어 20GiB로 확장을 권장합니다(`growpart` + `resize2fs`).
메모리도 1GB면 스왑 2GiB 추가를 권장합니다. 빌드는 GitHub Actions 러너에서 하고 EC2에는
jar만 전달하므로, 서버에서 직접 `./gradlew`를 실행하지 않는 한 큰 여유는 필요 없습니다.

```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

## 7. 보안 그룹

인바운드: 22(SSH), 8080(앱). 도메인을 연결하고 nginx+HTTPS를 붙이게 되면 80/443도 열고
`deploy/` 아래에 nginx 설정과 certbot 발급 절차를 추가하세요 (지금은 IP만 있어 생략).

## 8. 지금 서버에 어떤 커밋이 떠 있는지 확인하기

헬스체크를 통과한 배포마다 `/home/ubuntu/Team2-Backend/CURRENT_SHA`에 커밋 SHA를 기록합니다.

```bash
cat /home/ubuntu/Team2-Backend/CURRENT_SHA
```

## 9. 수동 스키마 마이그레이션이 필요한 배포

Flyway/Liquibase가 없고 `ddl-auto: update`만 씁니다. `update`는 **컬럼 삭제·이름 변경·NOT NULL
해제를 반영하지 않으므로**, 이런 변경이 담긴 PR을 배포할 땐 앱 재시작 전후로 서버 MySQL에
수동으로 ALTER를 실행해야 합니다. 안 하면 그 컬럼이 걸린 INSERT/UPDATE가 전부 실패합니다.

**대기중인 마이그레이션:** 없음.
