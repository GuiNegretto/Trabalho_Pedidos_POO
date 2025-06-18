package Services;

import Models.Usuario;

public class UsuarioService {

    // A l�gica de verifica��o de login e senha agora est� aqui e n�o � est�tica
    public int verificaLogin(String nome) {
        if (nome.equalsIgnoreCase(Usuario.getLoginAdmin())) {
            return 1; // Admin
        } else if (nome.equalsIgnoreCase(Usuario.getLoginCliente())) {
            return 2; // Cliente
        } else {
            return 3; // Usu�rio n�o existe
        }
    }

    public int verificaSenha(String senha, int nivel) {
        if (nivel == 1 && senha.equalsIgnoreCase(Usuario.getSenhaAdmin())) {
            return 1; // Senha correta para Admin
        } else if (nivel == 2 && senha.equalsIgnoreCase(Usuario.getSenhaCliente())) {
            return 2; // Senha correta para Cliente
        } else {
            return 3; // Senha incorreta
        }
    }
}