# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy the backend project into the container
COPY learn/ .
# Run the build
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/learn-0.0.1-SNAPSHOT.jar app.jar
# Expose the port the app runs on. Render will use this.
EXPOSE 8081
# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
