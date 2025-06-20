package Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Utils.PedidoStatus;

public class Pedido {
    private int id;
    private Cliente cliente;
    private LocalDate dataPedido;
    private LocalDate dataEntrega;
    private PedidoStatus situacao; // Enum para situa��o
    private List<ItemPedido> itens; // Lista de itens do pedido

    public Pedido() {
        this.itens = new ArrayList<>();
    }

    public Pedido(int id, Cliente cliente, LocalDate dataPedido, LocalDate dataEntrega, PedidoStatus situacao) {
        this.id = id;
        this.cliente = cliente;
        this.dataPedido = dataPedido;
        this.dataEntrega = dataEntrega;
        this.situacao = situacao;
        this.itens = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public PedidoStatus getSituacao() {
        return situacao;
    }

    public void setSituacao(PedidoStatus situacao) {
        this.situacao = situacao;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }
}