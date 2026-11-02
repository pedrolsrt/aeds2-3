import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * =====================================================================
 *  PILHA (LIFO – último a entrar, primeiro a sair) – código-base da professora
 * =====================================================================
 *  Estrutura: topo -> [C] -> [B] -> [A] -> [sentinela = fundo]
 *  A célula sentinela (vazia) fica no FUNDO. Pilha vazia: topo == fundo.
 *
 *  QUANDO USAR: "mais RECENTES", "últimos", "desfazer", "histórico".
 *
 *  >>> OFICINA PILHAS – TAREFA 2 <<<  subPilha(int numItens)
 *  EXTRAS (podem cair): tamanho, filtrar, calcularValorMedio, somar,
 *                       contar, buscar, paraCada, toString
 * =====================================================================
 */
public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	/** EMPILHAR: coloca no topo. O(1) */
	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	/** DESEMPILHAR: tira e devolve o do topo. Erro se vazia. O(1) */
	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	/** CONSULTAR TOPO: olha o do topo SEM tirar. Erro se vazia. O(1) */
	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * >>> OFICINA PILHAS – TAREFA 2 <<<
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * 
	 * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * COMO FUNCIONA: percorre do topo pra baixo empilhando numa pilha auxiliar
	 * (que fica INVERTIDA) e depois passa da auxiliar pra pilha final (desinverte).
	 * A pilha original NÃO é alterada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {

		if (numItens < 0) {
			throw new IllegalArgumentException("O número de itens não pode ser negativo!");
		}

		Pilha<E> auxiliar = new Pilha<>();
		Celula<E> atual = topo;

		for (int i = 0; i < numItens; i++) {
			if (atual == fundo) {
				throw new IllegalArgumentException("A pilha não contém " + numItens + " elementos!");
			}
			auxiliar.empilhar(atual.getItem());
			atual = atual.getProximo();
		}

		Pilha<E> resultado = new Pilha<>();
		while (!auxiliar.vazia()) {
			resultado.empilhar(auxiliar.desempilhar());
		}

		return resultado;
	}

	// ======================= EXTRAS =======================

	/** TAMANHO: quantos elementos tem. O(n) */
	public int tamanho() {
		int cont = 0;
		for (Celula<E> c = topo; c != fundo; c = c.getProximo()) {
			cont++;
		}
		return cont;
	}

	/**
	 * FILTRAR: nova pilha (mesma ordem) só com os elementos, dentre os
	 * 'quantidade' do topo, que passam no teste. Original NÃO muda.
	 * Ex.: pilhaPedidos.filtrar(p -> p.valorFinal() > 100, 5)
	 */
	public Pilha<E> filtrar(Predicate<E> condicional, int quantidade) {

		Pilha<E> auxiliar = new Pilha<>();
		Celula<E> atual = topo;

		for (int i = 0; i < quantidade && atual != fundo; i++) {
			if (condicional.test(atual.getItem())) {
				auxiliar.empilhar(atual.getItem());
			}
			atual = atual.getProximo();
		}

		Pilha<E> resultado = new Pilha<>();
		while (!auxiliar.vazia()) {
			resultado.empilhar(auxiliar.desempilhar());
		}
		return resultado;
	}

	/**
	 * MÉDIA de um atributo dos 'quantidade' elementos do topo.
	 * Ex.: pilhaPedidos.calcularValorMedio(Pedido::valorFinal, 3)
	 */
	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}
		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade deve ser maior que zero!");
		}

		double soma = 0;
		int cont = 0;
		Celula<E> atual = topo;
		while (cont < quantidade && atual != fundo) {
			soma += extrator.apply(atual.getItem());
			cont++;
			atual = atual.getProximo();
		}
		return soma / cont;
	}

	/** SOMA de um atributo dos 'quantidade' elementos do topo. */
	public double somar(Function<E, Double> extrator, int quantidade) {
		double soma = 0;
		Celula<E> atual = topo;
		for (int i = 0; i < quantidade && atual != fundo; i++) {
			soma += extrator.apply(atual.getItem());
			atual = atual.getProximo();
		}
		return soma;
	}

	/** CONTAR quantos, dentre os 'quantidade' do topo, passam no teste. */
	public int contar(Predicate<E> condicional, int quantidade) {
		int cont = 0;
		Celula<E> atual = topo;
		for (int i = 0; i < quantidade && atual != fundo; i++) {
			if (condicional.test(atual.getItem())) {
				cont++;
			}
			atual = atual.getProximo();
		}
		return cont;
	}

	/** BUSCAR o primeiro (a partir do topo) que passa no teste; null se nenhum. */
	public E buscar(Predicate<E> condicional) {
		for (Celula<E> c = topo; c != fundo; c = c.getProximo()) {
			if (condicional.test(c.getItem())) {
				return c.getItem();
			}
		}
		return null;
	}

	/** PERCORRER do topo pro fundo. Ex.: pilha.paraCada(p -> System.out.println(p)) */
	public void paraCada(Consumer<E> acao) {
		for (Celula<E> c = topo; c != fundo; c = c.getProximo()) {
			acao.accept(c.getItem());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Celula<E> c = topo; c != fundo; c = c.getProximo()) {
			sb.append(c.getItem()).append("\n");
		}
		return sb.toString();
	}
}
