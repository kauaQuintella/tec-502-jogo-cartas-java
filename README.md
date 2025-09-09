# TEC502 - Jogo de Cartas Multiplayer: Conflito Elemental

Este projeto implementa um servidor de jogo multiplayer para um jogo de cartas baseado em "Pedra, Papel e Tesoura", com um sistema de coleção de skins e gestão de concorrência, conforme especificado no problema da disciplina TEC502.

## Funcionalidades Implementadas
- Servidor TCP concorrente para múltiplas partidas 1v1.
- Protocolo de comunicação customizado sobre Sockets nativos, usando JSON com polimorfismo.
- Matchmaking de jogadores numa fila de espera `thread-safe`.
- Jogo "Conflito Elemental" (Pedra, Papel e Tesoura) executado em sessões isoladas.
- Sistema de obtenção de skins de um "estoque" global, com garantia de distribuição justa sob concorrência (`synchronized`).
- Testes automatizados de estresse que validam a justiça e medem o desempenho do sistema.
- Medição de latência da rede (PING/PONG).
- Execução do sistema em ambiente containerizado com Docker e Docker Compose.

## Como Executar (Usando Docker)

**Pré-requisitos:**
- Docker e Docker Compose instalados.
- Java (JDK 21+) e Maven instalados para compilar.

**1. Compilar o Projeto:**
Execute o seguinte comando na raiz do projeto para criar o JAR executável ("fat jar") que inclui todas as dependências:
```bash
mvn package
```

**2. Construir as Imagens Docker:**
Este comando irá ler os ficheiros `Dockerfile` e construir as imagens para o servidor e o cliente.
```bash
docker-compose build
```

**3. Iniciar o Servidor:**
Abra um terminal e execute:
```bash
docker-compose up servidor
```

**4. Iniciar um Cliente:**
Abra um **novo terminal** e execute o seguinte comando para iniciar um cliente interativo:
```bash
docker-compose run --rm cliente
```
Repita este passo num outro terminal para conectar um segundo jogador.

## Como Executar os Testes
Para executar os testes de unidade e concorrência, que validam o matchmaking e a obtenção de skins, use o comando Maven:
```bash
mvn test
```