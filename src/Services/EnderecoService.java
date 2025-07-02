package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.Endereco;
import Models.Cliente;
import Models.Fornecedor;

import java.io.*;

public class EnderecoService {
    private List<Endereco> enderecos;
    private int proximoId;
    private static final String ARQUIVO_ENDERECOS = "src/Arquivos/enderecos.dat";
    private ClienteService clienteService;
    private FornecedorService fornecedorService;

    public EnderecoService(ClienteService clienteService, FornecedorService fornecedorService) {
        this.enderecos = new ArrayList<>();
        this.proximoId = 1;
        this.clienteService = clienteService;
        this.fornecedorService = fornecedorService;
        carregarEnderecosDeArquivo();
        atualizarProximoId();
    }

    // Setters for circular dependency resolution
    public void setClienteService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    public void setFornecedorService(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    public void salvarEnderecosEmArquivo() {
        try {
            File pasta = new File("Arquivos");
            if (!pasta.exists()) pasta.mkdir();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_ENDERECOS));
            oos.writeObject(enderecos);
            oos.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar enderecos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarEnderecosDeArquivo() {
        File arquivo = new File(ARQUIVO_ENDERECOS);
        if (!arquivo.exists()) return;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_ENDERECOS));
            enderecos = (List<Endereco>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar enderecos: " + e.getMessage());
        }
    }

    private void atualizarProximoId() {
        int maiorId = 0;
        for (Endereco e : enderecos) {
            if (e.getId() > maiorId) maiorId = e.getId();
        }
        proximoId = maiorId + 1;
    }

    public int getContadorEnderecos() {
        return enderecos.size();
    }

    public void inserirEndereco(Scanner scanner) {
        System.out.println("\n--- Insercao de Endereco ---");
        System.out.print("Rua: ");
        String rua = scanner.nextLine();

        System.out.print("Numero: ");
        String numero = scanner.nextLine();

        System.out.print("Complemento (opcional): ");
        String complemento = scanner.nextLine();

        System.out.print("Bairro: ");
        String bairro = scanner.nextLine();

        System.out.print("CEP (8 digitos): ");
        String cep = scanner.nextLine();
        while (cep.length() != 8) {
            System.out.print("CEP invalido. Digite 8 digitos: ");
            cep = scanner.nextLine();
        }

        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        System.out.print("Estado (UF): ");
        String estado = scanner.nextLine();
        while (estado.length() != 2) {
            System.out.print("Estado invalido. Digite 2 caracteres (UF): ");
            estado = scanner.nextLine();
        }


        Endereco novoEndereco = new Endereco(proximoId++, rua, numero, complemento, bairro, cep, cidade, estado);
        enderecos.add(novoEndereco);
        salvarEnderecosEmArquivo();
        System.out.println("Endereco inserido com sucesso, ID: " + novoEndereco.getId());
    }

    public void editarEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para editar.");
            return;
        }

        consultaTodosEnderecos();

        System.out.print("Digite o ID do endereco que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o ID. Operacao cancelada.");
            return;
        }

        Endereco endereco = buscarEnderecoPorId(id);

        if (endereco == null) {
            System.out.println("Endereco nao encontrado.");
            return;
        }

        System.out.println("Editando endereco: " + endereco.toString());
        System.out.print("Nova Rua (deixe em branco para manter o atual): ");
        String novaRua = scanner.nextLine();
        if (!novaRua.trim().isEmpty()) {
            endereco.setRua(novaRua);
        }

        System.out.print("Novo Numero (deixe em branco para manter o atual): ");
        String novoNumero = scanner.nextLine();
        if (!novoNumero.trim().isEmpty()) {
            endereco.setNumero(novoNumero);
        }

        System.out.print("Novo Complemento (deixe em branco para manter o atual): ");
        String novoComplemento = scanner.nextLine();
        if (!novoComplemento.trim().isEmpty()) {
            endereco.setComplemento(novoComplemento);
        }

        System.out.print("Novo Bairro (deixe em branco para manter o atual): ");
        String novoBairro = scanner.nextLine();
        if (!novoBairro.trim().isEmpty()) {
            endereco.setBairro(novoBairro);
        }

        System.out.print("Novo CEP (deixe em branco para manter o atual, 8 digitos): ");
        String novoCep = scanner.nextLine();
        if (!novoCep.trim().isEmpty()) {
            if (novoCep.length() != 8) {
                System.out.println("CEP invalido (deve ter 8 digitos), mantendo o atual.");
            } else {
                endereco.setCep(novoCep);
            }
        }

        System.out.print("Nova Cidade (deixe em branco para manter o atual): ");
        String novaCidade = scanner.nextLine();
        if (!novaCidade.trim().isEmpty()) {
            endereco.setCidade(novaCidade);
        }

        System.out.print("Novo Estado (deixe em branco para manter o atual, UF): ");
        String novoEstado = scanner.nextLine();
        if (!novoEstado.trim().isEmpty()) {
            if (novoEstado.length() != 2) {
                System.out.println("Estado invalido (deve ter 2 caracteres), mantendo o atual.");
            } else {
                endereco.setEstado(novoEstado);
            }
        }

        System.out.println("Endereco atualizado com sucesso.");
        salvarEnderecosEmArquivo();
    }

    public void excluirEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para excluir.");
            return;
        }
        consultaTodosEnderecos();
        System.out.print("Digite o ID do endereco que deseja excluir: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o ID. Operacao cancelada.");
            return;
        }

        Endereco enderecoParaExcluir = buscarEnderecoPorId(id);

        if (enderecoParaExcluir == null) {
            System.out.println("Endereco nao encontrado.");
            return;
        }
        // Validacao: nao permitir exclusao se vinculado a cliente ou fornecedor
        boolean vinculadoACliente = false;
        boolean vinculadoAFornecedor = false;
        if (clienteService != null) {
            for (Cliente c : clienteService.getClientes()) {
                if (c.getEndereco() != null && c.getEndereco().getId() == enderecoParaExcluir.getId()) {
                    vinculadoACliente = true;
                    break;
                }
            }
        }
        if (fornecedorService != null) {
            for (Fornecedor f : fornecedorService.getFornecedores()) {
                if (f.getEndereco() != null && f.getEndereco().getId() == enderecoParaExcluir.getId()) {
                    vinculadoAFornecedor = true;
                    break;
                }
            }
        }
        if (vinculadoACliente || vinculadoAFornecedor) {
            System.out.println("Nao e possivel excluir o endereco. Ele esta vinculado a " +
                (vinculadoACliente ? "um cliente" : "") +
                (vinculadoACliente && vinculadoAFornecedor ? " e " : "") +
                (vinculadoAFornecedor ? "um fornecedor" : "") + ".");
            System.out.println("Desvincule o endereco antes de tentar exclui-lo.");
            return;
        }
        System.out.print("Tem certeza que deseja excluir o endereco '" + enderecoParaExcluir.getRua() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            enderecos.remove(enderecoParaExcluir);
            salvarEnderecosEmArquivo();
            System.out.println("Endereco excluido com sucesso.");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultaEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Endereco por ---");
        System.out.println("1 - Rua");
        System.out.println("2 - Codigo");
        System.out.println("3 - Todos os Enderecos");
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
                consultaEnderecoPorRua(scanner);
                break;
            case 2:
                consultaEnderecoPorCodigo(scanner);
                break;
            case 3:
                consultaTodosEnderecos();
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
        }
    }

    private void consultaEnderecoPorRua(Scanner scanner) {
        System.out.print("Digite a Rua do Endereco: ");
        String nomeRua = scanner.nextLine();

        System.out.println("\n--- Lista de Enderecos ---");
        System.out.println("ID | Endereco Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getRua().toLowerCase().contains(nomeRua.toLowerCase())) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endereco encontrado com essa rua.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de enderecos: " + enderecos.size());
    }

    private void consultaEnderecoPorCodigo(Scanner scanner) {
        System.out.print("Digite o Codigo do Endereco: ");
        int codigoEndereco;
        try {
            codigoEndereco = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida para o codigo. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Enderecos ---");
        System.out.println("ID | Endereco Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getId() == codigoEndereco) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endereco encontrado com esse codigo.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de enderecos: " + enderecos.size());
    }

    public void consultaTodosEnderecos() {
        if (enderecos.isEmpty()) {
            System.out.println("Nenhum endereco encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Enderecos ---");
        System.out.println("ID | Endereco Completo");
        System.out.println("----------------------------------------------------------------------------------");

        for (Endereco e : enderecos) {
            System.out.printf("%-2d | %s%n", e.getId(), e.toString());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de enderecos: " + enderecos.size());
    }
    
    public boolean temEnderecos() {
        if (enderecos.isEmpty()) {
            return false;
        }else {
        	return true;
        }
    }

    public Endereco buscarEnderecoPorId(int id) {
        for (Endereco e : enderecos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}