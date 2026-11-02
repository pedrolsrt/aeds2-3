import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * =====================================================================
 *  FILA (FIFO – primeiro a entrar, primeiro a sair) – mesmo estilo da Pilha dela
 * =====================================================================
 *  Estrutura: [sentinela = frente] -> [A] -> [B] -> [C] <- tras
 *  A célula sentinela (vazia) fica na FRENTE. Fila vazia: frente == tras.
 *  O primeiro elemento de verdade é frente.getProximo().
 *
 *  QUANDO USAR: "ordem de CHEGADA", "primeiros", "atendimento".
 *
 *  >>> OFICINA FILAS – TAREFA 1 <<<  calcularValorMedio(Function<E, Double> extrator, int quantidade)
 *  >>> OFICINA FILAS – TAREFA 2 <<<  filtrar(Predicate<E> condicional, int quantidade)
 *  EXTRAS (podem cair): tamanho, subFila, somar, contar, buscar, paraCada, toString
 * =====================================================================
 */
public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;

	public Fila() {

		Celula<E> sentinela = new Celula<E>();
		frente = sentinela;
		tras = sentinela;
	}

	public boolean vazia() {
		return frente == tras;
	}

	/** ENFILEIRAR: entra no FIM da fila. O(1) */
	public void enfileirar(E item) {

		Celula<E> novaCelula = new Celula<E>(item);
		tras.setProximo(novaCelula);
		tras = novaCelula;
	}

	/** DESENFILEIRAR: tira e devolve o da FRENTE. Erro se vazia. O(1) */
	public E desenfileirar() {

		E item = consultarPrimeiro();
		Celula<E> primeiro = frente.getProximo();
		frente.setProximo(primeiro.getProximo());

		if (primeiro == tras) {      // era o único: fila fica vazia
			tras = frente;
		}
		return item;
	}

	/** CONSULTAR PRIMEIRO: olha o da frente SEM tirar. Erro se vazia. O(1) */
	public E consultarPrimeiro() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na fila!");
		}
		return frente.getProximo().getItem();
	}

	/**
	 * >>> OFICINA FILAS – TAREFA 1 <<<
	 * Calcula e retorna o valor médio de um atributo dos elementos da fila,
	 * usando a função 'extrator' (que tira um Double de cada elemento).
	 * Usa apenas os 'quantidade' PRIMEIROS elementos. A fila NÃO é alterada.
	 *
	 * Ex.: filaPedidos.calcularValorMedio(Pedido::valorFinal, 5)
	 *      filaPedidos.calcularValorMedio(p -> p.valorFinal(), 5)   (mesma coisa)
	 *
	 * Se 'quantidade' for maior que o tamanho da fila, usa todos os elementos.
	 *
	 * @throws NoSuchElementException se a fila estiver vazia.
	 * @throws IllegalArgumentException se quantidade <= 0.
	 */
	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na fila!");
		}
		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade deve ser maior que zero!");
		}

		double soma = 0;
		int cont = 0;
		Celula<E> atual = frente.getProximo();

		while (cont < quantidade && atual != null) {
			soma += extrator.apply(atual.getItem());
			cont++;
			atual = atual.getProximo();
		}

		return soma / cont;
	}

	/**
	 * >>> OFICINA FILAS – TAREFA 2 <<<
	 * Retorna uma NOVA fila com os elementos que satisfazem a condição,
	 * testando apenas os 'quantidade' PRIMEIROS elementos da fila original.
	 * A ordem de chegada é mantida. A fila original NÃO é alterada.
	 *
	 * Ex.: filaPedidos.filtrar(p -> p.valorFinal() > 100, 10)
	 *      filaPedidos.filtrar(p -> p.existeNoPedido(produto) != null, 10)
	 *
	 * @throws IllegalArgumentException se quantidade < 0.
	 */
	public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {

		if (quantidade < 0) {
			throw new IllegalArgumentException("A quantidade não pode ser negativa!");
		}

		Fila<E> filtrada = new Fila<>();
		Celula<E> atual = frente.getProximo();

		for (int i = 0; i < quantidade && atual != null; i++) {
			if (condicional.test(atual.getItem())) {
				filtrada.enfileirar(atual.getItem());
			}
			atual = atual.getProximo();
		}

		return filtrada;
	}

	// ======================= EXTRAS =======================

	/** TAMANHO: quantos elementos tem. O(n) */
	public int tamanho() {
		int cont = 0;
		for (Celula<E> c = frente.getProximo(); c != null; c = c.getProximo()) {
			cont++;
		}
		return cont;
	}

	/** SUBFILA: nova fila só com os 'quantidade' primeiros. */
	public Fila<E> subFila(int quantidade) {
		return filtrar(item -> true, quantidade);
	}

	/** SOMA de um atributo dos 'quantidade' primeiros. */
	public double somar(Function<E, Double> extrator, int quantidade) {
		double soma = 0;
		Celula<E> atual = frente.getProximo();
		for (int i = 0; i < quantidade && atual != null; i++) {
			soma += extrator.apply(atual.getItem());
			atual = atual.getProximo();
		}
		return soma;
	}

	/** CONTAR quantos, dentre os 'quantidade' primeiros, passam no teste. */
	public int contar(Predicate<E> condicional, int quantidade) {
		int cont = 0;
		Celula<E> atual = frente.getProximo();
		for (int i = 0; i < quantidade && atual != null; i++) {
			if (condicional.test(atual.getItem())) {
				cont++;
			}
			atual = atual.getProximo();
		}
		return cont;
	}

	/** BUSCAR o primeiro (por ordem de chegada) que passa no teste; null se nenhum. */
	public E buscar(Predicate<E> condicional) {
		for (Celula<E> c = frente.getProximo(); c != null; c = c.getProximo()) {
			if (condicional.test(c.getItem())) {
				return c.getItem();
			}
		}
		return null;
	}

	/** PERCORRER da frente pro fim. Ex.: fila.paraCada(p -> System.out.println(p)) */
	public void paraCada(Consumer<E> acao) {
		for (Celula<E> c = frente.getProximo(); c != null; c = c.getProximo()) {
			acao.accept(c.getItem());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Celula<E> c = frente.getProximo(); c != null; c = c.getProximo()) {
			sb.append(c.getItem()).append("\n");
		}
		return sb.toString();
	}
}
