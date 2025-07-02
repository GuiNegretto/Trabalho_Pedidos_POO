package Services;

import Models.*;
import Utils.PedidoStatus;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Excecao customizada para estoque insuficiente
class EstoqueInsuficienteException extends Exception {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}

public class PedidoService {
    private List<Pedido> pedidos;
    private int proximoId;
    private ClienteService clienteService;
    private ProdutoService produtoService;
    private EstoqueService estoqueService;
    private static final String ARQUIVO_PEDIDOS = "src/Arquivos/pedidos.dat";

    public PedidoService(ClienteService clienteService, ProdutoService produtoService, EstoqueService estoqueService) {
        this.pedidos = new ArrayList<>();
        this.proximoId = 1;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        carregarPedidosDeArquivo();
        atualizarProximoId();
    }

    public void salvarPedidosEmArquivo() {
        try {
            File pasta = new File("Arquivos");
            if (!pasta.exists()) pasta.mkdir();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_PEDIDOS));
            oos.writeObject(pedidos);
            oos.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar pedidos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarPedidosDeArquivo() {
        File arquivo = new File(ARQUIVO_PEDIDOS);
        if (!arquivo.exists()) return;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_PEDIDOS));
            pedidos = (List<Pedido>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    private void atualizarProximoId() {
        int maiorId = 0;
        for (Pedido p : pedidos) {
            if (p.getId() > maiorId) maiorId = p.getId();
        }
        proximoId = maiorId + 1;
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
        System.out.print("Digite o ID do cliente que esta realizando o pedido: ");
        int idCliente;
        try {
            idCliente = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de cliente invalido. Operacao cancelada.");
            return;
        }

        Cliente cliente = clienteService.buscarClientePorId(idCliente);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado. Operacao cancelada.");
            return;
        }

        LocalDate dataPedido = LocalDate.now();
        LocalDate dataEntrega = null;
        System.out.print("Digite previsao de entrega (AAAA-MM-DD): ");
        String dataEntregaStr = scanner.nextLine();
        try {
            dataEntrega = LocalDate.parse(dataEntregaStr);
            if (dataEntrega.isBefore(dataPedido)) {
                System.out.println("Data de entrega nao pode ser anterior a data do pedido. Operacao cancelada.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data invalido. Use AAAA-MM-DD. Operacao cancelada.");
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
            // Exibe produtos com a quantidade em estoque
            List<Produto> produtos = produtoService.getProdutos();
            System.out.println("Codigo | Nome | Preço | Estoque");
            System.out.println("------------------------------------------");
            for (Produto p : produtos) {
                Estoque itemEstoque = estoqueService.buscarItemEstoquePorProdutoId(p.getId());
                int qtdEstoque = (itemEstoque != null) ? itemEstoque.getQuantidade() : 0;
                System.out.printf("%-6d | %-15s | R$ %-8.2f | %-7d\n",
                        p.getId(), p.getNome(), p.getPreco(), qtdEstoque);
            }
            System.out.println("------------------------------------------");
            System.out.print("Digite o ID do produto para adicionar (0 para finalizar): ");
            int idProduto;
            try {
                idProduto = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ID de produto invalido. Tente novamente.");
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
                System.out.println("Quantidade invalida. Tente novamente.");
                continue;
            }

            try {
                if (quantidade > itemEstoque.getQuantidade()) {
                    throw new EstoqueInsuficienteException("Quantidade em estoque insuficiente. Quantidade disponivel: " + itemEstoque.getQuantidade());
                }
                double precoUnitario = produto.getPreco();
                ItemPedido itemPedido = new ItemPedido(produto, quantidade, precoUnitario);
                novoPedido.adicionarItem(itemPedido);
                estoqueService.decrementarQuantidade(produto, quantidade);
                System.out.println("Item adicionado ao pedido.");
            } catch (EstoqueInsuficienteException e) {
                System.out.println(e.getMessage());
                System.out.print("Deseja adicionar a quantidade maxima disponivel? (S/N): ");
                String resposta = scanner.nextLine();
                if (resposta.equalsIgnoreCase("S")) {
                    int maxDisponivel = itemEstoque.getQuantidade();
                    if (maxDisponivel > 0) {
                        double precoUnitario = produto.getPreco();
                        ItemPedido itemPedido = new ItemPedido(produto, maxDisponivel, precoUnitario);
                        novoPedido.adicionarItem(itemPedido);
                        estoqueService.decrementarQuantidade(produto, maxDisponivel);
                        System.out.println("Item adicionado ao pedido com quantidade maxima disponivel.");
                    } else {
                        System.out.println("Nenhuma unidade disponivel.");
                    }
                }
            }

            System.out.print("Adicionar mais itens? (S/N): ");
            String continuar = scanner.nextLine();
            if (!continuar.equalsIgnoreCase("S")) {
                adicionarMaisItens = false;
            }
        }

        if (novoPedido.getItens().isEmpty()) {
            System.out.println("Pedido cancelado: nenhum item adicionado.");
            return;
        }

        // Exibe resumo do pedido antes de confirmar
        System.out.println("\n--- Resumo do Pedido ---");
        System.out.println("Cliente: " + novoPedido.getCliente().getNome());
        System.out.println("Data do Pedido: " + novoPedido.getDataPedido());
        System.out.println("Data de Entrega: " + novoPedido.getDataEntrega());
        System.out.println("Itens:");
        System.out.println("Produto | Qtd | Preço Unit. | Subtotal");
        for (ItemPedido item : novoPedido.getItens()) {
            System.out.printf("%s | %d | R$ %.2f | R$ %.2f\n",
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal());
        }
        System.out.printf("Total: R$ %.2f\n", novoPedido.calcularTotal());
        System.out.printf("ICMS (17%%): R$ %.2f\n", novoPedido.calcularValorIcms(novoPedido.calcularTotal()));
        System.out.printf("Total com ICMS: R$ %.2f\n", novoPedido.calcularTotalComIcms(novoPedido.calcularTotal()));

        System.out.print("Confirmar pedido? (S/N): ");
        String confirmacao = scanner.nextLine();
        if (!confirmacao.equalsIgnoreCase("S")) {
            // Devolve ao estoque
            for (ItemPedido item : novoPedido.getItens()) {
                estoqueService.incrementarQuantidade(item.getProduto(), item.getQuantidade());
            }
            System.out.println("Pedido cancelado pelo usuário.");
            return;
        }

        pedidos.add(novoPedido);
        salvarPedidosEmArquivo();
        System.out.printf("Pedido %d criado com sucesso para o cliente %s. \n Total sem ICMS: R$ %.2f%n  Valor do ICMS: R$ %.2f \n Total com ICMS: R$ %.2f",
                novoPedido.getId(), novoPedido.getCliente().getNome(), novoPedido.calcularTotal(), novoPedido.calcularValorIcms(novoPedido.calcularTotal()), novoPedido.calcularTotalComIcms(novoPedido.calcularTotal()));
    }

    public void consultaPedido(Scanner scanner) {
        System.out.println("\n--- Consulta de Pedidos ---");
        System.out.println("1. Consultar por numero do pedido");
        System.out.println("2. Consultar por intervalo de datas");
        System.out.print("Escolha uma opcao: ");
        int opcao = 0;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opcao invalida.");
            return;
        }
        switch (opcao) {
            case 1:
                consultaPedidoPorId(scanner);
                break;
            case 2:
                consultaPedidosPorIntervaloDeDatas(scanner);
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    // Nova funcao para consulta por intervalo de datas
    private void consultaPedidosPorIntervaloDeDatas(Scanner scanner) {
        System.out.print("Digite a data inicial (AAAA-MM-DD): ");
        String dataInicialStr = scanner.nextLine();
        System.out.print("Digite a data final (AAAA-MM-DD): ");
        String dataFinalStr = scanner.nextLine();
        LocalDate dataInicial, dataFinal;
        try {
            dataInicial = LocalDate.parse(dataInicialStr);
            dataFinal = LocalDate.parse(dataFinalStr);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data invalido.");
            return;
        }
        System.out.println("\n--- Pedidos no intervalo ---");
        for (Pedido pedido : pedidos) {
            if ((pedido.getDataPedido().isEqual(dataInicial) || pedido.getDataPedido().isAfter(dataInicial)) &&
                (pedido.getDataPedido().isEqual(dataFinal) || pedido.getDataPedido().isBefore(dataFinal))) {
                exibirDetalhesPedido(pedido);
            }
        }
    }

    private void consultaPedidoPorId(Scanner scanner) {
        System.out.print("Digite o ID do Pedido: ");
        int idPedido;
        try {
            idPedido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de pedido invalido. Operacao cancelada.");
            return;
        }

        Pedido pedido = buscarPedidoPorId(idPedido);
        if (pedido == null) {
            System.out.println("Pedido nao encontrado.");
            return;
        }
        exibirDetalhesPedido(pedido);
    }

    public void consultarPedidosPorCliente(Scanner scanner) { // Publico para ser chamado pelo menu Cliente
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
            System.out.println("ID de cliente invalido. Operacao cancelada.");
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
            System.out.println("ID de pedido invalido. Operacao cancelada.");
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

            // Logica para lidar com alteracoes de estoque ao mudar status
            if (pedido.getSituacao() == PedidoStatus.NOVO && novaSituacao == PedidoStatus.CANCELADO) {
                // Se o pedido era NOVO e foi CANCELADO, retorna itens para o estoque
                for (ItemPedido item : pedido.getItens()) {
                    estoqueService.incrementarQuantidade(item.getProduto(), item.getQuantidade());
                }
                System.out.println("Itens do pedido " + pedido.getId() + " retornados ao estoque devido ao cancelamento.");
            } else if (pedido.getSituacao() == PedidoStatus.CANCELADO && novaSituacao == PedidoStatus.NOVO) {
                 System.out.println("Nao e possivel reverter um pedido CANCELADO para NOVO sem verificar estoque manualmente.");
                 // Em um sistema real, voce faria uma verificacao de estoque aqui para ver se e possivel reverter
                 return;
            }

            pedido.setSituacao(novaSituacao);
            if (novaSituacao == PedidoStatus.ENTREGUE) {
                pedido.setDataEntregaReal(LocalDate.now());
            }
            salvarPedidosEmArquivo();
            System.out.println("Situacao do pedido " + pedido.getId() + " atualizada para: " + novaSituacao);
        } catch (IllegalArgumentException e) {
            System.out.println("Situacao invalida. Por favor, escolha uma das opcoes listadas.");
        }
    }

    private void exibirDetalhesPedido(Pedido pedido) {
        System.out.println("-------------------------------------");
        System.out.println("ID Pedido: " + pedido.getId());
        System.out.println("Cliente: " + pedido.getCliente().getNome() + " (ID: " + pedido.getCliente().getId() + ")");
        System.out.println("Data do Pedido: " + pedido.getDataPedido());
        System.out.println("Data de Previsao de Entrega: " + pedido.getDataEntrega());
        if (pedido.getSituacao() == Utils.PedidoStatus.ENTREGUE && pedido.getDataEntregaReal() != null) {
            System.out.println("Data de Entrega: " + pedido.getDataEntregaReal());
        }
        System.out.println("Situacao: " + pedido.getSituacao());
        System.out.println("Itens do Pedido:");
        if (pedido.getItens().isEmpty()) {
            System.out.println("  Nenhum item neste pedido.");
        } else {
            System.out.println("  Produto (ID) | Qtd | Preco Unit. | Subtotal");
            System.out.println("  ----------------------------------------------");
            for (ItemPedido item : pedido.getItens()) {
                System.out.printf("  %-15s | %-3d | R$ %-8.2f | R$ %.2f%n",
                        item.getProduto().getNome() + " (" + item.getProduto().getId() + ")",
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal());
            }
            System.out.println("  ----------------------------------------------");
            double total = pedido.calcularTotal();
            double valorIcms = pedido.calcularValorIcms(total);
            double totalComIcms = pedido.calcularTotalComIcms(total);
            System.out.printf("  Total do Pedido (sem ICMS): R$ %.2f%n", total);
            System.out.printf("  ICMS (17%%): R$ %.2f%n", valorIcms);
            System.out.printf("  Total do Pedido (com ICMS): R$ %.2f%n", totalComIcms);
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