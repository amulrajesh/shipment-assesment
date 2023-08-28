FROM openjdk:17-jdk-slim

# Refer to Maven build -> finalName
ARG JAR_FILE=target/aggregation-api-0.0.2-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]