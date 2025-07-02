package Services;

import Models.Usuario;

public class UsuarioService {

    // A logica de verificacao de login e senha agora esta aqui e nao e estatica
    public int verificaLogin(String nome) {
        if (nome.equalsIgnoreCase(Usuario.getLoginAdmin())) {
            return 1; // Admin
        } else if (nome.equalsIgnoreCase(Usuario.getLoginCliente())) {
            return 2; // Cliente
        } else {
            return 3; // Usuario nao existe
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