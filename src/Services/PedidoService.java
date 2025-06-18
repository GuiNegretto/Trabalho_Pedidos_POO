package Services;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.*;
import Utils.PedidoStatus;

public class PedidoService {
    private List<Pedido> pedidos;
    private int proximoId;
    private ClienteService clienteService;
    private ProdutoService produtoService;
    private EstoqueService estoqueService;

    public PedidoService(ClienteService clienteService, ProdutoService produtoService, EstoqueService estoqueService) {
        this.pedidos = new ArrayList<>();
        this.proximoId = 1;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
    }

    public int getContadorPedidos() {
        return pedidos.size();
    }

    public void realizarNovoPedido(Scanner scanner) {
        System.out.println("\n--- Realizar Novo Pedido ---");

        if (clienteService.getContadorClientes() == 0) {
            System.out.println("Nao ha clientes cadastrados. Cadastre um cliente primeiro.");
            return;
        }
        clienteService.consultaTodosClientes();
        System.out.print("Digite o ID do cliente que está realizando o pedido: ");
        int idCliente;
        try {
            idCliente = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de cliente inválido. Operacao cancelada.");
            return;
        }

        Cliente cliente = clienteService.buscarClientePorId(idCliente);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado. Operacao cancelada.");
            return;
        }

        LocalDate dataPedido = LocalDate.now();
        LocalDate dataEntrega = null;
        System.out.print("Digite a data de entrega (AAAA-MM-DD): ");
        String dataEntregaStr = scanner.nextLine();
        try {
            dataEntrega = LocalDate.parse(dataEntregaStr);
            if (dataEntrega.isBefore(dataPedido)) {
                System.out.println("Data de entrega nao pode ser anterior a data do pedido. Operacao cancelada.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use AAAA-MM-DD. Operacao cancelada.");
            return;
        }

        Pedido novoPedido = new Pedido(proximoId++, cliente, dataPedido, dataEntrega, PedidoStatus.NOVO);

        boolean adicionarMaisItens = true;
        while (adicionarMaisItens) {
            System.out.println("\n--- Adicionar Item ao Pedido ---");
            if (produtoService.getContadorProdutos() == 0) {
                System.out.println("Nao ha produtos cadastrados.");
                break;
            }
            produtoService.consultaTodosProdutos();
            System.out.print("Digite o ID do produto para adicionar (0 para finalizar): ");
            int idProduto;
            try {
                idProduto = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ID de produto inválido. Tente novamente.");
                continue;
            }

            if (idProduto == 0) {
                adicionarMaisItens = false;
                break;
            }

            Produto produto = produtoService.buscarProdutoPorId(idProduto);
            if (produto == null) {
                System.out.println("Produto nao encontrado.");
                continue;
            }

            Estoque itemEstoque = estoqueService.buscarItemEstoquePorProdutoId(produto.getId());
            if (itemEstoque == null || itemEstoque.getQuantidade() == 0) {
                System.out.println("Produto fora de estoque ou nao cadastrado no estoque.");
                continue;
            }

            System.out.print("Digite a quantidade: ");
            int quantidade;
            try {
                quantidade = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Quantidade inválida. Tente novamente.");
                continue;
            }

            if (quantidade <= 0) {
                System.out.println("Quantidade deve ser maior que zero.");
                continue;
            }

            if (quantidade > itemEstoque.getQuantidade()) {
                System.out.println("Quantidade em estoque insuficiente. Disponível: " + itemEstoque.getQuantidade());
                continue;
            }

            // O preço do item no pedido é o preço atual do produto no momento da adição
            ItemPedido itemPedido = new ItemPedido(produto, quantidade, produto.getPreco());
            novoPedido.adicionarItem(itemPedido);

            // Decrementa a quantidade do produto no estoque
            estoqueService.decrementarQuantidade(produto, quantidade);

            System.out.println("Item '" + produto.getNome() + "' adicionado ao pedido.");
            System.out.print("Adicionar mais itens? (S/N): ");
            String continuar = scanner.nextLine();
            if (!continuar.equalsIgnoreCase("S")) {
                adicionarMaisItens = false;
            }
        }

        if (novoPedido.getItens().isEmpty()) {
            System.out.println("Pedido cancelado: nenhum item adicionado.");
            // Se o pedido for cancelado, talvez você queira reverter o decremento do estoque
            // Isso dependerá da lógica de negócio. Para simplicidade, assumimos que se itens foram
            // adicionados, o pedido será salvo.
            return;
        }

        pedidos.add(novoPedido);
        System.out.printf("Pedido %d criado com sucesso para o cliente %s. Total: R$ %.2f%n",
                novoPedido.getId(), novoPedido.getCliente().getNome(), novoPedido.calcularTotal());
    }

