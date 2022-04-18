FROM --platform=linux/x86_64 amazoncorretto:11-alpine-jdk
MAINTAINER atolpeko <alextolpeko@gmail.com>

ARG JAR_FILE
COPY ${JAR_FILE} employee-service.jar

ARG EXPOSED_PORT
EXPOSE ${EXPOSED_PORT}

ENTRYPOINT ["java", "-jar", "/employee-service.jar"]
