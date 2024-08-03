FROM openjdk:20-jdk-slim
WORKDIR /app

COPY target/vinted-0.0.1-SNAPSHOT.jar /app/vinted.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "vinted.jar"]