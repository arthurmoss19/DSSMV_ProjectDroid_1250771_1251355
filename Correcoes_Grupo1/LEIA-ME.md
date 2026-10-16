# Correções do Grupo 1 — o que falta para a app funcionar

Esta pasta **não faz parte do projeto**: serve só para te guiar. Depois de aplicares tudo
podes apagá-la.

## Porque é que a app não arranca hoje

O projeto **compila** (BUILD SUCCESSFUL), mas não funciona por quatro razões:

1. **O manifesto abre um ecrã que não existe.** O launcher está em `.MainActivity`, e essa
   classe já não existe → a app fecha logo ao abrir.
2. **Nenhum ecrã é montado.** Não existe um único `setContentView` nem `findViewById` no
   projeto: as 5 Activities têm os campos e os métodos, mas falta o `onCreate` que os liga.
3. **Falta o adaptador** (`views/TripAdapter.java`) → a lista de viagens nunca mostra nada.
4. **Não há backend simulado** (`mock/MockData` + `mock/MockInterceptor`) e o `ApiClient`
   não o liga → o login vai para o endereço falso do RESTDB e falha sempre.

Há ainda um ficheiro que não existe: **o layout do formulário da viagem**, por isso o
`TripFormActivity` não tem o que mostrar.

## Como usar esta pasta

Tem duas partes:

| Pasta | O que é |
| --- | --- |
| `criar/` | Ficheiros que **não existem**. Ao lado de cada um está o caminho completo a partir de `Project/`. Copia o ficheiro para lá, tal e qual. |
| `inserir/` | Blocos de código a **colar dentro de ficheiros que já tens**. Cada `.txt` diz, na primeira linha, em que ficheiro e depois de que linha é que o bloco entra. |

### Parte 1 — criar (4 ficheiros)

| Ficheiro desta pasta | Onde fica no projeto |
| --- | --- |
| `criar/app/src/main/java/views/TripAdapter.java` | `Project/app/src/main/java/views/TripAdapter.java` |
| `criar/app/src/main/java/mock/MockData.java` | `Project/app/src/main/java/mock/MockData.java` (cria a pasta `mock`) |
| `criar/app/src/main/java/mock/MockInterceptor.java` | `Project/app/src/main/java/mock/MockInterceptor.java` |
| `criar/app/src/main/res/layout/new_trip_screen.xml` | `Project/app/src/main/res/layout/new_trip_screen.xml` |

> O layout chamei-lhe `new_trip_screen.xml`, para ficar igual ao teu `new_place_screen.xml`.
> Se preferires outro nome, renomeia-o e troca a única referência no `TripFormActivity`
> (`setContentView(R.layout.new_trip_screen)`).

### Parte 2 — inserir (por esta ordem)

1. `AndroidManifest.xml.txt` — substituir as linhas 11 a 19 (a Activity do `MainActivity`).
2. `LoginActivity.java.txt` — inserir depois da linha 34.
3. `RegisterActivity.java.txt` — inserir depois da linha 30.
4. `TripListActivity.java.txt` — três blocos (A, B e C).
5. `TripFormActivity.java.txt` — três blocos (A, B e C).
6. `TripDetailActivity.java.txt` — inserir depois da linha 30.
7. `ApiClient.java.txt` — duas linhas (um `import` e uma instrução).
8. `strings.xml.txt` — 10 textos novos, colados antes do `</resources>` final.
9. `local.properties.txt` — uma linha a corrigir (sem chaves, ver abaixo).

Depois disto corre **Build → Make Project** e instala. Entras com
**demo@triptrail.pt / triptrail** e vês 3 viagens de exemplo.

## Atenção ao `local.properties`

Uma coisa tem de ser corrigida **mesmo antes de pôr as chaves**: a linha

```
RESTDB_URL=https://<seu-nome-da-base>.restdb.io/
```

tem `<` e `>` no meio, o que **não é um endereço válido** — o Retrofit rebenta com
`IllegalArgumentException: Invalid URL host` e a app fecha ao tentares entrar. Não é
preciso chave nenhuma para isto: basta pôr um endereço válido (o próprio valor por omissão
do `app/build.gradle.kts`) ou deixar a linha em branco, que ele usa esse valor.

Detalhes em `inserir/app/src/main/local.properties.txt`.

## Como é que isto funciona sem chaves

Com `USE_MOCK_DATA=true` (o valor por omissão do `build.gradle.kts`), o `MockInterceptor`
apanha todos os pedidos a `/rest/...` e responde com os dados do `MockData`: dois
utilizadores (o demo e a Ana), as 7 categorias e 3 viagens de exemplo. Quando tiveres as
chaves, basta pôr `USE_MOCK_DATA=false` no `local.properties` para passar a usar o RESTDB a
sério — o resto do código não muda.

## Importante para o Git

Esta pasta (`Correcoes_Grupo1/`) **não deve ir para o repositório**. Se aparecer no
`git status`, apaga-a ou acrescenta-a ao `.gitignore`. Os ficheiros corrigidos, esses sim,
vão para o commit.
