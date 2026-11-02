# AEDs II – Sistemas de software com Estruturas de Dados Lineares

Código-base da professora (sistema de comércio) com **todas as tarefas das oficinas de Pilhas e Filas implementadas** e extras que podem cair na atividade avaliativa.

## Aluno
* Pedro Lucas Soares Rezende

## Mapa das tarefas (Ctrl+F por "TAREFA" nos arquivos)
| Oficina | Tarefa | O que pede | Onde está |
|---|---|---|---|
| Pilhas | 1 | Armazenar os pedidos na estrutura adequada (pilha) – `finalizarPedido` | `src/App.java` |
| Pilhas | 2 | `Pilha<E> subPilha(int numItens)` | `src/Pilha.java` |
| Pilhas | 3 | `listarProdutosPedidosRecentes` (menu 6) | `src/App.java` |
| Filas | 0 | Armazenar os pedidos por ordem de chegada (`filaPedidos`) | `src/App.java` |
| Filas | 1 | `double calcularValorMedio(Function<E, Double> extrator, int quantidade)` | `src/Fila.java` |
| Filas | 2 | `Fila<E> filtrar(Predicate<E> condicional, int quantidade)` | `src/Fila.java` |
| Filas | 3a | Valor total médio dos N primeiros pedidos (menu 7) | `src/App.java` |
| Filas | 3b | Primeiros pedidos com valor total acima de um valor (menu 8) | `src/App.java` |
| Filas | 3c | Primeiros pedidos que apresentam um produto (menu 9) | `src/App.java` |

## Extras prontos (podem cair)
`Pilha` e `Fila` também têm: `tamanho`, `somar`, `contar`, `buscar`, `paraCada`, `toString`; a `Pilha` tem `filtrar` e `calcularValorMedio`; a `Fila` tem `subFila`.

Exemplos de uso:
```java
filaPedidos.calcularValorMedio(Pedido::valorFinal, 5);              // média dos 5 primeiros
filaPedidos.filtrar(p -> p.valorFinal() > 100, 10);                 // pedidos acima de R$ 100
filaPedidos.filtrar(p -> p.existeNoPedido(produto) != null, 10);    // pedidos com o produto
pilhaPedidos.subPilha(3);                                           // 3 pedidos mais recentes
filaPedidos.contar(p -> p.valorFinal() > 50, filaPedidos.tamanho()); // quantos acima de R$ 50
```

## Quando usar cada estrutura
| Estrutura | Regra | Palavras do enunciado |
|---|---|---|
| Pilha | LIFO – último a entrar, primeiro a sair | "mais recentes", "últimos", "desfazer" |
| Fila  | FIFO – primeiro a entrar, primeiro a sair | "ordem de chegada", "primeiros", "atender" |

## Como rodar (na pasta raiz do projeto, onde está o produtos.txt)
```
javac -encoding UTF-8 -d out src/*.java
java -cp out App
```
