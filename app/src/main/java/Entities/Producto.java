package Entities;

import java.io.Serializable;

public class Producto implements Serializable {
    private int id_producto;
    private String descripcion;
    private String stock;
    private String status;
    private String price;

    public Producto(int id_producto,String descripcion, String stock, String status,String price) {
        this.descripcion = descripcion;
        this.stock = stock;
        this.status = status;
        this.id_producto=id_producto;
        this.price=price;
    }
    public Producto() {

    }

    public int getId() {
        return id_producto;
    }

    public void setId(int id) {
        this.id_producto = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
