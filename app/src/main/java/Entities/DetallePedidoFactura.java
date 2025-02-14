package Entities;

import java.io.Serializable;

public class DetallePedidoFactura implements Serializable {
    private int id;
    private int id_pedido;
    private String descripcionProducto;
    private int idProducto;
    private int cantidad;
    private String subtotal;

    public DetallePedidoFactura(int idProducto, String descripcionProducto, int cantidad, String subtotal) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.descripcionProducto=descripcionProducto;
    }
    public DetallePedidoFactura(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {this.descripcionProducto = descripcionProducto;}

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

}
