FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/messageService.jar
COPY ${JAR_FILE} messageService.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.security.egd=file:/dev/./urandom","-jar","/messageService.jar"]

