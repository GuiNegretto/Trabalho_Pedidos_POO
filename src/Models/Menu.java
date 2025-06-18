package Models;

import java.util.InputMismatchException;
import java.util.Scanner;
import Services.*;

public class Menu {

    private Scanner scanner;
    private UsuarioService usuarioService;
    private FornecedorService fornecedorService;
    private ProdutoService produtoService;
    private EstoqueService estoqueService;
    private ClienteService clienteService; 
    private EnderecoService enderecoService; 
    private PedidoService pedidoService; 



    public Menu(Scanner scanner, UsuarioService usuarioService,
                FornecedorService fornecedorService, ProdutoService produtoService,
                EstoqueService estoqueService, ClienteService clienteService,
                EnderecoService enderecoService, PedidoService pedidoService) {
        this.scanner = scanner;
        this.usuarioService = usuarioService;
        this.fornecedorService = fornecedorService;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        this.clienteService = clienteService;
        this.enderecoService = enderecoService;
        this.pedidoService = pedidoService;
    }

    public void fazerLogin() {
        while (true) {
            System.out.print("Digite o seu usuario (0 para sair): ");
            String login = scanner.nextLine();

            if (login.equals("0")) {
                System.out.println("Saindo");
                break;
            }

            System.out.print("Digite a sua senha: ");
            String senha = scanner.nextLine();

            int nivelAcesso = usuarioService.verificaLogin(login);

            if (nivelAcesso == 1 || nivelAcesso == 2) {
                nivelAcesso = usuarioService.verificaSenha(senha, nivelAcesso);

                switch (nivelAcesso) {
                    case 1:
                        menuAdmin();
                        break;
                    case 2:
                        menuCliente();
                        break;
                    case 3:
                        System.out.println("Senha incorreta, tente novamente\n");
                        break;
                }
            } else {
                System.out.println("Nao existe esse usuario, tente novamente.\n");
            }
        }
    }

