# Estágio 1: Build da aplicação com Maven (idêntico ao do servidor)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

# Estágio 2: Criação da imagem final do CLIENTE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR compilado
COPY --from=build /app/target/tec-502-1.0-SNAPSHOT.jar .

# Comando para executar o CLIENTE quando o contêiner iniciar
CMD ["java", "-cp", "tec-502-1.0-SNAPSHOT.jar", "org.project.client.TCPClient"]