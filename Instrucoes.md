# Instruções - Jogo STOP Distribuído

### Funcionamento
1. Compile os arquivos: `javac *.java`
2. Inicie o Servidor: `java Server`
3. Inicie a quantidade de clientes definida no Servidor (`n_jogadores`): `java Client`
4. Siga as instruções no terminal do Cliente.

### Regras Implementadas
- O servidor sorteia uma letra automaticamente.
- Os jogadores devem digitar suas respostas quando solicitado.
- O servidor aguarda todos responderem antes de somar os pontos.
- Placar final exibido após `n_rodadas`.