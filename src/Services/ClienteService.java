package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.Cliente;
import Models.Endereco;
import Services.EnderecoService;

public class ClienteService {
    private List<Cliente> clientes;
    private int proximoId;
    private EnderecoService enderecoService; // Dependência para Endereco

    public ClienteService(EnderecoService enderecoService) {
        this.clientes = new ArrayList<>();
        this.proximoId = 1;
        this.enderecoService = enderecoService;
    }

    public int getContadorClientes() {
        return clientes.size();
    }

    public void inserirCliente(Scanner scanner) {
        System.out.println("\n--- Inserção de Cliente ---");
        System.out.print("Insira o nome do Cliente: ");
        String nome = scanner.nextLine();

        System.out.print("Insira o telefone do Cliente: ");
        String telefone = scanner.nextLine();

        System.out.print("Insira o e-mail do Cliente: ");
        String email = scanner.nextLine();

        System.out.print("Insira o cartão de crédito do Cliente: ");
        String cartaoCredito = scanner.nextLine();

        Endereco enderecoCliente = null;
        if (enderecoService.getContadorEnderecos() > 0) {
            System.out.print("Deseja vincular um endereço existente? (S/N): ");
            String vincularEndereco = scanner.nextLine();
            if (vincularEndereco.equalsIgnoreCase("S")) {
                enderecoService.consultaTodosEnderecos();
                System.out.print("Digite o ID do endereço a vincular: ");
                int idEndereco;
                try {
                    idEndereco = Integer.parseInt(scanner.nextLine());
                    enderecoCliente = enderecoService.buscarEnderecoPorId(idEndereco);
                    if (enderecoCliente == null) {
                        System.out.println("Endereço não encontrado. Cliente será cadastrado sem endereço vinculado.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ID de endereço inválido. Cliente será cadastrado sem endereço vinculado.");
                }
            }
        } else {
            System.out.println("Não há endereços cadastrados para vincular.");
        }


        Cliente novoCliente = new Cliente(proximoId++, nome, telefone, email, cartaoCredito, enderecoCliente);
        clientes.add(novoCliente);
        System.out.println("Cliente inserido com sucesso, ID: " + novoCliente.getId());
    }

    public void editarCliente(Scanner scanner) {
        if (clientes.isEmpty()) {
            System.out.println("Nao ha clientes cadastrados para editar.");
            return;
        }

        consultaTodosClientes();

        System.out.print("Digite o ID do cliente que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
            return;
        }

        Cliente cliente = buscarClientePorId(id);

        if (cliente == null) {
            System.out.println("Cliente nao encontrado.");
            return;
        }

        System.out.println("Editando cliente: " + cliente.getNome());
        System.out.print("Novo nome (deixe em branco para manter o atual): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            cliente.setNome(novoNome);
        }

        System.out.print("Novo telefone (deixe em branco para manter o atual): ");
        String novoTelefone = scanner.nextLine();
        if (!novoTelefone.trim().isEmpty()) {
            cliente.setTelefone(novoTelefone);
        }

        System.out.print("Novo e-mail (deixe em branco para manter o atual): ");
        String novoEmail = scanner.nextLine();
        if (!novoEmail.trim().isEmpty()) {
            cliente.setEmail(novoEmail);
        }

        System.out.print("Novo cartão de crédito (deixe em branco para manter o atual): ");
        String novoCartaoCredito = scanner.nextLine();
        if (!novoCartaoCredito.trim().isEmpty()) {
            cliente.setCartaoCredito(novoCartaoCredito);
        }

        System.out.print("Deseja alterar o endereço vinculado? (S/N): ");
        String alterarEndereco = scanner.nextLine();
        if (alterarEndereco.equalsIgnoreCase("S")) {
            enderecoService.consultaTodosEnderecos();
            System.out.print("Digite o ID do novo endereço (0 para remover): ");
            int idEndereco;
            try {
                idEndereco = Integer.parseInt(scanner.nextLine());
                if (idEndereco == 0) {
                    cliente.setEndereco(null);
                    System.out.println("Endereço removido do cliente.");
                } else {
                    Endereco novoEndereco = enderecoService.buscarEnderecoPorId(idEndereco);
                    if (novoEndereco == null) {
                        System.out.println("Endereço não encontrado. Mantendo o endereço atual.");
                    } else {
                        cliente.setEndereco(novoEndereco);
                        System.out.println("Endereço atualizado com sucesso.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("ID de endereço inválido. Mantendo o endereço atual.");
            }
        }

        System.out.println("Cliente atualizado com sucesso.");
    }

    public void excluirCliente(Scanner scanner) {
        if (clientes.isEmpty()) {
            System.out.println("Nao ha clientes cadastrados para excluir.");
            return;
        }

        consultaTodosClientes();

        System.out.print("Digite o ID do cliente que deseja excluir: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
            return;
        }

        Cliente clienteParaExcluir = buscarClientePorId(id);

        if (clienteParaExcluir == null) {
            System.out.println("Cliente nao encontrado.");
            return;
        }
        
        // Em um sistema real, você verificaria se o cliente tem pedidos antes de excluir
        // Aqui, para simplificar, permitiremos a exclusão sem essa verificação.

        System.out.print("Tem certeza que deseja excluir o cliente '" + clienteParaExcluir.getNome() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            clientes.remove(clienteParaExcluir);
            System.out.println("Cliente excluído com sucesso.");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultaCliente(Scanner scanner) {
        if (clientes.isEmpty()) {
            System.out.println("Nao ha clientes cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Cliente por ---");
        System.out.println("1 - Nome");
        System.out.println("2 - Código");
        System.out.println("3 - Todos os Clientes");
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
                consultaClientePorNome(scanner);
                break;
            case 2:
                consultaClientePorCodigo(scanner);
                break;
            case 3:
                consultaTodosClientes();
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
        }
    }

    private void consultaClientePorNome(Scanner scanner) {
        System.out.print("Digite o Nome do Cliente: ");
        String nomeCliente = scanner.nextLine();

        System.out.println("\n--- Lista de Clientes ---");
        System.out.println("ID | Nome | Telefone | Email | Cartão de Crédito | Endereço");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(nomeCliente.toLowerCase())) {
                String enderecoInfo = (c.getEndereco() != null) ?
                        c.getEndereco().getRua() + ", " + c.getEndereco().getNumero() + " - " + c.getEndereco().getCidade() :
                        "N/A";
                System.out.printf("%-2d | %-10s | %-10s | %-15s | %-15s | %s%n",
                        c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getCartaoCredito(), enderecoInfo);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum cliente encontrado com esse nome.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de clientes: " + clientes.size());
    }

    private void consultaClientePorCodigo(Scanner scanner) {
        System.out.print("Digite o Código do Cliente: ");
        int codigoCliente;
        try {
            codigoCliente = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Clientes ---");
        System.out.println("ID | Nome | Telefone | Email | Cartão de Crédito | Endereço");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Cliente c : clientes) {
            if (c.getId() == codigoCliente) {
                String enderecoInfo = (c.getEndereco() != null) ?
                        c.getEndereco().getRua() + ", " + c.getEndereco().getNumero() + " - " + c.getEndereco().getCidade() :
                        "N/A";
                System.out.printf("%-2d | %-10s | %-10s | %-15s | %-15s | %s%n",
                        c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getCartaoCredito(), enderecoInfo);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum cliente encontrado com esse código.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de clientes: " + clientes.size());
    }

    public void consultaTodosClientes() {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Clientes ---");
        System.out.println("ID | Nome | Telefone | Email | Cartão de Crédito | Endereço");
        System.out.println("----------------------------------------------------------------------------------");

        for (Cliente c : clientes) {
            String enderecoInfo = (c.getEndereco() != null) ?
                    c.getEndereco().getRua() + ", " + c.getEndereco().getNumero() + " - " + c.getEndereco().getCidade() :
                    "N/A";
            System.out.printf("%-2d | %-10s | %-10s | %-15s | %-15s | %s%n",
                    c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getCartaoCredito(), enderecoInfo);
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de clientes: " + clientes.size());
    }

    public Cliente buscarClientePorId(int id) {
        for (Cliente c : clientes) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
}