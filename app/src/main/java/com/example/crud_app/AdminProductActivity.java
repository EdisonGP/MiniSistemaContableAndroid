package com.example.crud_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ListProductoAdapter;
import Manager.ManagerProductos;
import Entities.Producto;
import Utils.ProductPopupWindow;
import Utils.SearchSpinnerHelper;

public class AdminProductActivity extends AppCompatActivity {
    private ManagerProductos crud;
    private List<Producto> listaProd;
    private FloatingActionButton floatBtn;
    private ListProductoAdapter listAdapter;
    private ConstraintLayout mainLayout;
    private SearchSpinnerHelper searchSpinnerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_producto);

        initializeViews();
        setupSearchAndFilter();
        init(); // Listar productos

        floatBtn.setOnClickListener(v -> showProductManagementDialog(null));
    }

    private void initializeViews() {
        mainLayout = findViewById(R.id.layout_main);
        floatBtn = findViewById(R.id.floatBtn);
        crud = new ManagerProductos(this, "compras", 1);
    }

    private void setupSearchAndFilter() {
        try {
            // Usar this en lugar de getBaseContext()
            searchSpinnerHelper = new SearchSpinnerHelper(this);

            // Validar que mainLayout no sea null
            if (mainLayout == null) {
                Log.e("AdminProductActivity", "mainLayout is null");
                return;
            }

            // Opciones para el filtro
            String[] filterOptions = {"Todos", "Stock>50", "Stock<5"};

            // Configurar todo en un solo paso con manejo de excepciones
            LinearLayout container = searchSpinnerHelper.setupSearchAndFilter(
                    mainLayout,
                    filterOptions,
                    this::handleFilterSelected,
                    this::filterList
            );

            // Validar que el container se haya creado correctamente
            if (container == null) {
                Log.e("AdminProductActivity", "Container creation failed");
                return;
            }

            // Actualizar constraints del RecyclerView con validaciones
            RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
            if (recyclerView != null) {
                ConstraintLayout.LayoutParams params =
                        (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
                if (params != null) {
                    params.topToBottom = container.getId();
                    recyclerView.setLayoutParams(params);
                }
            }
        } catch (Exception e) {
            Log.e("AdminProductActivity", "Error in setupSearchAndFilter: " + e.getMessage());
            // Opcional: Mostrar un mensaje al usuario
            Toast.makeText(this, "Error al configurar la búsqueda", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFilterSelected(String filter) {
        if (listaProd == null || listaProd.isEmpty()) {
            // Si no hay datos, simplemente actualizar el adapter con una lista vacía
            listAdapter.setFilterList(new ArrayList<>());
            return;
        }

        if (filter.equals("Todos")) {
            // Mostrar todos los productos
            listAdapter.setFilterList(listaProd);
        } else if (filter.equals("Stock>50")) {
            // Filtrar productos con stock mayor a 50
            List<Producto> filteredList = new ArrayList<>();
            for (Producto producto : listaProd) {
                if (Integer.parseInt(producto.getStock()) > 50) {
                    filteredList.add(producto);
                }
            }
            listAdapter.setFilterList(filteredList);
        } else if (filter.equals("Stock<5")) {
            // Filtrar productos con stock menor a 5
            List<Producto> filteredList = new ArrayList<>();
            for (Producto producto : listaProd) {
                if (Integer.parseInt(producto.getStock()) < 5) {
                    filteredList.add(producto);
                }
            }
            listAdapter.setFilterList(filteredList);
        }
    }

    public void init() {
        Producto[] productos = crud.allProductos();

        listaProd = new ArrayList<>();         // Inicializar la lista aunque no haya productos

        if (productos != null && productos.length > 0) {          // Procesar productos existentes
            StringBuilder prodStockBajo = new StringBuilder("");
            boolean hayStockBajo = false;

            if (hayStockBajo) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Alerta de Productos con Stock Bajo")
                        .setMessage(prodStockBajo)
                        .setPositiveButton("OK", null)
                        .show();
            }

            listaProd = Arrays.asList(productos);
        }


        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        TextView txtNoData = findViewById(R.id.txtNoData);

        if (listaProd.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            listaProd = Arrays.asList(productos);
        }
        // Crear el adapter incluso si la lista está vacía
        listAdapter = new ListProductoAdapter(listaProd, this, false, this::showProductManagementDialog);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);
        }
    }
    private void showProductManagementDialog(Producto prod) {
        ProductPopupWindow dialog = new ProductPopupWindow(
                this,
                prod,
                (producto, actionType) -> {
                    switch (actionType) {
                        case CREATE:
                            handleCreateProduct(producto);
                            break;
                        case UPDATE:
                            handleUpdateProduct(producto);
                            break;
                        case DELETE:
                            handleDeleteProduct(producto);
                            break;
                    }
                }
        );
        dialog.show(findViewById(android.R.id.content));
    }

    private void handleCreateProduct(Producto product) {
        boolean success = crud.insertProducto(
                product.getDescripcion(),
                Integer.parseInt(product.getStock()),
                product.getStatus(),
                product.getPrice()
        );

        if (success) {
            Toast.makeText(this, "Producto insertado correctamente", Toast.LENGTH_SHORT).show();
            init();
        } else {
            Toast.makeText(this, "Error al insertar producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUpdateProduct(Producto product) {
        Producto productoActualizado = new Producto(
                product.getId(),
                product.getDescripcion(),
                String.valueOf(product.getStock()),
                product.getStatus(),
                String.valueOf(product.getPrice())
        );

        boolean success = crud.updateProducto(productoActualizado);
        if (success) {
            Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
            init();
        } else {
            Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteProduct(Producto product) {
        boolean success = crud.deleteProducto(product.getId());
        if (success) {
            Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
            init();
        } else {
            Toast.makeText(this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
        }
    }

    public void filterList(String query) {
        List<Producto> filteredList = new ArrayList<>();

        // Si el campo de búsqueda está vacío, mostrar todos los productos
        if (query.isEmpty()) {
            listAdapter.setFilterList(listaProd);
            return;
        }

        // Filtrar por descripción
        if (listaProd != null && !listaProd.isEmpty()) {
            for (Producto producto : listaProd) {
                if (producto.getDescripcion().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(producto);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
        } else {
            listAdapter.setFilterList(filteredList);
        }
    }
}