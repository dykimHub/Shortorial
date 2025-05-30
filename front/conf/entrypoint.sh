#!/bin/sh

envsubst '${VITE_HOME_URL}' < /etc/nginx/templates/default.template.conf > /etc/nginx/conf.d/default.conf

echo "proxy_pass: "
cat /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'