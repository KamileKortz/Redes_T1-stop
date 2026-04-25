# Stop – Instruções

## 1. Compilar os arquivos

Execute isso no terminal da pasta onde estão os arquivos `Server.java` e `Client.java` :

```bash
javac Server.java
javac Client.java
```

---

## 2. Iniciar o Servidor

Em um terminal, execute:

```bash
java Server
```

O servidor vai ficar aguardando os jogadores conectarem.

---

## 3. Conectar os Clientes

Abra um terminal para cada jogador e execute:

```bash
java Client
```

- Os jogadores precisam colocar os nomes para o jogo iniciar.
- O jogo começa automaticamente quando todos os jogadores tiverem se conectado.

---

## 4. Como Jogar

1. No começo da rodada, o servidor sorteia uma letra.
2. Cada jogador digita suas respostas, separadas por viírgula, na ordem correta das categorias.
3. Depois que todos enviaram, o servidor calcula e exibe a pontuação da rodada.
4. No final de todas as rodadas, o servidor exibe o placar final.

---

## Regras de Pontuação

| Situação | Pontos |
|---|---|
| Se nenhum outro jogador escreveu o mesmo | **3 pontos** |
| Se outro jogador escreveu a mesma coisa | **1 ponto** |
| Sem resposta | **0 pontos** |