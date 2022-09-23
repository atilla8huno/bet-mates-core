# Bet Mates Core [![build](https://github.com/bet-mates/bet-mates-core/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/bet-mates/bet-mates-core/actions/workflows/gradle.yml)

This project contains the core features of the *Bet Mates*. The core features at the moment are competitions and bets.

## Technologies

- Java 17
- Kotlin
- Ktor

## Build

To build the project locally, just run:

```
./gradlew clean build --no-daemon
```

After the build, there will be a standalone jar in the build dir:

`./build/libs/bet-mates-core-0.0.1-SNAPSHOT-standalone.jar`

## Linter

The project is using the [Kotlinter](https://github.com/jeremymailen/kotlinter-gradle) as a linter tool.

To format the code, just run:

```
./gradlew formatKotlin
```

## Run
### Jar

To run the jar directly:

```
java -jar ./build/libs/bet-mates-core-0.0.1-SNAPSHOT-standalone.jar
```

### Docker

Note: you must log in the registry [GitHub Packages (ghcr.io)](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry) with `docker login`

To build the image, refer to the [Git Workflow code](https://github.com/bet-mates/bet-mates-core/blob/main/.github/workflows/gradle.yml#L70-L73) of the project

Otherwise, pull the image from the registry built from master:
```
docker pull ghcr.io/bet-mates/bet-mates-core:latest
```
Then run:
```
docker run -p 8080:8080 ghcr.io/bet-mates/bet-mates-core:latest -d
```
Now it should be available at http://localhost:8080/

### Directly from Intellij IDEA

Run the method `main` from the class [app.betmates.core.Application.kt](https://github.com/bet-mates/bet-mates-core/blob/main/src/main/kotlin/app/betmates/core/Application.kt#L9-L10)
