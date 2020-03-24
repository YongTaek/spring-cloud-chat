FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/userService.jar
COPY ${JAR_FILE} userService.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.security.egd=file:/dev/./urandom","-jar","/userService.jar"]

