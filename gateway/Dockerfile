FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/api.jar
COPY ${JAR_FILE} api.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.security.egd=file:/dev/./urandom","-jar","/api.jar"]

EXPOSE 8765
