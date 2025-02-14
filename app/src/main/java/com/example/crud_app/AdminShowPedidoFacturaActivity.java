package com.example.crud_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ListPedidoFacturaAdapter;
import Entities.PedidoFactura;
import Manager.ManagerPedidoFactura;


public class AdminShowPedidoFacturaActivity extends AppCompatActivity implements Serializable {

    ManagerPedidoFactura gestion;
    List<PedidoFactura> listFactura;
    SearchView searchView;
    ListPedidoFacturaAdapter listAdapter;
    String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_pedido_factura);

        gestion=new ManagerPedidoFactura(this, "compras",1);
        searchView = (SearchView) findViewById(R.id.search_viewFac);
        searchView.clearFocus();
        searchView.setQueryHint("search orders ..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return false;
            }
        });;

        //Obtener los parametros del activity anterior.
        Bundle bb=this.getIntent().getExtras();
        categoria=bb.getString("categoria");
        init(); //Llama a método listar facturas

    }
    public void init() {
        PedidoFactura[] facturas = gestion.allPedidoFacturas(categoria);
        listFactura = new ArrayList<>();

        if (facturas != null) {
            listFactura = Arrays.asList(facturas);
            listAdapter = new ListPedidoFacturaAdapter(listFactura,categoria,gestion,this, new ListPedidoFacturaAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(PedidoFactura fac) {facturaSelected(fac);

                }
            });
            RecyclerView recyclerView = findViewById(R.id.listRecyclerViewFac);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);
        }
    }

    public void facturaSelected(PedidoFactura fac){
        Intent intento =new Intent(AdminShowPedidoFacturaActivity.this, VerDetallePedidoFacturaActivity.class);
        intento.putExtra("opcion",(categoria.equals("pedido") ? "0":"3"));
        intento.putExtra("id",String.valueOf(fac.getId()));
        intento.putExtra("fecha",fac.getFecha());
        intento.putExtra("total",fac.getTotal());
        startActivity(intento);
    }
    public void filterList(String query){
        List<PedidoFactura> filterList= new ArrayList<>();
        for (int i = 0; i <listFactura.size() ; i++) {
            if(listFactura.get(i).getApellidoCliente().toLowerCase().contains(query.toLowerCase())){
                filterList.add(listFactura.get(i));
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(getBaseContext(), "No data found", Toast.LENGTH_SHORT).show();
        }else{
            listAdapter.setFilterList(filterList);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intento =new Intent(AdminShowPedidoFacturaActivity.this, MenuActivity.class);
        startActivity(intento);
    }

}