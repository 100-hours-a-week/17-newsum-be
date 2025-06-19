FROM eclipse-temurin:17-jdk-alpine

# GitHub Actions에서 app.jar로 복사되었기 때문에
COPY app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
