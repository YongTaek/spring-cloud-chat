FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/authenticationService.jar
COPY ${JAR_FILE} authenticationService.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.security.egd=file:/dev/./urandom","-jar","/authenticationService.jar"]

EXPOSE 8888
