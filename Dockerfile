FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give execution permission to the maven wrapper
RUN chmod +x mvnw

# Download dependencies (improves build caching)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (for local reference)
EXPOSE 8080

# Run the application with preview features enabled, and assign the port provided by Render
CMD ["sh", "-c", "java --enable-preview -jar app.jar --server.port=${PORT:-8080}"]
