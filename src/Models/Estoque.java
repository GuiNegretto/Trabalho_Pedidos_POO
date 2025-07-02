package Models;

import java.io.Serializable;

public class Estoque implements Serializable {

    private Produto produto; // Depende da classe Produto
    private int quantidade;

    // Removemos o Scanner estatico e os arrays estaticos

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