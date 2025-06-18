package Models;

public class Estoque {

    private Produto produto; // Depende da classe Produto
    private int quantidade;

    // Removemos o Scanner estático e os arrays estáticos

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Estoque() {
    }

    public Estoque(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }
}