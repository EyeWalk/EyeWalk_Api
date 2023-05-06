FROM openjdk:17-jdk-alpine
RUN addgroup -S insane && adduser -S insane -G insane
USER insane:insane
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]