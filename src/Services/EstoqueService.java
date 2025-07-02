package Services;

import Models.Estoque;
import Models.Produto;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.*;

public class EstoqueService {

    private List<Estoque> itensEstoque;
    private ProdutoService produtoService; // Dependencia de ProdutoService
    private static final String ARQUIVO_ESTOQUE = "src/Arquivos/estoque.dat";

    public EstoqueService(ProdutoService produtoService) {
        this.itensEstoque = new ArrayList<>();
        this.produtoService = produtoService;
        carregarEstoqueDeArquivo();
    }

    public void salvarEstoqueEmArquivo() {
        try {
            File pasta = new File("Arquivos");
            if (!pasta.exists()) pasta.mkdir();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_ESTOQUE));
            oos.writeObject(itensEstoque);
            oos.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar estoque: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarEstoqueDeArquivo() {
        File arquivo = new File(ARQUIVO_ESTOQUE);
        if (!arquivo.exists()) return;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_ESTOQUE));
            itensEstoque = (List<Estoque>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar estoque: " + e.getMessage());
        }
    }

    public int getContadorItens() {
        return itensEstoque.size();
    }

    public void adicionarItemEstoque(Scanner scanner) {
        System.out.println("\n--- Adicao de Item ao Estoque ---");

        if (produtoService.getContadorProdutos() == 0) {
            System.out.println("Nao ha produtos cadastrados. Cadastre um produto primeiro.");
            return;
        }

        produtoService.consultaTodosProdutos();

        System.out.print("Digite o codigo do produto para adicionar ao estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do produto. Operacao cancelada.");
            return;
        }

        Produto produtoEncontrado = produtoService.buscarProdutoPorId(codigoProduto);

        if (produtoEncontrado == null) {
            System.out.println("Produto nao encontrado. Operacao cancelada.");
            return;
        }

        Estoque itemExistente = buscarItemEstoquePorProdutoId(produtoEncontrado.getId());
        if (itemExistente != null) {
            System.out.println("Este produto ja existe no estoque. Quantidade atual: " + itemExistente.getQuantidade());
            System.out.print("Digite a quantidade a ser ADICIONADA: ");
            int quantidadeAdicional;
            try {
                quantidadeAdicional = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Operacao cancelada.");
                return;
            }

            if (quantidadeAdicional <= 0) {
                System.out.println("A quantidade a adicionar deve ser maior que zero. Operacao cancelada.");
                return;
            }
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidadeAdicional);
            salvarEstoqueEmArquivo();
            System.out.println("Quantidade atualizada com sucesso. Nova quantidade: " + itemExistente.getQuantidade());
            return;
        }

        System.out.print("Digite a quantidade a ser adicionada: ");
        int quantidade;
        try {
            quantidade = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida. Operacao cancelada.");
            return;
        }

        if (quantidade <= 0) {
            System.out.println("A quantidade deve ser maior que zero. Operacao cancelada.");
            return;
        }

        Estoque novoItem = new Estoque(produtoEncontrado, quantidade);
        itensEstoque.add(novoItem);
        salvarEstoqueEmArquivo();
        System.out.println("Item adicionado ao estoque com sucesso");
    }

    public void alterarItemEstoque(Scanner scanner) {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nao ha itens no estoque para alterar.");
            return;
        }

        consultarTodosItensEstoque();

        System.out.print("Digite o codigo do produto que deseja alterar no estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do produto. Operacao cancelada.");
            return;
        }

        Estoque item = buscarItemEstoquePorProdutoId(codigoProduto);

        if (item == null) {
            System.out.println("Produto nao encontrado no estoque");
            return;
        }
        // Apos alteracao
        salvarEstoqueEmArquivo();

        System.out.println("Alterando quantidade do produto: " + item.getProduto().getNome());
        System.out.println("Quantidade atual: " + item.getQuantidade());
        System.out.print("Nova quantidade: ");
        int novaQuantidade;
        try {
            novaQuantidade = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para a nova quantidade. Operacao cancelada.");
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
            System.out.println("Nao ha itens no estoque para excluir.");
            return;
        }

        consultarTodosItensEstoque();

        System.out.print("Digite o codigo do produto que deseja excluir do estoque: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do produto. Operacao cancelada.");
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
            System.out.println("Item excluido do estoque com sucesso");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultarItemEstoque(Scanner scanner) {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nao ha itens no estoque para consultar.");
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
            System.out.println("Entrada invalida. Operacao cancelada.");
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
        System.out.println("ID | Produto | Quantidade | Disponibilidade");
        boolean encontrou = false;

        for (Estoque item : itensEstoque) {
            if (item.getProduto().getNome().toLowerCase().contains(nomeProduto.toLowerCase())) {
                String disponibilidade = item.getQuantidade() > 0 ? "Disponivel" : "Indisponivel";
                System.out.printf("%d | %s | %d | %s\n",
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        disponibilidade);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum item encontrado com esse nome.");
        }
    }

    private void consultarItemEstoquePorCodigo(Scanner scanner) {
        System.out.print("Digite o codigo do produto: ");
        int codigoProduto;
        try {
            codigoProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo do produto. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Itens no Estoque ---");
        System.out.println("ID | Produto | Quantidade | Disponibilidade");

        boolean encontrou = false;

        for (Estoque item : itensEstoque) {
            if (item.getProduto().getId() == codigoProduto) {
                String disponibilidade = item.getQuantidade() > 0 ? "Disponivel" : "Indisponivel";
                System.out.printf("%d | %s | %d | %s\n",
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        disponibilidade);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum item encontrado com esse codigo.");
        }
    }

    public void consultarTodosItensEstoque() {
        if (itensEstoque.isEmpty()) {
            System.out.println("Nenhum item no estoque.");
            return;
        }
        System.out.println("\n--- Estoque Atual ---");
        System.out.println("ID | Produto | Quantidade | Disponibilidade");
        for (Estoque item : itensEstoque) {
            String disponibilidade = item.getQuantidade() > 0 ? "Disponivel" : "Indisponivel";
            System.out.printf("%d | %s | %d | %s\n",
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    disponibilidade);
        }
    }

    // Auxiliar para PedidoService e outras verificacoes de estoque
    public Estoque buscarItemEstoquePorProdutoId(int idProduto) {
        for (Estoque item : itensEstoque) {
            if (item.getProduto().getId() == idProduto) {
                return item;
            }
        }
        return null;
    }

    // Metodo auxiliar para verificar disponibilidade
    public boolean produtoDisponivel(int idProduto) {
        Estoque item = buscarItemEstoquePorProdutoId(idProduto);
        return item != null && item.getQuantidade() > 0;
    }

    // Metodo para decrementar quantidade em estoque apos um pedido
    public boolean decrementarQuantidade(Produto produto, int quantidade) {
        Estoque item = buscarItemEstoquePorProdutoId(produto.getId());
        if (item != null && item.getQuantidade() >= quantidade) {
            item.setQuantidade(item.getQuantidade() - quantidade);
            salvarEstoqueEmArquivo();
            return true;
        }
        return false;
    }
    
    // Metodo para incrementar quantidade (por exemplo, em caso de cancelamento de pedido)
    public void incrementarQuantidade(Produto produto, int quantidade) {
        Estoque item = buscarItemEstoquePorProdutoId(produto.getId());
        if (item != null) {
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            itensEstoque.add(new Estoque(produto, quantidade));
        }
        salvarEstoqueEmArquivo();
    }
}