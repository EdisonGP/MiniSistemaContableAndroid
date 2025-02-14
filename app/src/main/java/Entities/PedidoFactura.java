package Entities;

public class PedidoFactura {
    private int id;
    private int idCliente;
    private String cedulaCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String total;
    private String iva;
    private String fecha;
    private String categoria;
    private String despachado;
    private String facturado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCiente) {
        this.apellidoCliente = apellidoCiente;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDespachado() {
        return despachado;
    }

    public void setDespachado(String despachado) {
        this.despachado = despachado;
    }

    public String getCedulaCliente() {return cedulaCliente;}

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public int getIdCliente() {return idCliente;}

    public void setIdCliente(int idCliente) {this.idCliente = idCliente;}
    public String getCategoria() {return categoria;}

    public void setCategoria(String categoria) {this.categoria = categoria;}
    public void setFacturado(String facturado) {this.facturado = facturado;}
    public String getFacturado() {return facturado;}
}
