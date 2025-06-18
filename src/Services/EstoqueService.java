package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.Estoque;
import Models.Produto;

public class EstoqueService {

    private List<Estoque> itensEstoque;
    private ProdutoService produtoService; // Dependência de ProdutoService

    public EstoqueService(ProdutoService produtoService) {
        this.itensEstoque = new ArrayList<>();
        this.produtoService = produtoService;
    }

    public int getContadorItens() {
        return itensEstoque.size();
    }

    public void adicionarItemEstoque(Scanner scanner) {
        System.out.println("\n--- Adicao de Item ao Estoque ---");

        if (produtoService.getContadorProdutos() == 0) {
            System.out.println("Nao há produtos cadastrados. Cadastre um produto primeiro.");
            return;
        }

        produtoService.consultaTodosProdutos();

        System.out.print("Digite o codigo do produto para adicionar ao estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código do produto. Operacao cancelada.");
            return;
        }

        Produto produtoEncontrado = produtoService.buscarProdutoPorId(codigoProduto);

        if (produtoEncontrado == null) {
            System.out.println("Produto nao encontrado. Operacao cancelada.");
            return;
        }

        Estoque itemExistente = buscarItemEstoquePorProdutoId(produtoEncontrado.getId());
        if (itemExistente != null) {
            System.out.println("Este produto já existe no estoque. Quantidade atual: " + itemExistente.getQuantidade());
            System.out.print("Digite a quantidade a ser ADICIONADA: ");
            int quantidadeAdicional;
            try {
                quantidadeAdicional = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Operacao cancelada.");
                return;
            }

            if (quantidadeAdicional <= 0) {
                System.out.println("A quantidade a adicionar deve ser maior que zero. Operacao cancelada.");
                return;
            }
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidadeAdicional);
            System.out.println("Quantidade atualizada com sucesso. Nova quantidade: " + itemExistente.getQuantidade());
            return;
        }

        System.out.print("Digite a quantidade a ser adicionada: ");
        int quantidade;
        try {
            quantidade = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Operacao cancelada.");
            return;
        }

        if (quantidade <= 0) {
            System.out.println("A quantidade deve ser maior que zero. Operacao cancelada.");
            return;
        }

        Estoque novoItem = new Estoque(produtoEncontrado, quantidade);
        itensEstoque.add(novoItem);

        System.out.println("Item adicionado ao estoque com sucesso");
    }

    public void alterarItemEstoque(Scanner scanner) {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nao há itens no estoque para alterar.");
            return;
        }

        consultarTodosItensEstoque();

        System.out.print("Digite o codigo do produto que deseja alterar no estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código do produto. Operacao cancelada.");
            return;
        }

        Estoque item = buscarItemEstoquePorProdutoId(codigoProduto);

        if (item == null) {
            System.out.println("Produto nao encontrado no estoque");
            return;
        }

        System.out.println("Alterando quantidade do produto: " + item.getProduto().getNome());
        System.out.println("Quantidade atual: " + item.getQuantidade());
        System.out.print("Nova quantidade: ");
        int novaQuantidade;
        try {
            novaQuantidade = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para a nova quantidade. Operacao cancelada.");
            return;
        }

        if (novaQuantidade < 0) {
            System.out.println("A quantidade nao pode ser negativa.");
            return;
        }

        if (novaQuantidade == 0) {
            System.out.print("Tem certeza que deseja remover o item completamente do estoque? (S/N): ");
            String confirmacao = scanner.nextLine();
            if (confirmacao.equalsIgnoreCase("S")) {
                itensEstoque.remove(item);
                System.out.println("Item removido do estoque.");
                return;
            } else {
                System.out.println("Operacao cancelada.");
                return;
            }
        }

        item.setQuantidade(novaQuantidade);
        System.out.println("Quantidade atualizada com sucesso");
    }

    public void excluirItemEstoque(Scanner scanner) {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nao há itens no estoque para excluir.");
            return;
        }

        consultarTodosItensEstoque();

        System.out.print("Digite o codigo do produto que deseja excluir do estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código do produto. Operacao cancelada.");
            return;
        }

        Estoque itemParaExcluir = buscarItemEstoquePorProdutoId(codigoProduto);

        if (itemParaExcluir == null) {
            System.out.println("Produto nao encontrado no estoque");
            return;
        }

        System.out.print("Tem certeza que deseja excluir o item '" + itemParaExcluir.getProduto().getNome() + "' do estoque? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            itensEstoque.remove(itemParaExcluir);
            System.out.println("Item excluído do estoque com sucesso");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultarItemEstoque(Scanner scanner) {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nao há itens no estoque para consultar.");
            return;
        }

        System.out.println("\n--- Consultar Estoque por ---");
        System.out.println("1. Nome do Produto");
        System.out.println("2. Codigo do Produto");
        System.out.println("3. Todos os itens");
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
                consultarItemEstoquePorNome(scanner);
                break;
            case 2:
                consultarItemEstoquePorCodigo(scanner);
                break;
            case 3:
                consultarTodosItensEstoque();
                break;
            default:
                System.out.println("Opcao invalida");
                break;
        }
    }

    private void consultarItemEstoquePorNome(Scanner scanner) {
        System.out.print("Digite o nome do produto: ");
        String nomeProduto = scanner.nextLine();

        System.out.println("\n--- Lista de Itens no Estoque ---");
        System.out.println("Codigo | Nome | Fornecedor | Quantidade | Preço Unitário"); // Preço adicionado
        System.out.println("---------------------------------------------------------------------");

        boolean encontrou = false;

        for (Estoque item : itensEstoque) {
            if (item.getProduto().getNome().toLowerCase().contains(nomeProduto.toLowerCase())) {
                System.out.printf("%-6d | %-15s | %-15s | %-10d | R$ %.2f%n",
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getProduto().getFornecedor().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco()); // Preço do produto
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum item encontrado com esse nome.");
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Total de itens no estoque: " + itensEstoque.size());
    }

    private void consultarItemEstoquePorCodigo(Scanner scanner) {
        System.out.print("Digite o codigo do produto: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código do produto. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Itens no Estoque ---");
        System.out.println("Codigo | Nome | Fornecedor | Quantidade | Preço Unitário"); // Preço adicionado
        System.out.println("---------------------------------------------------------------------");

        boolean encontrou = false;

        for (Estoque item : itensEstoque) {
            if (item.getProduto().getId() == codigoProduto) {
                System.out.printf("%-6d | %-15s | %-15s | %-10d | R$ %.2f%n",
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getProduto().getFornecedor().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco()); // Preço do produto
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum item encontrado com esse codigo.");
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Total de itens no estoque: " + itensEstoque.size());
    }

    public void consultarTodosItensEstoque() {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nenhum item encontrado no estoque.");
            return;
        }

        System.out.println("\n--- Lista de Itens no Estoque ---");
        System.out.println("Codigo | Nome | Fornecedor | Quantidade | Preço Unitário"); // Preço adicionado
        System.out.println("---------------------------------------------------------------------");

        for (Estoque item : itensEstoque) {
            System.out.printf("%-6d | %-15s | %-15s | %-10d | R$ %.2f%n",
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getProduto().getFornecedor().getNome(),
                    item.getQuantidade(),
                    item.getProduto().getPreco()); // Preço do produto
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Total de itens no estoque: " + itensEstoque.size());
    }

    // Auxiliar para PedidoService e outras verificações de estoque
    public Estoque buscarItemEstoquePorProdutoId(int produtoId) {
        for (Estoque item : itensEstoque) {
            if (item.getProduto().getId() == produtoId) {
                return item;
            }
        }
        return null;
    }
    
    // Método para decrementar quantidade em estoque após um pedido
    public boolean decrementarQuantidade(Produto produto, int quantidade) {
        Estoque item = buscarItemEstoquePorProdutoId(produto.getId());
        if (item != null && item.getQuantidade() >= quantidade) {
            item.setQuantidade(item.getQuantidade() - quantidade);
            return true;
        }
        return false;
    }
    
    // Método para incrementar quantidade (por exemplo, em caso de cancelamento de pedido)
    public void incrementarQuantidade(Produto produto, int quantidade) {
        Estoque item = buscarItemEstoquePorProdutoId(produto.getId());
        if (item != null) {
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            // Se o item não existir no estoque, adiciona-o (pode ser uma nova entrada no estoque)
            itensEstoque.add(new Estoque(produto, quantidade));
        }
    }
}