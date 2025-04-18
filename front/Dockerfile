FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

RUN npm run build

FROM nginx:1.26-alpine

# builder 스테이지에서 /app/dist(/app/builder 아님)에 만들어진 정적 파일들을 Nginx 루트 경로로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# Nginx 설정 템플릿 복사
COPY conf/default.template.conf /etc/nginx/templates/default.template.conf

# 환경변수 치환, Nginx 실행하는 entrypoint 스크립트 복사
COPY conf/entrypoint.sh /entrypoint.sh

# entrypoint.sh 실행 권한 부여
RUN chmod +x /entrypoint.sh

# 컨테이너 시작 시 entrypoint.sh 실행
ENTRYPOINT ["/entrypoint.sh"]