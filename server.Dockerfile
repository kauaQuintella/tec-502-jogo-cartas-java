# Estágio 1: Build da aplicação com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código fonte e compila o projeto inteiro
COPY src ./src
RUN mvn package

# Estágio 2: Criação da imagem final do SERVIDOR
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia apenas o JAR compilado do estágio de build
COPY --from=build /app/target/tec-502-1.0-SNAPSHOT.jar .

# Expõe a porta que o servidor usa
EXPOSE 2020

# Comando para executar o SERVIDOR quando o contêiner iniciar
CMD ["java", "-cp", "tec-502-1.0-SNAPSHOT.jar", "org.project.server.TCPServerMain"]