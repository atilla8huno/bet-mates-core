FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
RUN --mount=type=secret,id=DB_PASSWORD
COPY --from=build /home/gradle/src/build/libs/*standalone.jar /app/bet-mates-core.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app/bet-mates-core.jar", "-Ddb.password=$(cat /run/secrets/DB_PASSWORD)"]