    private void menuAdmin() {
        while (true) {
            System.out.println("\n--- Menu do Administrador ---");
            System.out.println("1 - Fornecedores");
            System.out.println("2 - Produtos");
            System.out.println("3 - Estoque");
            System.out.println("4 - Clientes"); 
            System.out.println("5 - Endereços"); 
            System.out.println("6 - Pedidos");
            System.out.println("7 - Voltar para Login");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    menuFornecedores();
                    break;
                case 2:
                    menuProduto();
                    break;
                case 3:
                    menuEstoque();
                    break;
                case 4:
                    menuClientes(); 
                    break;
                case 5:
                    menuEnderecos(); 
                    break;
                case 6:
                    menuPedidosAdmin(); 
                    break;
                case 7:
                    System.out.println("Voltando para a tela de Login.");
                    return;
                default:
                    System.out.println("Nao existe essa opcao, tente novamente.");
            }
        }
    }

    private void menuCliente() {
        while (true) {
            System.out.println("\n--- Menu do Cliente ---");
            System.out.println("1 - Buscar Fornecedor");
            System.out.println("2 - Buscar Produtos");
            System.out.println("3 - Estoque de Produto");
            System.out.println("4 - Meus Pedidos"); // 
            System.out.println("5 - Realizar Novo Pedido");
            System.out.println("6 - Voltar para Login");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    fornecedorService.consultaFornecedor(scanner);
                    break;
                case 2:
                    produtoService.consultaProduto(scanner);
                    break;
                case 3:
                    estoqueService.consultarItemEstoque(scanner);
                    break;
                case 4:
                    pedidoService.consultarPedidosPorCliente(scanner); 
                    break;
                case 5:
                    pedidoService.realizarNovoPedido(scanner); 
                    break;
                case 6:
                    System.out.println("Voltando para a tela de Login.");
                    return;
                default:
                    System.out.println("Nao existe essa opcao, tente novamente.");
            }
        }
    }

    // --- MENUS ADMIN ---

    private void menuFornecedores() {
        while (true) {
            System.out.println("\n--- Menu de Fornecedores ---");
            System.out.println("1 - Inclusão de Fornecedor");
            System.out.println("2 - Alteração de Fornecedor");
            System.out.println("3 - Exclusão de Fornecedor");
            System.out.println("4 - Consulta de Fornecedores");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    fornecedorService.inserirFornecedor(scanner);
                    break;
                case 2:
                    fornecedorService.editarFornecedor(scanner);
                    break;
                case 3:
                    fornecedorService.excluirFornecedor(scanner);
                    break;
                case 4:
                    fornecedorService.consultaFornecedor(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void menuProduto() {
        while (true) {
            System.out.println("\n--- Menu de Produtos ---");
            System.out.println("1 - Inclusão de Produto");
            System.out.println("2 - Alteração de Produto");
            System.out.println("3 - Exclusão de Produto");
            System.out.println("4 - Consulta de Produtos");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    produtoService.inserirProduto(scanner);
                    break;
                case 2:
                    produtoService.editarProduto(scanner);
                    break;
                case 3:
                    produtoService.excluirProduto(scanner);
                    break;
                case 4:
                    produtoService.consultaProduto(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Nao existe essa opcao, tente novamente.");
            }
        }
    }

    private void menuEstoque() {
        while (true) {
            System.out.println("\n--- Menu de Estoque ---");
            System.out.println("1 - Inclusão de Item no Estoque");
            System.out.println("2 - Exclusão de Item do Estoque");
            System.out.println("3 - Alteração de Quantidade no Estoque"); 
            System.out.println("4 - Consulta do Estoque");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    estoqueService.adicionarItemEstoque(scanner);
                    break;
                case 2:
                    estoqueService.excluirItemEstoque(scanner);
                    break;
                case 3:
                    estoqueService.alterarItemEstoque(scanner); 
                    break;
                case 4:
                    estoqueService.consultarItemEstoque(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Nao existe essa opcao, tente novamente.");
            }
        }
    }

    private void menuClientes() { 
        while (true) {
            System.out.println("\n--- Menu de Clientes ---");
            System.out.println("1 - Inclusão de Cliente");
            System.out.println("2 - Alteração de Cliente");
            System.out.println("3 - Exclusão de Cliente");
            System.out.println("4 - Consulta de Clientes");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    clienteService.inserirCliente(scanner);
                    break;
                case 2:
                    clienteService.editarCliente(scanner);
                    break;
                case 3:
                    clienteService.excluirCliente(scanner);
                    break;
                case 4:
                    clienteService.consultaCliente(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void menuEnderecos() {
        while (true) {
            System.out.println("\n--- Menu de Endereços ---");
            System.out.println("1 - Inclusão de Endereço");
            System.out.println("2 - Alteração de Endereço");
            System.out.println("3 - Exclusão de Endereço");
            System.out.println("4 - Consulta de Endereços");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    enderecoService.inserirEndereco(scanner);
                    break;
                case 2:
                    enderecoService.editarEndereco(scanner);
                    break;
                case 3:
                    enderecoService.excluirEndereco(scanner);
                    break;
                case 4:
                    enderecoService.consultaEndereco(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void menuPedidosAdmin() { 
        while (true) {
            System.out.println("\n--- Menu de Pedidos (Admin) ---");
            System.out.println("1 - Realizar Novo Pedido (Admin)"); // Admin também pode criar pedidos
            System.out.println("2 - Consultar Pedidos");
            System.out.println("3 - Atualizar Situação do Pedido");
            System.out.println("4 - Voltar");
            System.out.print("Escolha uma opcao: ");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    pedidoService.realizarNovoPedido(scanner);
                    break;
                case 2:
                    pedidoService.consultaPedido(scanner);
                    break;
                case 3:
                    pedidoService.atualizarSituacaoPedido(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}