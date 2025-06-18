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
        System.out.println("\n--- Inser��o de Endere�o ---");
        System.out.print("Rua: ");
        String rua = scanner.nextLine();

        System.out.print("N�mero: ");
        String numero = scanner.nextLine();

        System.out.print("Complemento (opcional): ");
        String complemento = scanner.nextLine();

        System.out.print("Bairro: ");
        String bairro = scanner.nextLine();

        System.out.print("CEP (8 d�gitos): ");
        String cep = scanner.nextLine();
        while (cep.length() != 8) {
            System.out.print("CEP inv�lido. Digite 8 d�gitos: ");
            cep = scanner.nextLine();
        }

        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        System.out.print("Estado (UF): ");
        String estado = scanner.nextLine();
        while (estado.length() != 2) {
            System.out.print("Estado inv�lido. Digite 2 caracteres (UF): ");
            estado = scanner.nextLine();
        }


        Endereco novoEndereco = new Endereco(proximoId++, rua, numero, complemento, bairro, cep, cidade, estado);
        enderecos.add(novoEndereco);
        System.out.println("Endere�o inserido com sucesso, ID: " + novoEndereco.getId());
    }

    public void editarEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para editar.");
            return;
        }

        consultaTodosEnderecos();

        System.out.print("Digite o ID do endere�o que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inv�lida para o ID. Operacao cancelada.");
            return;
        }

        Endereco endereco = buscarEnderecoPorId(id);

        if (endereco == null) {
            System.out.println("Endere�o nao encontrado.");
            return;
        }

        System.out.println("Editando endere�o: " + endereco.toString());
        System.out.print("Nova Rua (deixe em branco para manter o atual): ");
        String novaRua = scanner.nextLine();
        if (!novaRua.trim().isEmpty()) {
            endereco.setRua(novaRua);
        }

        System.out.print("Novo N�mero (deixe em branco para manter o atual): ");
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

        System.out.print("Novo CEP (deixe em branco para manter o atual, 8 d�gitos): ");
        String novoCep = scanner.nextLine();
        if (!novoCep.trim().isEmpty()) {
            if (novoCep.length() != 8) {
                System.out.println("CEP inv�lido (deve ter 8 d�gitos), mantendo o atual.");
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
                System.out.println("Estado inv�lido (deve ter 2 caracteres), mantendo o atual.");
            } else {
                endereco.setEstado(novoEstado);
            }
        }

        System.out.println("Endere�o atualizado com sucesso.");
    }

    public void excluirEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados para excluir.");
            return;
        }

        consultaTodosEnderecos();

        System.out.print("Digite o ID do endere�o que deseja excluir: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inv�lida para o ID. Operacao cancelada.");
            return;
        }

        Endereco enderecoParaExcluir = buscarEnderecoPorId(id);

        if (enderecoParaExcluir == null) {
            System.out.println("Endere�o nao encontrado.");
            return;
        }


        System.out.print("Tem certeza que deseja excluir o endere�o '" + enderecoParaExcluir.getRua() + "'? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            enderecos.remove(enderecoParaExcluir);
            System.out.println("Endere�o exclu�do com sucesso.");
        } else {
            System.out.println("Operacao cancelada.");
        }
    }

    public void consultaEndereco(Scanner scanner) {
        if (enderecos.isEmpty()) {
            System.out.println("Nao ha enderecos cadastrados.");
            return;
        }

        System.out.println("\n--- Consultar Endere�o por ---");
        System.out.println("1 - Rua");
        System.out.println("2 - C�digo");
        System.out.println("3 - Todos os Endere�os");
        System.out.print("Escolha uma opcao: ");
        int opcao;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inv�lida. Operacao cancelada.");
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
        System.out.print("Digite a Rua do Endere�o: ");
        String nomeRua = scanner.nextLine();

        System.out.println("\n--- Lista de Endere�os ---");
        System.out.println("ID | Endere�o Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getRua().toLowerCase().contains(nomeRua.toLowerCase())) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endere�o encontrado com essa rua.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endere�os: " + enderecos.size());
    }

    private void consultaEnderecoPorCodigo(Scanner scanner) {
        System.out.print("Digite o C�digo do Endere�o: ");
        int codigoEndereco;
        try {
            codigoEndereco = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inv�lida para o c�digo. Operacao cancelada.");
            return;
        }

        System.out.println("\n--- Lista de Endere�os ---");
        System.out.println("ID | Endere�o Completo");
        System.out.println("----------------------------------------------------------------------------------");

        boolean encontrou = false;
        for (Endereco e : enderecos) {
            if (e.getId() == codigoEndereco) {
                System.out.printf("%-2d | %s%n", e.getId(), e.toString());
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum endere�o encontrado com esse c�digo.");
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endere�os: " + enderecos.size());
    }

    public void consultaTodosEnderecos() {
        if (enderecos.isEmpty()) {
            System.out.println("Nenhum endere�o encontrado.");
            return;
        }

        System.out.println("\n--- Lista de Endere�os ---");
        System.out.println("ID | Endere�o Completo");
        System.out.println("----------------------------------------------------------------------------------");

        for (Endereco e : enderecos) {
            System.out.printf("%-2d | %s%n", e.getId(), e.toString());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total de endere�os: " + enderecos.size());
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