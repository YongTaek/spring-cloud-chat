FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/eureka-server.jar
COPY ${JAR_FILE} eureka-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/eureka-server.jar"]

EXPOSE 8761
