# Oficina Mecânica – Gerenciamento de Carros

CRUD em Java (console) com banco MySQL: carros, catálogo de peças e diagnósticos.

Participantes: José Gicivaldo da Silva Santos e Igor Galindo da Silva.

## Requisitos

- JDK 17 ou mais novo
- Maven
- MySQL ligado no computador

## Como rodar

1. Clone o repositório:
   ```
   git clone <link-do-repositorio>
   cd OficinaMecanica_MySQL
   ```
2. Copie o arquivo de configuração do banco:
   ```
   cp banco.properties.example banco.properties
   ```
   (no Windows: `copy banco.properties.example banco.properties`)
3. Abra o `banco.properties` e coloque o usuário e a senha do seu MySQL.
   O banco `oficina` e as tabelas são criados sozinhos na primeira execução.
4. Rode pelo terminal:
   ```
   mvn compile exec:java -Dexec.mainClass=oficina.Main
   ```
   Ou pelo IntelliJ: **File → Open**, selecione a pasta do projeto, espere o Maven baixar as
   dependências, abra `src/main/java/oficina/Main.java` e clique na seta verde ▶ ao lado do `main`.
5. O menu aparece no terminal (ou na aba **Run** do IntelliJ). Digite o número da opção.

## Testar sem MySQL

No `banco.properties`, coloque `#` na linha do MySQL e tire o `#` da linha do H2.
Os dados ficam no arquivo `oficina-teste.mv.db`, na pasta do projeto.

## Funcionalidades

- **Carros:** cadastrar, listar (com valor da revisão), atualizar, excluir
- **Peças (catálogo):** cadastrar, ver catálogo, atualizar, excluir
- **Diagnósticos:** registrar avaria, listar, histórico do carro, atualizar, excluir
