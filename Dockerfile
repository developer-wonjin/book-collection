# Java 17 이미지를 기반으로 설정
FROM openjdk:17-jdk-alpine

# 컨테이너가 시작될 때 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "/app/book-collection-0.0.1-SNAPSHOT.jar"]

# 기본 포트 설정 (필요에 따라 변경 가능)
EXPOSE 8080
