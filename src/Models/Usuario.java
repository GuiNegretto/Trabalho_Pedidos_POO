package Models;

public class Usuario {

    private boolean admin; // Esta propriedade não está sendo usada atualmente, mas pode ser mantida
    private static String loginAdmin = "admin";
    private static String senhaAdmin = "1234";
    private static String loginCliente = "cliente";
    private static String senhaCliente = "5678";


    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    // Os getters e setters estáticos ainda são úteis se você quiser
    // a capacidade de mudar as credenciais em tempo de execução
    public static String getLoginAdmin() {
        return loginAdmin;
    }

    public static void setLoginAdmin(String loginAdmin) {
        Usuario.loginAdmin = loginAdmin;
    }

    public static String getSenhaAdmin() {
        return senhaAdmin;
    }

    public static void setSenhaAdmin(String senhaAdmin) {
        Usuario.senhaAdmin = senhaAdmin;
    }

    public static String getLoginCliente() {
        return loginCliente;
    }

    public static void setLoginCliente(String loginCliente) {
        Usuario.loginCliente = loginCliente;
    }

    public static String getSenhaCliente() {
        return senhaCliente;
    }

    public static void setSenhaCliente(String senhaCliente) {
        Usuario.senhaCliente = senhaCliente;
    }
}