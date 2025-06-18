package Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Models.Endereco;

public class EnderecoService {
    private List<Endereco> enderecos;
    private int proximoId;

    public EnderecoService() {
        this.enderecos = new ArrayList<>();
        this.proximoId = 1;
    }

    public int getContadorEnderecos() {
        return enderecos.size();
    }

    public void inserirEndereco(Scanner scanner) {
        System.out.println("\n--- Inserção de Endereço ---");
        System.out.print("Rua: ");
        String rua = scanner.nextLine();

        System.out.print("Número: ");
        String numero = scanner.nextLine();

        System.out.print("Complemento (opcional): ");
        String complemento = scanner.nextLine();

        System.out.print("Bairro: ");
        String bairro = scanner.nextLine();

        System.out.print("CEP (8 dígitos): ");
        String cep = scanner.nextLine();
        while (cep.length() != 8) {
            System.out.print("CEP inválido. Digite 8 dígitos: ");
            cep = scanner.nextLine();
        }

        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        System.out.print("Estado (UF): ");
        String estado = scanner.nextLine();
        while (estado.length() != 2) {
            System.out.print("Estado inválido. Digite 2 caracteres (UF): ");
            estado = scanner.nextLine();
        }


        Endereco novoEndereco = new Endereco(proximoId++, rua, numero, complemento, bairro, cep, cidade, estado);
        enderecos.add(novoEndereco);
        System.out.println("Endereço inserido com sucesso, ID: " + novoEndereco.getId());
    }

    public void editarEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para editar.");
            return;
        }

        consultaTodosEnderecos();

        System.out.print("Digite o ID do endereço que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
            return;
        }

        Endereco endereco = buscarEnderecoPorId(id);

        if (endereco == null) {
            System.out.println("Endereço nao encontrado.");
            return;
        }

        System.out.println("Editando endereço: " + endereco.toString());
        System.out.print("Nova Rua (deixe em branco para manter o atual): ");
        String novaRua = scanner.nextLine();
        if (!novaRua.trim().isEmpty()) {
            endereco.setRua(novaRua);
        }

        System.out.print("Novo Número (deixe em branco para manter o atual): ");
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

        System.out.print("Novo CEP (deixe em branco para manter o atual, 8 dígitos): ");
        String novoCep = scanner.nextLine();
        if (!novoCep.trim().isEmpty()) {
            if (novoCep.length() != 8) {
                System.out.println("CEP inválido (deve ter 8 dígitos), mantendo o atual.");
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
                System.out.println("Estado inválido (deve ter 2 caracteres), mantendo o atual.");
            } else {
                endereco.setEstado(novoEstado);
            }
        }

        System.out.println("Endereço atualizado com sucesso.");
    }

    public void excluirEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para excluir.");
            return;
        }

        consultaTodosEnderecos();

        System.out.print("Digite o ID do endereço que deseja excluir: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o ID. Operacao cancelada.");
            return;
        }

        Endereco enderecoParaExcluir = buscarEnderecoPorId(id);

        if (enderecoParaExcluir == null) {
            System.out.println("Endereço nao encontrado.");
            return;
        }


        System.out.print("Tem certeza que deseja excluir o endereço '" + enderecoParaExcluir.getRua() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            enderecos.remove(enderecoParaExcluir);
            System.out.println("Endereço excluído com sucesso.");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultaEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Endereço por ---");
        System.out.println("1 - Rua");
        System.out.println("2 - Código");
        System.out.println("3 - Todos os Endereços");
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
        System.out.print("Digite a Rua do Endereço: ");
        String nomeRua = scanner.nextLine();

        System.out.println("\n--- Lista de Endereços ---");
        System.out.println("ID | Endereço Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getRua().toLowerCase().contains(nomeRua.toLowerCase())) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endereço encontrado com essa rua.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endereços: " + enderecos.size());
    }

    private void consultaEnderecoPorCodigo(Scanner scanner) {
        System.out.print("Digite o Código do Endereço: ");
        int codigoEndereco;
        try {
            codigoEndereco = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o código. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Endereços ---");
        System.out.println("ID | Endereço Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getId() == codigoEndereco) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endereço encontrado com esse código.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endereços: " + enderecos.size());
    }

    public void consultaTodosEnderecos() {
        if (enderecos.isEmpty()) {
            System.out.println("Nenhum endereço encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Endereços ---");
        System.out.println("ID | Endereço Completo");
        System.out.println("----------------------------------------------------------------------------------");

        for (Endereco e : enderecos) {
            System.out.printf("%-2d | %s%n", e.getId(), e.toString());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endereços: " + enderecos.size());
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