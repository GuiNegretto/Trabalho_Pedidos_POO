package Models;

import Services.*;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Inicializa os servicos na ordem correta das dependencias
        EnderecoService enderecoService = new EnderecoService(null, null);
        ProdutoService produtoService = new ProdutoService();
        ClienteService clienteService = new ClienteService(enderecoService);
        FornecedorService fornecedorService = new FornecedorService(produtoService, enderecoService);
        // Agora que temos todas as instancias, setamos as dependencias faltantes
        enderecoService.setClienteService(clienteService);
        enderecoService.setFornecedorService(fornecedorService); 
        produtoService.setFornecedorService(fornecedorService); 
        UsuarioService usuarioService = new UsuarioService();

        // PedidoService precisa de ClienteService, ProdutoService e EstoqueService
        EstoqueService estoqueService = new EstoqueService(produtoService); 
        produtoService.setEstoqueService(estoqueService); 
        PedidoService pedidoService = new PedidoService(clienteService, produtoService, estoqueService);

        // Inicializa o Menu principal com todas as dependencias
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