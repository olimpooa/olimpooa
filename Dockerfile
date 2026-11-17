# Etapa 1: compila o projeto (JDK 21 + Maven Wrapper)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -q -DskipTests package

# Etapa 2: imagem final só com o JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --create-home appuser && mkdir -p /app/data /app/uploads && chown -R appuser /app
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
