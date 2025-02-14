package com.example.crud_app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ListDetallePedAdapter;
import Entities.User;
import Manager.ManagerPedidoFactura;
import Manager.ManagerUser;
import Manager.ManagerDetallePedidoFactura;
import Manager.ManagerProductos;
import Entities.DetallePedidoFactura;
import Entities.PedidoFactura;


public class VerDetallePedidoFacturaActivity extends AppCompatActivity implements Serializable,BottomNavigationView.OnNavigationItemSelectedListener{
    TextView txtNumPedidoFactura,txtFecha,txtCliente,txtCedulaCli,txtSubtotal,txtIva,txtTotal, btnEliminarDet;
    ManagerPedidoFactura mPedidoFactura;
    ManagerDetallePedidoFactura mDetalle;
    ManagerProductos mProducto;
    ManagerUser mCliente;
    List<DetallePedidoFactura> listDetalle; //Utilizado para pasar el detalle al Adapter
    List<DetallePedidoFactura> detalle; //Recibe el detalle de Pedido o Factura cuando estan generandolos
    ListDetallePedAdapter listAdapter;
    String opcion; //Hay la posibilidad de venir desde 4 partes al detalle
    String clienteSeleccionado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle_pedido_factura);

        txtNumPedidoFactura=(TextView) findViewById(R.id.txtNumPedido);
        txtFecha=(TextView) findViewById(R.id.txtFechaPedido);
        txtCliente=(TextView) findViewById(R.id.txtClientePedido);
        txtCedulaCli=(TextView) findViewById(R.id.txtCedulaCliPedido);
        txtSubtotal=(TextView) findViewById(R.id.txtSubtotalPedido);
        txtIva=(TextView) findViewById(R.id.txtIvaPedido);
        txtTotal=(TextView) findViewById(R.id.txtTotalPedido);
        btnEliminarDet=(TextView) findViewById(R.id.btnEliminarDetPedido);
        mPedidoFactura=new ManagerPedidoFactura(this, "compras",1);
        mDetalle =new ManagerDetallePedidoFactura(this, "compras",1);
        mProducto=new ManagerProductos(this, "compras",1);
        mCliente=new ManagerUser(this, "compras",1);


        //Obtener los parametros del activity anterior.
        Bundle bb=this.getIntent().getExtras();
        opcion=bb.getString("opcion");
        txtFecha.setText(bb.getString("fecha"));
        txtTotal.setText("Total: "+bb.getString("total"));
        clienteSeleccionado=bb.getString("cliente");

        if(opcion.equals("0")){ // Desde Lista se ve un pedido ya realizado por un cliente, ver detalle desde administrador
            int idPed=Integer.parseInt(bb.getString("id"));
            PedidoFactura pf=mPedidoFactura.getPedidoFactura(idPed,    "pedido");
            mostrarDatosClientes(pf.getApellidoCliente()+" "+pf.getNombreCliente(),pf.getCedulaCliente());
            txtNumPedidoFactura.setText("Num Pedido:  "+idPed);

            DetallePedidoFactura[] detalle = mDetalle.getPedidoFacturaDetalle(idPed);
            List<DetallePedidoFactura> detalleA=Arrays.asList(detalle);
            initDetalleFromList(detalleA,false);

        }else if(opcion.equals("1")){   // Realizando pedido por el cliente, ver detalle desde cliente
            //Obtener los parametros del ciente de SharedPreferences
            SharedPreferences pref= getSharedPreferences("datos", Context.MODE_PRIVATE);
            mostrarDatosClientes((pref.getString("apellidoUser","")+" "+pref.getString("nombreUser","")),pref.getString("cedulaUser",""));
            txtNumPedidoFactura.setText("Num Pedido:  "+"0"); //Contar las facturas de este cliente
            Bundle bundle = new Bundle();
            bundle = getIntent().getBundleExtra("lista");
            detalle = (List<DetallePedidoFactura>) bundle.getSerializable("detalle"); //Obtener el detalle de pedido.
            initDetalleFromList(detalle,true);

        }else if (opcion.equals("2")){ // Realizando factura desde el administrador, ver detalle desde administrador
            String[] partes = clienteSeleccionado.split("-");
            String cli = partes[0];
            String cedula = partes.length > 1 ? partes[1] : "";
            txtNumPedidoFactura.setText("Num Factura:  "+bb.getString("idFactura"));
            mostrarDatosClientes(cli,cedula);
            Bundle bundle = new Bundle();
            bundle = getIntent().getBundleExtra("lista");
            detalle = (List<DetallePedidoFactura>) bundle.getSerializable("detalle"); //Obtener el detalle de factura.
            initDetalleFromList(detalle,true);

        }else{
            // Lista de Facturas ya realizados, ver detalle desde administrador
            int idFactura=Integer.parseInt(bb.getString("id"));
            txtNumPedidoFactura.setText("Num Factura:  "+idFactura);
            PedidoFactura pf=mPedidoFactura.getPedidoFactura(idFactura,"factura");
            Toast.makeText(this,"PF. "+pf.getNombreCliente(),Toast.LENGTH_SHORT).show();
            mostrarDatosClientes(pf.getApellidoCliente()+" "+pf.getNombreCliente(),pf.getCedulaCliente());
            DetallePedidoFactura[] detalle = mDetalle.getPedidoFacturaDetalle(idFactura);
            List<DetallePedidoFactura> detalleA=Arrays.asList(detalle);
            initDetalleFromList(detalleA,false);
        }
    }

    public void initDetalleFromList(List<DetallePedidoFactura> detalle, boolean isGeneratingPF){
        listDetalle= new ArrayList<>();
       if(detalle!=null) {
            listDetalle= detalle;
            listAdapter = new ListDetallePedAdapter(0,listDetalle, this, new ListDetallePedAdapter.OnItemClickListener() {

                @Override
                public void OnItemClick(DetallePedidoFactura det, int pos) {
                    if(isGeneratingPF) {
                        eliminarDetalle(det, pos);
                    }
                }
            });
            RecyclerView recyclerView = findViewById(R.id.listRecyclerViewDetallePed);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);

            //Calcular iva y subtotal
           mostrarValoresCalculados(listDetalle);
        }
    }

    public void eliminarDetalle(DetallePedidoFactura ped, int posicion){
        if(opcion.equals("1")){   //Elimina el detalle de pedido
            detalle.remove(posicion);
        }else{                    //Elimina el detalle de factura
            detalle.remove(posicion);
        }

        listAdapter.notifyDataSetChanged();
        List<DetallePedidoFactura> lista=listAdapter.getPedidoDetalles();
        mostrarValoresCalculados(lista);
    }
    public void mostrarValoresCalculados( List<DetallePedidoFactura> list){
        //Calcular iva y subtotal
        double subTotal=0;
        double iva=0;

        for (DetallePedidoFactura dt: list) {
            subTotal+= Double.parseDouble(dt.getSubtotal());
            iva+= Double.parseDouble((dt.getSubtotal()))*0.12;
        }
        txtIva.setText("Iva: "+Math.round((iva) * 100.0) / 100.0);
        txtSubtotal.setText("Subtotal: "+Math.round((subTotal) * 100.0) / 100.0);
        txtTotal.setText("Total: "+Math.round((iva+subTotal) * 100.0) / 100.0);
    }

    public void mostrarDatosClientes(String cliente,String cedula){
        txtCliente.setText(cliente);
        txtCedulaCli.setText(cedula);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(opcion.equals("0")){ //Debe regresar a pedidos en la cuenta de administrador
            Intent intento = new Intent(VerDetallePedidoFacturaActivity.this, AdminShowPedidoFacturaActivity.class);
            startActivity(intento);

        }else if(opcion.equals("1")){ //Debe regresar a realizar campra en la cuenta de cliente
            List<DetallePedidoFactura> ped = listAdapter.getPedidoDetalles();
            Intent intento = new Intent(VerDetallePedidoFacturaActivity.this, Compra_FacturacionActivity.class);
            Bundle bundle = new Bundle();
            ArrayList<DetallePedidoFactura> lista = new ArrayList<>();
            for (int i = 0; i < ped.size(); i++) {
                lista.add(ped.get(i));
            }
            intento.putExtra("base", "detalle");
            bundle.putSerializable("detalle", lista);
            intento.putExtra("lista", bundle);
            startActivity(intento);

        }else if (opcion.equals("2")){ //Debe regresar a realizar facturación en la cuenta de administrador
            List<DetallePedidoFactura> ped = listAdapter.getPedidoDetalles();
            Intent intento = new Intent(VerDetallePedidoFacturaActivity.this, Compra_FacturacionActivity.class);
            Bundle bundle = new Bundle();
            ArrayList<DetallePedidoFactura> lista = new ArrayList<>();
            for (int i = 0; i < ped.size(); i++) {
                lista.add(ped.get(i));
            }
            intento.putExtra("base", "detalle");
            bundle.putSerializable("detalle", lista);
            intento.putExtra("lista", bundle);
            startActivity(intento);

        }else{  //Debe regresar a facturas en la cuenta de administrador
            Intent intento = new Intent(VerDetallePedidoFacturaActivity.this, AdminShowPedidoFacturaActivity.class);
            startActivity(intento);
        }
        return false;
    }

}