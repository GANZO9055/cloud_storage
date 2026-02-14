FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/cloud_storage-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]