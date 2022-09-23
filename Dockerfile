FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
ARG DB_PASSWORD
ARG JWT_SECRET
ENV DB_PASSWORD $DB_PASSWORD
ENV JWT_SECRET $JWT_SECRET
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*standalone.jar /app/bet-mates-core.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app/bet-mates-core.jar"]