    public void consultaPedido(Scanner scanner) {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        System.out.println("\n--- Consultar Pedido por ---");
        System.out.println("1 - ID do Pedido");
        System.out.println("2 - Cliente");
        System.out.println("3 - Todos os Pedidos");
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
                consultaPedidoPorId(scanner);
                break;
            case 2:
                consultarPedidosPorCliente(scanner);
                break;
            case 3:
                consultaTodosPedidos();
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
        }
    }

    private void consultaPedidoPorId(Scanner scanner) {
        System.out.print("Digite o ID do Pedido: ");
        int idPedido;
        try {
            idPedido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de pedido inválido. Operacao cancelada.");
            return;
        }

        Pedido pedido = buscarPedidoPorId(idPedido);
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }
        exibirDetalhesPedido(pedido);
    }

    public void consultarPedidosPorCliente(Scanner scanner) { // Público para ser chamado pelo menu Cliente
        if (clienteService.getContadorClientes() == 0) {
            System.out.println("Nao ha clientes cadastrados.");
            return;
        }
        clienteService.consultaTodosClientes();
        System.out.print("Digite o ID do cliente para consultar pedidos: ");
        int idCliente;
        try {
            idCliente = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de cliente inválido. Operacao cancelada.");
            return;
        }

        Cliente cliente = clienteService.buscarClientePorId(idCliente);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado.");
            return;
        }

        List<Pedido> pedidosDoCliente = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getCliente().getId() == cliente.getId()) {
                pedidosDoCliente.add(p);
            }
        }

        if (pedidosDoCliente.isEmpty()) {
            System.out.println("Nenhum pedido encontrado para o cliente: " + cliente.getNome());
            return;
        }

        System.out.println("\n--- Pedidos do Cliente: " + cliente.getNome() + " ---");
        for (Pedido p : pedidosDoCliente) {
            exibirDetalhesPedido(p);
        }
    }

    public void consultaTodosPedidos() {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        System.out.println("\n--- Lista de Todos os Pedidos ---");
        for (Pedido p : pedidos) {
            exibirDetalhesPedido(p);
            System.out.println("-------------------------------------");
        }
    }

    public void atualizarSituacaoPedido(Scanner scanner) {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido para atualizar.");
            return;
        }

        consultaTodosPedidos();
        System.out.print("Digite o ID do pedido que deseja atualizar a situacao: ");
        int idPedido;
        try {
            idPedido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de pedido inválido. Operacao cancelada.");
            return;
        }

        Pedido pedido = buscarPedidoPorId(idPedido);
        if (pedido == null) {
            System.out.println("Pedido nao encontrado.");
            return;
        }

        System.out.println("Situacao atual do Pedido " + pedido.getId() + ": " + pedido.getSituacao());
        System.out.println("Novas situacoes disponiveis:");
        for (PedidoStatus status : PedidoStatus.values()) {
            System.out.println("- " + status.name());
        }
        System.out.print("Digite a nova situacao: ");
        String novaSituacaoStr = scanner.nextLine().toUpperCase();

        try {
            PedidoStatus novaSituacao = PedidoStatus.valueOf(novaSituacaoStr);

            // Lógica para lidar com alterações de estoque ao mudar status
            if (pedido.getSituacao() == PedidoStatus.NOVO && novaSituacao == PedidoStatus.CANCELADO) {
                // Se o pedido era NOVO e foi CANCELADO, retorna itens para o estoque
                for (ItemPedido item : pedido.getItens()) {
                    estoqueService.incrementarQuantidade(item.getProduto(), item.getQuantidade());
                }
                System.out.println("Itens do pedido " + pedido.getId() + " retornados ao estoque devido ao cancelamento.");
            } else if (pedido.getSituacao() == PedidoStatus.CANCELADO && novaSituacao == PedidoStatus.NOVO) {
                 System.out.println("Não é possível reverter um pedido CANCELADO para NOVO sem verificar estoque manualmente.");
                 // Em um sistema real, você faria uma verificação de estoque aqui para ver se é possível reverter
                 return;
            }


            pedido.setSituacao(novaSituacao);
            System.out.println("Situacao do pedido " + pedido.getId() + " atualizada para: " + novaSituacao);
        } catch (IllegalArgumentException e) {
            System.out.println("Situacao inválida. Por favor, escolha uma das opções listadas.");
        }
    }

    private void exibirDetalhesPedido(Pedido pedido) {
        System.out.println("-------------------------------------");
        System.out.println("ID Pedido: " + pedido.getId());
        System.out.println("Cliente: " + pedido.getCliente().getNome() + " (ID: " + pedido.getCliente().getId() + ")");
        System.out.println("Data do Pedido: " + pedido.getDataPedido());
        System.out.println("Data de Entrega: " + pedido.getDataEntrega());
        System.out.println("Situacao: " + pedido.getSituacao());
        System.out.println("Itens do Pedido:");
        if (pedido.getItens().isEmpty()) {
            System.out.println("  Nenhum item neste pedido.");
        } else {
            System.out.println("  Produto (ID) | Qtd | Preço Unit. | Subtotal");
            System.out.println("  ----------------------------------------------");
            for (ItemPedido item : pedido.getItens()) {
                System.out.printf("  %-15s | %-3d | R$ %-8.2f | R$ %.2f%n",
                        item.getProduto().getNome() + " (" + item.getProduto().getId() + ")",
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal());
            }
            System.out.println("  ----------------------------------------------");
            System.out.printf("  Total do Pedido: R$ %.2f%n", pedido.calcularTotal());
        }
    }

    public Pedido buscarPedidoPorId(int id) {
        for (Pedido p : pedidos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}