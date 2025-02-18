package com.example.crud_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Build;
import android.os.Bundle;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import Adapter.ListPedidoFacturaAdapter;
import Entities.PedidoFactura;
import Manager.ManagerPedidoFactura;
import Utils.CustomDateRangeDialog;
import Utils.SearchSpinnerHelper;


public class AdminShowPedidoFacturaActivity extends AppCompatActivity implements Serializable {

    ManagerPedidoFactura gestion;
    List<PedidoFactura> listPedidoFactura;
    SearchView searchView;
    ListPedidoFacturaAdapter listAdapter;
    String categoria;
    String usuario;
    ConstraintLayout mainLayout;
    SearchSpinnerHelper searchSpinnerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_pedido_factura);

        gestion=new ManagerPedidoFactura(this, "compras",1);
        mainLayout = findViewById(R.id.layout_mainPF);

        //Obtener los parametros del activity anterior.
        Bundle bb=this.getIntent().getExtras();
        categoria=bb.getString("categoria");
        usuario=bb.getString("usuario");
        init(); //Llama a método listar facturas
        initSearchAndFilter(); //LLama al metodo que establece las vista de Buscar y filtrar

    }

    public void init() {
        PedidoFactura[] pf;

        if (this.usuario.equals("administrador")) {
            pf = gestion.allPedidoFacturas(categoria);
        } else {
            SharedPreferences pref = getSharedPreferences("datos", Context.MODE_PRIVATE);
            String idUsuario = pref.getString("idUser", "");
            pf = gestion.getAllPedidoOfClient(categoria, Integer.parseInt(idUsuario));
        }

        listPedidoFactura = new ArrayList<>();         // Inicializar la lista aunque no haya productos
        if (pf != null && pf.length > 0)          listPedidoFactura = Arrays.asList(pf);

        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        TextView txtNoData = findViewById(R.id.txtNoData);

        if (listPedidoFactura.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            listAdapter = new ListPedidoFacturaAdapter(listPedidoFactura, categoria, usuario, gestion, this, new ListPedidoFacturaAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(PedidoFactura fac) {
                    try {
                        Intent intento = new Intent(AdminShowPedidoFacturaActivity.this, VerDetallePedidoFacturaActivity.class);
                        intento.putExtra("opcion", (categoria.equals("pedido") ? "0" : "3"));
                        intento.putExtra("id", String.valueOf(fac.getId()));
                        intento.putExtra("fecha", fac.getFecha());
                        intento.putExtra("total", fac.getTotal());
                        startActivity(intento);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminShowPedidoFacturaActivity.this, "Error al abrir el detalle", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            if (recyclerView != null) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(listAdapter);
            }
        }

    }


    private void initSearchAndFilter() {
        searchSpinnerHelper = new SearchSpinnerHelper(this);

        // Opciones para el filtro
        String[] filterOptions;
        if(usuario.equals("administrador")) {
            if (categoria.equals("pedido")) { //Ver facturas o pedidos desde administrador
                filterOptions = new String[]{"Todos", "Facturados", "Sin facturar"};

            } else {
                filterOptions = new String[]{"Todos", "Despachados", "Sin despachar"};
            }
        }else{
            filterOptions = new String[]{"Todos", "Por fechas"};
        }

        // Configurar todos loss parametros en un solo paso
        LinearLayout container = searchSpinnerHelper.setupSearchAndFilter(
                mainLayout,
                filterOptions,
                this::handleFilterSelected,
                this::filterList
        );

        // Actualizar constraints del RecyclerView para que esté debajo del contenedor
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
        params.topToBottom = container.getId();
        recyclerView.setLayoutParams(params);
    }

    private void handleFilterSelected(String filter) {
        if(usuario.equals("administrador")) {
            if (this.categoria.equals("pedido")) { //Ver facturas o pedidos desde administrador
                handleFilter(filter,"Facturados","Sin facturar");

            } else {
                handleFilter(filter,"Despachados","Sin despachar");
            }
        }else{ //Ver desde cliente
            handleFilter(filter,"Por fechas","");
        }
    }

    private void handleFilter(String filter, String opt2, String opt3) {

        if (listPedidoFactura == null || listPedidoFactura.isEmpty()) {
            // Si no hay datos, simplemente actualizar el adapter con una lista vacía
            listAdapter.setFilterList(new ArrayList<>());
            return;
        }

        List<PedidoFactura> filteredList = new ArrayList<>();

        if (filter.equals("Todos")) {
            listAdapter.setFilterList(listPedidoFactura);

        } else if (filter.equals("Por fechas")) {
            CustomDateRangeDialog dialog = new CustomDateRangeDialog(this);
            dialog.show((fechaInicio, fechaFin) -> {
                List<PedidoFactura> filteredByDate = new ArrayList<>();
                for (PedidoFactura pf : listPedidoFactura) {
                    if (isDateInRange(pf.getFecha(), fechaInicio, fechaFin)) {
                        filteredByDate.add(pf);
                    }
                }
                listAdapter.setFilterList(filteredByDate);

                // Añade mensaje si no hay resultados
                if (filteredByDate.isEmpty()) {
                    Toast.makeText(this, "No se encontraron registros en ese rango de fechas", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (filter.equals(opt2)) {
            if(categoria.equals("pedido")) {
                filteredList = recorrerPedidoFactura(PedidoFactura::getFacturado, "si");
            } else {
                filteredList = recorrerPedidoFactura(PedidoFactura::getDespachado, "si");
            }
            listAdapter.setFilterList(filteredList);

        } else if (filter.equals(opt3) && !opt3.isEmpty()) {
            if(categoria.equals("pedido")) {
                filteredList = recorrerPedidoFactura(PedidoFactura::getFacturado, "no");
            } else {
                filteredList = recorrerPedidoFactura(PedidoFactura::getDespachado, "no");
            }
            listAdapter.setFilterList(filteredList);
        }

        // Muestra un mensaje si no hay resultados (excepto para el filtro por fechas que ya tiene su propio mensaje)
        if (filteredList.isEmpty() && !filter.equals("Todos") && !filter.equals("Por fechas")) {
            Toast.makeText(this, "No se encontraron registros para el filtro seleccionado", Toast.LENGTH_SHORT).show();
        }
    }

    public List<PedidoFactura> recorrerPedidoFactura(Function<PedidoFactura, String> metodo,String condicion) {
        List<PedidoFactura> filteredList = new ArrayList<>();
        for (PedidoFactura producto : listPedidoFactura) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (metodo.apply(producto).equals(condicion)) { // Se usa la función pasada como parámetro
                    filteredList.add(producto);
                }
            }
        }
        return filteredList;
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date dateToCheck = sdf.parse(date);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            return dateToCheck != null && start != null && end != null && !dateToCheck.before(start) && !dateToCheck.after(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    //==============================================
    public void filterList(String query) {
        List<PedidoFactura> filteredList = new ArrayList<>();

        if (query.isEmpty()) {         // Si el campo de búsqueda está vacío, mostrar todos los productos
            listAdapter.setFilterList(listPedidoFactura);
            return;
        }

        String lowerQuery = query.toLowerCase();

        // Filtrar por nombre o apellido
        for (PedidoFactura pf : listPedidoFactura) {
            if (pf.getNombreCliente().toLowerCase().contains(lowerQuery) || pf.getApellidoCliente().toLowerCase().contains(lowerQuery)) {
                filteredList.add(pf);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No se encontraron registros", Toast.LENGTH_SHORT).show();
        } else {
            listAdapter.setFilterList(filteredList);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intento = new Intent(AdminShowPedidoFacturaActivity.this, MenuActivity.class);
        startActivity(intento);
    }

}