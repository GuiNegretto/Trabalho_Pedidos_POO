package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.Produto;
import Models.Fornecedor;

public class ProdutoService {

    private List<Produto> produtos;
    private int proximoId;
    private FornecedorService fornecedorService; // Injetado via setter

    public ProdutoService() { // Construtor sem FornecedorService
        this.produtos = new ArrayList<>();
        this.proximoId = 1;
    }

    public void setFornecedorService(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    public int getContadorProdutos() {
        return produtos.size();
    }

    public void inserirProduto(Scanner scanner) {
        System.out.println("\n--- Inserção de Produto ---");
        System.out.print("Insira o nome do Produto: ");
        String nomeProduto = scanner.nextLine();

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
            System.out.println("Entrada inválida para o código do fornecedor. Operacao cancelada.");
            return;
        }

        Fornecedor fornecedorEncontrado = fornecedorService.buscarFornecedorPorId(codigoFornecedor);

        if (fornecedorEncontrado == null) {
            System.out.println("Fornecedor nao encontrado. Operacao cancelada.");
            return;
        }

        System.out.print("Insira o preço do Produto: "); // Nova entrada: preço
        double precoProduto;
        try {
            precoProduto = Double.parseDouble(scanner.nextLine());
            if (precoProduto < 0) {
                System.out.println("Preço não pode ser negativo. Operacao cancelada.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o preço. Operacao cancelada.");
            return;
        }


        Produto novoProduto = new Produto(proximoId++, nomeProduto, fornecedorEncontrado, precoProduto);
        produtos.add(novoProduto);

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
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
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

        System.out.print("Novo preço (deixe em branco para manter o atual, use ',' para decimais): "); // Edição de preço
        String novoPrecoStr = scanner.nextLine();
        if (!novoPrecoStr.trim().isEmpty()) {
            try {
                // Substitui vírgula por ponto para Double.parseDouble
                double novoPreco = Double.parseDouble(novoPrecoStr.replace(",", "."));
                if (novoPreco < 0) {
                    System.out.println("Preço não pode ser negativo. Mantendo o atual.");
                } else {
                    produto.setPreco(novoPreco);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida para o preço. Mantendo o preço atual.");
            }
        }

        System.out.println("Deseja alterar o fornecedor? (S/N): ");
        String alterarFornecedor = scanner.nextLine();

        if (alterarFornecedor.equalsIgnoreCase("S")) {
            if (fornecedorService == null) {
                System.out.println("Serviço de fornecedores não disponível.");
                return;
            }
            fornecedorService.consultaTodosFornecedores();

            System.out.print("Digite o codigo do novo fornecedor: ");
            int codigoFornecedor;
            try {
                codigoFornecedor = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida para o código do fornecedor. Mantendo o fornecedor atual.");
                return;
            }

            Fornecedor fornecedorID = fornecedorService.buscarFornecedorPorId(codigoFornecedor);

            if (fornecedorID == null) {
                System.out.println("Fornecedor nao encontrado. Mantendo o fornecedor atual.");
            } else {
                produto.setFornecedor(fornecedorID);
            }
        }

        System.out.println("Produto atualizado com sucesso");
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
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
            return;
        }

        Produto produtoParaExcluir = buscarProdutoPorId(id);

        if (produtoParaExcluir == null) {
            System.out.println("Produto nao encontrado");
            return;
        }

        // Em um sistema real, você também verificaria se este produto está em algum pedido
        // ou se ele existe no estoque antes de permitir a exclusão.
        // Para simplificar, permitiremos a exclusão sem essa verificação aqui.

        System.out.print("Tem certeza que deseja excluir o produto '" + produtoParaExcluir.getNome() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            produtos.remove(produtoParaExcluir);
            System.out.println("Produto excluído com sucesso");
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
        System.out.println("3. Todos os produtos");
        System.out.print("Escolha uma opcao: ");
        int opcao;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Operacao cancelada.");
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
        System.out.println("Codigo | Nome | Fornecedor | Preço"); // Coluna Preço adicionada
        System.out.println("------------------------------------------------------------");

        boolean encontrou = false;

        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(nomeProduto.toLowerCase())) {
                System.out.printf("%-6d | %-15s | %-15s | R$ %.2f%n",
                        p.getId(), p.getNome(), p.getFornecedor().getNome(), p.getPreco());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum produto encontrado com esse nome.");
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    private void consultaProdutoPorCodigo(Scanner scanner) {
        System.out.print("Digite o Codigo do Produto: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código do produto. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Fornecedor | Preço"); // Coluna Preço adicionada
        System.out.println("------------------------------------------------------------");

        boolean encontrou = false;

        for (Produto p : produtos) {
            if (codigoProduto == p.getId()) {
                System.out.printf("%-6d | %-15s | %-15s | R$ %.2f%n",
                        p.getId(), p.getNome(), p.getFornecedor().getNome(), p.getPreco());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum produto encontrado com esse codigo.");
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total de produtos: " + produtos.size());
    }

    public void consultaTodosProdutos() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Produtos ---");
        System.out.println("Codigo | Nome | Fornecedor | Preço"); // Coluna Preço adicionada
        System.out.println("------------------------------------------------------------");

        for (Produto p : produtos) {
            System.out.printf("%-6d | %-15s | %-15s | R$ %.2f%n",
                    p.getId(), p.getNome(), p.getFornecedor().getNome(), p.getPreco());
        }

        System.out.println("------------------------------------------------------------");
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
            // Verifica se o fornecedor do produto é o mesmo que o fornecedor passado
            if (p.getFornecedor().getId() == fornecedor.getId()) {
                produtosDoFornecedor.add(p);
            }
        }
        return produtosDoFornecedor;
    }
}