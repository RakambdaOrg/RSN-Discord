FROM gradle:7.2-jdk16-hotspot AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN ls -R .
RUN gradle build --no-daemon

FROM adoptopenjdk:16-jre

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*-shaded.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
