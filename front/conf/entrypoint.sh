# 복사해 둔 템플릿에 환경변수 삽입하고 nginx가 읽는 설정파일로 복사
envsubst '${VITE_HOME_URL}' < /etc/nginx/templates/default.template.conf > /etc/nginx/conf.d/default.conf

# nginx 시작
exec nginx -g 'daemon off;'
