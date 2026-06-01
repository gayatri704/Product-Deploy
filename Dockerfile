#Step 1: Build Stage
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app

#Command to grant execution permissions to prevent Windows environment permission errors
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

#Step 2: Execution Stage
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
COPY --from=build /home/app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
