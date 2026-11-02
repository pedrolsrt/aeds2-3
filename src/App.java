import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/*
 * =====================================================================
 *  APP – sistema de comércio (código-base da professora + tarefas feitas)
 * =====================================================================
 *  ONDE ESTÁ CADA TAREFA (Ctrl+F por "TAREFA"):
 *   Pilhas T1 -> finalizarPedido (empilha)
 *   Pilhas T2 -> Pilha.subPilha                 (arquivo Pilha.java)
 *   Pilhas T3 -> listarProdutosPedidosRecentes  (menu 6)
 *   Filas  T0 -> filaPedidos + finalizarPedido (enfileira)
 *   Filas  T1 -> Fila.calcularValorMedio        (arquivo Fila.java)
 *   Filas  T2 -> Fila.filtrar                   (arquivo Fila.java)
 *   Filas  T3 -> exibirValorMedioPrimeirosPedidos (menu 7),
 *                exibirPedidosAcimaDeValor        (menu 8),
 *                exibirPedidosComProduto          (menu 9)
 *
 *  O que eu MUDEI no código dela: só preenchi os TODO, criei a fila,
 *  acrescentei as opções 7, 8 e 9 no menu e o "pedido = null" no case 5.
 * =====================================================================
 */
public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos (o mais RECENTE fica no topo) – Oficina Pilhas */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** >>> OFICINA FILAS – TAREFA 0 <<< Fila de pedidos (ORDEM DE CHEGADA) */
    static Fila<Pedido> filaPedidos = new Fila<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Exibir o valor total médio dos N primeiros pedidos");
        System.out.println("8 - Exibir os primeiros pedidos com valor total acima de um determinado valor");
        System.out.println("9 - Exibir os primeiros pedidos que apresentam um determinado produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.toLowerCase().equals(descricao.toLowerCase())) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * >>> OFICINA PILHAS – TAREFA 1  +  OFICINA FILAS – TAREFA 0 <<<
     * Finaliza um pedido, momento no qual ele deve ser armazenado em uma pilha de pedidos.
     * PILHA: pra achar os pedidos mais RECENTES (Tarefa 1 de Pilhas: a estrutura certa é a pilha).
     * FILA:  pra guardar por ORDEM DE CHEGADA (Tarefa 0 de Filas).
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {

    	if (pedido == null) {
    		System.out.println("Não há pedido em aberto. Inicie um pedido (opção 4) antes de fechar.");
    		return;
    	}

    	pilhaPedidos.empilhar(pedido);
    	filaPedidos.enfileirar(pedido);

    	System.out.println("Pedido finalizado e armazenado:");
    	System.out.println(pedido);
    }

    /**
     * >>> OFICINA PILHAS – TAREFA 3 <<<
     * Lista os produtos dos N pedidos mais recentes.
     * Usa subPilha (cópia dos N do topo) e desempilha a CÓPIA, então a pilha original não muda.
     */
    public static void listarProdutosPedidosRecentes() {

    	cabecalho();
    	if (pilhaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado ainda.");
    		return;
    	}

    	Integer numPedidos = lerOpcao("Quantos pedidos mais recentes deseja consultar?", Integer.class);
    	if (numPedidos == null || numPedidos <= 0) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	try {
    		Pilha<Pedido> recentes = pilhaPedidos.subPilha(numPedidos);
    		System.out.println("\nPRODUTOS DOS " + numPedidos + " PEDIDOS MAIS RECENTES:");
    		while (!recentes.vazia()) {
    			Pedido pedido = recentes.desempilhar();
    			System.out.println("------------------------------");
    			System.out.println("Pedido " + pedido.hashCode() + ":");
    			for (ItemDePedido item : pedido.getItensDoPedido()) {
    				if (item != null) {               // o vetor tem posições vazias (null) no final
    					System.out.println(item.getProduto());
    				}
    			}
    		}
    	} catch (IllegalArgumentException e) {
    		System.out.println(e.getMessage());
    	}
    }

    /**
     * >>> OFICINA FILAS – TAREFA 3 (opção a) <<<
     * Exibe o valor total médio dos N primeiros pedidos (Fila.calcularValorMedio).
     */
    public static void exibirValorMedioPrimeirosPedidos() {

    	cabecalho();
    	if (filaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado ainda.");
    		return;
    	}

    	Integer n = lerOpcao("Quantos primeiros pedidos deseja considerar?", Integer.class);
    	if (n == null || n <= 0) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	double media = filaPedidos.calcularValorMedio(Pedido::valorFinal, n);
    	System.out.println(String.format("Valor total médio dos %d primeiros pedidos: R$ %.2f",
    			Math.min(n, filaPedidos.tamanho()), media));
    }

    /**
     * >>> OFICINA FILAS – TAREFA 3 (opção b) <<<
     * Exibe, dentre os N primeiros pedidos, os que têm valor total acima de um valor (Fila.filtrar).
     */
    public static void exibirPedidosAcimaDeValor() {

    	cabecalho();
    	if (filaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado ainda.");
    		return;
    	}

    	Integer n = lerOpcao("Quantos primeiros pedidos deseja analisar?", Integer.class);
    	if (n == null || n <= 0) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	System.out.println("Digite o valor mínimo (ex.: 50,00):");
    	double valorMinimo;
    	try {
    		valorMinimo = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
    	} catch (NumberFormatException e) {
    		System.out.println("Valor inválido.");
    		return;
    	}

    	Fila<Pedido> resultado = filaPedidos.filtrar(pedido -> pedido.valorFinal() > valorMinimo, n);
    	exibirFilaDePedidos(resultado, String.format("com valor total acima de R$ %.2f", valorMinimo));
    }

    /**
     * >>> OFICINA FILAS – TAREFA 3 (opção c) <<<
     * Exibe, dentre os N primeiros pedidos, os que contêm um produto (Fila.filtrar).
     * Usa o existeNoPedido do Pedido da professora (devolve null se não tiver o produto).
     */
    public static void exibirPedidosComProduto() {

    	cabecalho();
    	if (filaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado ainda.");
    		return;
    	}

    	Integer n = lerOpcao("Quantos primeiros pedidos deseja analisar?", Integer.class);
    	if (n == null || n <= 0) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	Produto produto = localizarProdutoDescricao();
    	if (produto == null) {
    		System.out.println("Produto não encontrado.");
    		return;
    	}

    	Fila<Pedido> resultado = filaPedidos.filtrar(pedido -> pedido.existeNoPedido(produto) != null, n);
    	exibirFilaDePedidos(resultado, "que apresentam o produto " + produto.descricao);
    }

    /** Mostra os pedidos de uma fila (usado pelas opções 8 e 9). */
    static void exibirFilaDePedidos(Fila<Pedido> fila, String descricao) {

    	if (fila.vazia()) {
    		System.out.println("Nenhum pedido " + descricao + ".");
    		return;
    	}
    	System.out.println("\n" + fila.tamanho() + " pedido(s) " + descricao + ":");
    	fila.paraCada(pedido -> System.out.println(pedido));
    }

	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                	finalizarPedido(pedido);
                	pedido = null;          // evita fechar o mesmo pedido duas vezes
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> exibirValorMedioPrimeirosPedidos();
                case 8 -> exibirPedidosAcimaDeValor();
                case 9 -> exibirPedidosComProduto();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
