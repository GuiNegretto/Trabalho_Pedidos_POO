package Models;

public class Produto {
    private int id;
    private String nome;
    private Fornecedor fornecedor;
    private double preco; // NOVA PROPRIEDADE: preço

    public Produto() {
    }

    public Produto(int id, String nome, Fornecedor fornecedor, double preco) {
        this.id = id;
        this.nome = nome;
        this.fornecedor = fornecedor;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public double getPreco() { // Novo getter para preço
        return preco;
    }

    public void setPreco(double preco) { // Novo setter para preço
        this.preco = preco;
    }
}