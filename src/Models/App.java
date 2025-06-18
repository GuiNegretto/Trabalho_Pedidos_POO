package Models;

import java.util.Scanner;
import Services.*;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Inicializa os serviços na ordem correta das dependências
        ProdutoService produtoService = new ProdutoService(); 
        EstoqueService estoqueService = new EstoqueService(produtoService); // EstoqueService depende de ProdutoService

        FornecedorService fornecedorService = new FornecedorService(produtoService); // FornecedorService depende de ProdutoService
        produtoService.setFornecedorService(fornecedorService); // Injeta FornecedorService em ProdutoService

        EnderecoService enderecoService = new EnderecoService();
        ClienteService clienteService = new ClienteService(enderecoService); // ClienteService depende de EnderecoService
        UsuarioService usuarioService = new UsuarioService();

        // PedidoService precisa de ClienteService, ProdutoService e EstoqueService
        PedidoService pedidoService = new PedidoService(clienteService, produtoService, estoqueService);

        // Inicializa o Menu principal com todas as dependências
        Menu menu = new Menu(scanner, usuarioService, fornecedorService,
                             produtoService, estoqueService, clienteService,
                             enderecoService, pedidoService);

        try {
            menu.fazerLogin();
        } finally {
            scanner.close();
        }
    }
}