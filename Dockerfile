FROM openjdk:14-alpine
MAINTAINER Thomas Couchoud <thomas.couchoud@gmail.com>

COPY ./build/libs /usr/app
WORKDIR /usr/app

ENTRYPOINT ["java", "-jar", "rsn-discord-shaded.jar"]
