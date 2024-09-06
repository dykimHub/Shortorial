FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app

# Gradle 파일 복사
# gradlew 파일을 /app에 복사
COPY gradlew . 

# graddle 폴더를 /app/gradle에 복사
COPY gradle gradle

# build.gradle 파일을 /app에 복사
COPY build.gradle .

# settings.gradle 파일을 /app에 복사
COPY settings.gradle .

# gradlew 파일에 실행 권한 추가
RUN chmod +x ./gradlew

# Gradle 관련 파일 다운 받고 코드 변경되지 않으면 재다운로드 안 함
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
# src 폴더를 /app/src 에 복사
COPY src src

# Spring Boot 앱을 빌드(일반 jar 파일과 다르게 자체 실행 서버 포함)
# 코드 변경되지 않으면 재다운로드 안 함
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-alpine

# builder 스테이지에서 생성된 jar 파일을(build/libs/*.jar에 위치) /home.server.jar에 복사
# 최종 생성된 jar 파일만 복사하여 경량 실행용 이미지를 생성
COPY --from=builder /app/build/libs/*.jar /home/server.jar

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/home/server.jar"]