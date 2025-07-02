package Services;
import Models.Endereco;
import Models.Fornecedor;
import Models.Produto;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.*;

public class FornecedorService {

    private List<Fornecedor> fornecedores;

    List<Fornecedor> getFornecedores() {
        return fornecedores;
    }
    private int proximoId;
    private ProdutoService produtoService;
    private EnderecoService enderecoService;
    private static final String ARQUIVO_FORNECEDORES = "src/Arquivos/fornecedores.dat";

    public FornecedorService(ProdutoService produtoService, EnderecoService enderecoService) {
        this.fornecedores = new ArrayList<>();
        this.proximoId = 1;
        this.produtoService = produtoService;
        this.enderecoService = enderecoService;
        carregarFornecedoresDeArquivo();
        atualizarProximoId();
    }

    public void salvarFornecedoresEmArquivo() {
        try {
            File pasta = new File("Arquivos");
            if (!pasta.exists()) pasta.mkdir();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_FORNECEDORES));
            oos.writeObject(fornecedores);
            oos.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar fornecedores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarFornecedoresDeArquivo() {
        File arquivo = new File(ARQUIVO_FORNECEDORES);
        if (!arquivo.exists()) return;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_FORNECEDORES));
            fornecedores = (List<Fornecedor>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar fornecedores: " + e.getMessage());
        }
    }

    private void atualizarProximoId() {
        int maiorId = 0;
        for (Fornecedor f : fornecedores) {
            if (f.getId() > maiorId) maiorId = f.getId();
        }
        proximoId = maiorId + 1;
    }



    public int getContadorFornecedores() {
        return fornecedores.size();
    }

    public void inserirFornecedor(Scanner scanner) {
        System.out.println("\n--- Insercao de Fornecedor ---");
        System.out.print("Insira o nome do Fornecedor: ");
        String nome = scanner.nextLine();

        System.out.print("Insira o CNPJ do Fornecedor (14 digitos): ");
        String cnpj = scanner.nextLine();

        while (cnpj.length() != 14) {
            System.out.print("CNPJ invalido, digite novamente (14 digitos): ");
            cnpj = scanner.nextLine();
        }

        System.out.print("Insira o telefone do Fornecedor: ");
        String telefone = scanner.nextLine();

        System.out.print("Insira o e-mail do Fornecedor: ");   
        String email = scanner.nextLine();

        Endereco enderecoFornecedor = null;
        if (enderecoService != null && enderecoService.getContadorEnderecos() > 0) {
            System.out.print("Deseja vincular um endereco existente? (S/N): ");
            String vincularEndereco = scanner.nextLine();
            if (vincularEndereco.equalsIgnoreCase("S")) {
                enderecoService.consultaTodosEnderecos();
                System.out.print("Digite o ID do endereco a vincular: ");
                int idEndereco;
                try {
                    idEndereco = Integer.parseInt(scanner.nextLine());
                    enderecoFornecedor = enderecoService.buscarEnderecoPorId(idEndereco);
                    if (enderecoFornecedor == null) {
                        System.out.println("Endereco nao encontrado. Fornecedor sera cadastrado sem endereco vinculado.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ID de endereco invalido. Fornecedor sera cadastrado sem endereco vinculado.");
                }
            }
        } else {
            System.out.println("Nao ha enderecos cadastrados para vincular.");
        }

        // Construtor atualizado
        Fornecedor novoFornecedor = new Fornecedor(proximoId++, nome, cnpj, telefone, email, enderecoFornecedor);
        fornecedores.add(novoFornecedor);
        salvarFornecedoresEmArquivo();
        System.out.println("Fornecedor inserido com sucesso, ID: " + novoFornecedor.getId());
    }

    public void editarFornecedor(Scanner scanner) {
        if (fornecedores.isEmpty()) {
            System.out.println("Nao tem fornecedores cadastrados para editar.");
            return;
        }

        consultaTodosFornecedores();

        System.out.print("Digite o ID do fornecedor que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o ID. Operacao cancelada.");
            return;
        }

        Fornecedor fornecedor = buscarFornecedorPorId(id);

        if (fornecedor == null) {
            System.out.println("Fornecedor nao encontrado com o ID: " + id);
            return;
        }

        System.out.println("Editando fornecedor: " + fornecedor.getNome());
        System.out.print("Novo nome (deixe em branco para manter o atual): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            fornecedor.setNome(novoNome);
        }

        System.out.print("Novo CNPJ (deixe em branco para manter o atual, deve ter 14 digitos): ");
        String novoCnpj = scanner.nextLine();
        if (!novoCnpj.trim().isEmpty()) {
            if (novoCnpj.length() != 14) {
                System.out.println("CNPJ invalido (deve ter 14 digitos), mantendo o CNPJ atual.");
            } else {
                fornecedor.setCnpj(novoCnpj);
            }
        }

        System.out.print("Novo telefone (deixe em branco para manter o atual): ");
        String novoTelefone = scanner.nextLine();
        if (!novoTelefone.trim().isEmpty()) {
            fornecedor.setTelefone(novoTelefone);
        }

        System.out.print("Novo e-mail (deixe em branco para manter o atual): ");   
        String novoEmail = scanner.nextLine();
        if (!novoEmail.trim().isEmpty()) {
            fornecedor.setEmail(novoEmail);
        }

        System.out.print("Deseja alterar o endereco vinculado? (S/N): ");
        String alterarEndereco = scanner.nextLine();
        if (alterarEndereco.equalsIgnoreCase("S")) {
            if (enderecoService != null) {
                enderecoService.consultaTodosEnderecos();
                System.out.print("Digite o ID do novo endereco (0 para remover): ");
                int idEndereco;
                try {
                    idEndereco = Integer.parseInt(scanner.nextLine());
                    if (idEndereco == 0) {
                        fornecedor.setEndereco(null);
                        System.out.println("Endereco removido do fornecedor.");
                    } else {
                        Endereco novoEndereco = enderecoService.buscarEnderecoPorId(idEndereco);
                        if (novoEndereco == null) {
                            System.out.println("Endereco nao encontrado. Mantendo o endereco atual.");
                        } else {
                            fornecedor.setEndereco(novoEndereco);
                            System.out.println("Endereco atualizado com sucesso.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ID de endereco invalido. Mantendo o endereco atual.");
                }
            } else {
                System.out.println("Servico de enderecos nao disponivel.");
            }
        }

        System.out.println("Fornecedor atualizado com sucesso");
        salvarFornecedoresEmArquivo();
    }

    public void excluirFornecedor(Scanner scanner) {
        if (fornecedores.isEmpty()) {
            System.out.println("Nao ha fornecedores cadastrados para excluir.");
            return;
        }

        consultaTodosFornecedores();

        System.out.print("Digite o Codigo do fornecedor que deseja excluir: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo. Operacao cancelada.");
            return;
        }

        Fornecedor fornecedorParaExcluir = buscarFornecedorPorId(id);

        if (fornecedorParaExcluir == null) {
            System.out.println("Fornecedor nao encontrado com o ID: " + id);
            return;
        }

        List<Produto> produtosVinculados = produtoService.buscarProdutosPorFornecedor(fornecedorParaExcluir);
        if (!produtosVinculados.isEmpty()) {
            System.out.println("\nATENCAO: Este fornecedor nao pode ser excluido.");
            System.out.println("Ele possui os seguintes produtos vinculados:");
            System.out.println("-------------------------------------");
            System.out.println("ID Produto | Nome Produto");
            System.out.println("-------------------------------------");
            for (Produto p : produtosVinculados) {
                System.out.println(p.getId() + "          | " + p.getNome());
            }
            System.out.println("-------------------------------------\n");
            System.out.println("Por favor, desvincule ou exclua esses produtos antes de remover o fornecedor.");
            return;
        }

        System.out.print("Tem certeza que deseja excluir o fornecedor '" + fornecedorParaExcluir.getNome() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            fornecedores.remove(fornecedorParaExcluir);
            salvarFornecedoresEmArquivo();
            System.out.println("Fornecedor excluido com sucesso.");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultaFornecedor(Scanner scanner) {
        if (fornecedores.isEmpty()) {
            System.out.println("Nao ha fornecedores cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Fornecedor por ---");
        System.out.println("1 - Nome ");
        System.out.println("2 - Codigo");
        System.out.println("3 - Todos");
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
                consultaFornecedorPorNome(scanner);
                break;
            case 2:
                consultaFornecedorPorCodigo(scanner);
                break;
            case 3:
                consultaTodosFornecedores();
                break;
            default:
                System.out.println("Nao existe essa opcao");
                break;
        }
    }

    private void consultaFornecedorPorNome(Scanner scanner) {
        System.out.print("Digite o Nome do Fornecedor: ");
        String nomeFornecedor = scanner.nextLine();

        System.out.println("\n--- Lista de Fornecedores ---");
        System.out.println("Codigo | Nome | CNPJ | Telefone | Email | Endereco");
        System.out.println("--------------------------------------------------------------------------------------------------");

        boolean encontrou = false;

        for (Fornecedor f : fornecedores) {
            if (f.getNome().toLowerCase().contains(nomeFornecedor.toLowerCase())) {
                String enderecoInfo = (f.getEndereco() != null) ? f.getEndereco().toString() : "N/A";
                System.out.printf("%-6d | %-15s | %-14s | %-12s | %-20s | %s%n",
                        f.getId(), f.getNome(), f.getCnpj(), f.getTelefone(), f.getEmail(), enderecoInfo);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum fornecedor encontrado com esse nome.");
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("Total de fornecedores: " + fornecedores.size());
    }

    private void consultaFornecedorPorCodigo(Scanner scanner) {
        System.out.print("Digite o Codigo do Fornecedor: ");
        int codigoFornecedor;
        try {
            codigoFornecedor = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Fornecedores ---");
        System.out.println("Codigo | Nome | CNPJ | Telefone | Email | Endereco");
        System.out.println("--------------------------------------------------------------------------------------------------");

        boolean encontrou = false;

        for (Fornecedor f : fornecedores) {
            if (codigoFornecedor == f.getId()) {
                String enderecoInfo = (f.getEndereco() != null) ? f.getEndereco().toString() : "N/A";
                System.out.printf("%-6d | %-15s | %-14s | %-12s | %-20s | %s%n",
                        f.getId(), f.getNome(), f.getCnpj(), f.getTelefone(), f.getEmail(), enderecoInfo);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum fornecedor encontrado com esse codigo.");
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("Total de fornecedores: " + fornecedores.size());
    }

    public void consultaTodosFornecedores() {
        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Fornecedores ---");
        System.out.println("Codigo | Nome | CNPJ | Telefone | Email | Endereco");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (Fornecedor f : fornecedores) {
            String enderecoInfo = (f.getEndereco() != null) ? f.getEndereco().toString() : "N/A";
            System.out.printf("%-6d | %-15s | %-14s | %-12s | %-20s | %s%n",
                    f.getId(), f.getNome(), f.getCnpj(), f.getTelefone(), f.getEmail(), enderecoInfo);
        }

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("Total de fornecedores: " + fornecedores.size());
    }

    public Fornecedor buscarFornecedorPorId(int id) {
        for (Fornecedor f : fornecedores) {
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }
}