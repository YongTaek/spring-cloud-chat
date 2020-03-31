FROM openjdk:11
MAINTAINER oytech

VOLUME /tmp
ARG JAR_FILE=target/userService.jar
COPY ${JAR_FILE} /home/app/userService.jar
ENTRYPOINT ["java","-Dspring.profiles.active=kubernetes","-Djava.security.egd=file:/dev/./urandom","-jar","/home/app/userService.jar"]
EXPOSE 50001
