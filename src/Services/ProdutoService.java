package Services;

import Models.Estoque;
import Models.Fornecedor;
import Models.Produto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoService {

    private List<Produto> produtos;

    public List<Produto> getProdutos() {
        return produtos;
    }
    private int proximoId;
    private FornecedorService fornecedorService; // Injetado via setter
    private EstoqueService estoqueService; // Injetado via setter
    private static final String ARQUIVO_PRODUTOS = "src/Arquivos/produtos.dat";

    public ProdutoService() { // Construtor sem FornecedorService
        this.produtos = new ArrayList<>();
        this.proximoId = 1;
        carregarProdutosDeArquivo();
        atualizarProximoId();
    }

    public void setEstoqueService(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    public void salvarProdutosEmArquivo() {
        try {
            File pasta = new File("Arquivos");
            if (!pasta.exists()) pasta.mkdir();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_PRODUTOS));
            oos.writeObject(produtos);
            oos.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar produtos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarProdutosDeArquivo() {
        File arquivo = new File(ARQUIVO_PRODUTOS);
        if (!arquivo.exists()) return;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_PRODUTOS));
            produtos = (List<Produto>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void atualizarProximoId() {
        int maiorId = 0;
        for (Produto p : produtos) {
            if (p.getId() > maiorId) maiorId = p.getId();
        }
        proximoId = maiorId + 1;
    }

    public void setFornecedorService(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    public int getContadorProdutos() {
        return produtos.size();
    }

    public void inserirProduto(Scanner scanner) {
        System.out.println("\n--- Insercao de Produto ---");
        System.out.print("Insira o nome do Produto: ");
        String nomeProduto = scanner.nextLine();
        System.out.print("Insira a descricao do Produto: ");
        String descricaoProduto = scanner.nextLine();

        if (fornecedorService == null || fornecedorService.getContadorFornecedores() == 0) {
            System.out.println("Nao ha fornecedores cadastrados. Cadastre um fornecedor primeiro.");
            return;
        }

        fornecedorService.consultaTodosFornecedores();

        System.out.print("Digite o codigo do fornecedor para este produto: ");
        int codigoFornecedor;
        try {
            codigoFornecedor = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do fornecedor. Operacao cancelada.");
            return;
        }

        Fornecedor fornecedorEncontrado = fornecedorService.buscarFornecedorPorId(codigoFornecedor);

        if (fornecedorEncontrado == null) {
            System.out.println("Fornecedor nao encontrado. Operacao cancelada.");
            return;
        }

        System.out.print("Insira o preco do Produto ( use '.' para decimais): ");
        double precoProduto;
        try {
            precoProduto = Double.parseDouble(scanner.nextLine());
            if (precoProduto < 0) {
                System.out.println("Preco nao pode ser negativo. Operacao cancelada.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o preco. Operacao cancelada.");
            return;
        }


        Produto novoProduto = new Produto(proximoId++, nomeProduto, descricaoProduto, fornecedorEncontrado, precoProduto);
        produtos.add(novoProduto);
        salvarProdutosEmArquivo();
        System.out.println("Produto inserido com sucesso ID: " + novoProduto.getId());
    }

    public void editarProduto(Scanner scanner) {
        if (produtos.isEmpty()) {
            System.out.println("Nao ha produtos cadastrados para editar.");
            return;
        }

        consultaTodosProdutos();

        System.out.print("Digite o ID do produto que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o ID. Operacao cancelada.");
            return;
        }

        Produto produto = buscarProdutoPorId(id);

        if (produto == null) {
            System.out.println("Produto nao encontrado");
            return;
        }

        System.out.println("Editando produto: " + produto.getNome());
        System.out.print("Novo nome (deixe em branco para manter o atual): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            produto.setNome(novoNome);
        }

        System.out.print("Nova descricao (deixe em branco para manter a atual): ");
        String novaDescricao = scanner.nextLine();
        if (!novaDescricao.trim().isEmpty()) {
            produto.setDescricao(novaDescricao);
        }

        System.out.print("Novo preco (deixe em branco para manter o atual, use '.' para decimais): "); // Edicao de preco
        String novoPrecoStr = scanner.nextLine();
        if (!novoPrecoStr.trim().isEmpty()) {
            try {
                // Substitui virgula por ponto para Double.parseDouble
                double novoPreco = Double.parseDouble(novoPrecoStr.replace(",", "."));
                if (novoPreco < 0) {
                    System.out.println("Preco nao pode ser negativo. Mantendo o atual.");
                } else {
                    produto.setPreco(novoPreco);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida para o preco. Mantendo o preco atual.");
            }
        }

        System.out.println("Deseja alterar o fornecedor? (S/N): ");
        String alterarFornecedor = scanner.nextLine();

        if (alterarFornecedor.equalsIgnoreCase("S")) {
            if (fornecedorService == null) {
                System.out.println("Servico de fornecedores nao disponivel.");
                return;
            }
            fornecedorService.consultaTodosFornecedores();

            System.out.print("Digite o codigo do novo fornecedor: ");
            int codigoFornecedor;
            try {
                codigoFornecedor = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida para o codigo do fornecedor. Mantendo o fornecedor atual.");
                return;
            }

            Fornecedor fornecedorID = fornecedorService.buscarFornecedorPorId(codigoFornecedor);

            if (fornecedorID == null) {
                System.out.println("Fornecedor nao encontrado. Mantendo o fornecedor atual.");
            } else {
                produto.setFornecedor(fornecedorID);
            }
        }

        System.out.println("Produto editado com sucesso.");
        salvarProdutosEmArquivo();
    }

    public void excluirProduto(Scanner scanner) {
    if (produtos.isEmpty()) {
        System.out.println("Nao ha produtos cadastrados para excluir.");
        return;
    }

    consultaTodosProdutos();

    System.out.print("Digite o ID do produto que deseja excluir: ");
    int id;
    try {
        id = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
        System.out.println("Entrada invalida para o ID. Operacao cancelada.");
        return;
    }

    Produto produtoParaExcluir = buscarProdutoPorId(id);

    if (produtoParaExcluir == null) {
        System.out.println("Produto nao encontrado");
        return;
    }

    // Verificação de estoque
    if (estoqueService != null) {
        Estoque itemEstoque = estoqueService.buscarItemEstoquePorProdutoId(produtoParaExcluir.getId());
        if (itemEstoque != null && itemEstoque.getQuantidade() > 0) {
            System.out.println("Nao e possível excluir o produto pois ele possui estoque (quantidade: " + itemEstoque.getQuantidade() + ")");
            return;
        }
    }

    System.out.print("Tem certeza que deseja excluir o produto '" + produtoParaExcluir.getNome() + "'? (S/N): ");
    String confirmacao = scanner.nextLine();

    if (confirmacao.equalsIgnoreCase("S")) {
        produtos.remove(produtoParaExcluir);
        System.out.println("Produto excluido com sucesso");
    } else {
        System.out.println("Operacao cancelada.");
    }
}

    public void consultaProduto(Scanner scanner) {
        if (produtos.isEmpty()) {
            System.out.println("Nao ha produtos cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Produto por ---");
        System.out.println("1. Nome ");
        System.out.println("2. Codigo");
        System.out.println("3. Descricao");
        System.out.println("4. Todos os produtos");
        System.out.print("Escolha uma opcao: ");
        int opcao;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida. Operacao cancelada.");
            return;
        }

        switch (opcao) {
            case 1:
                consultaProdutoPorNome(scanner);
                break;
            case 2:
                consultaProdutoPorCodigo(scanner);
                break;
            case 3:
                consultaProdutoPorDescricao(scanner);
                break;
            case 4:
                consultaTodosProdutos();
                break;
            default:
                System.out.println("Opcao invalida");
                break;
        }
    }

    private void consultaProdutoPorNome(Scanner scanner) {
        System.out.print("Digite o Nome do Produto: ");
        String nomeProduto = scanner.nextLine();

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Descricao | Fornecedor | Preco");
        System.out.println("----------------------------------------------------------------------------");

        boolean encontrou = false;

        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(nomeProduto.toLowerCase())) {
                System.out.printf("%-6d | %-15s | %-20s | %-15s | R$ %.2f%n",
                        p.getId(), p.getNome(), p.getDescricao(), p.getFornecedor().getNome(), p.getPreco());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum produto encontrado com esse nome.");
        }
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    private void consultaProdutoPorDescricao(Scanner scanner) {
        System.out.print("Digite uma palavra-chave da Descricao do Produto: ");
        String palavraChave = scanner.nextLine();

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Descricao | Fornecedor | Preco");
        System.out.println("----------------------------------------------------------------------------");

        boolean encontrou = false;

        for (Produto p : produtos) {
            if (p.getDescricao().toLowerCase().contains(palavraChave.toLowerCase())) {
                System.out.printf("%-6d | %-15s | %-20s | %-15s | R$ %.2f%n",
                        p.getId(), p.getNome(), p.getDescricao(), p.getFornecedor().getNome(), p.getPreco());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum produto encontrado com essa palavra-chave.");
        }
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    private void consultaProdutoPorCodigo(Scanner scanner) {
        System.out.print("Digite o Codigo do Produto: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do produto. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Descricao | Fornecedor | Preco");
        System.out.println("----------------------------------------------------------------------------");

        boolean encontrou = false;

        for (Produto p : produtos) {
            if (codigoProduto == p.getId()) {
                System.out.printf("%-6d | %-15s | %-20s | %-15s | R$ %.2f%n",
                        p.getId(), p.getNome(), p.getDescricao(), p.getFornecedor().getNome(), p.getPreco());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum produto encontrado com esse codigo.");
        }
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    public void consultaTodosProdutos() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Descricao | Fornecedor | Preco");
        System.out.println("----------------------------------------------------------------------------");

        for (Produto p : produtos) {
            System.out.printf("%-6d | %-15s | %-20s | %-15s | R$ %.2f%n",
                    p.getId(), p.getNome(), p.getDescricao(), p.getFornecedor().getNome(), p.getPreco());
        }

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    public Produto buscarProdutoPorId(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
    
    public List<Produto> buscarProdutosPorFornecedor(Fornecedor fornecedor) {
        List<Produto> produtosDoFornecedor = new ArrayList<>();
        for (Produto p : produtos) {
            // Verifica se o fornecedor do produto e o mesmo que o fornecedor passado
            if (p.getFornecedor().getId() == fornecedor.getId()) {
                produtosDoFornecedor.add(p);
            }
        }
        return produtosDoFornecedor;
    }
}