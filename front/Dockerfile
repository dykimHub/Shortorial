FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

RUN npm run build

FROM nginx:1.26-alpine

# /app/builder에 없고 /app/dist에 있음
COPY --from=builder /app/dist /usr/share/nginx/html

COPY conf/nginx.conf /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]