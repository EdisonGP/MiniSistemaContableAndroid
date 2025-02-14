package com.example.crud_app;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ListProductoAdapter;
import Manager.ManagerProductos;
import Entities.Producto;

public class AdminProductActivity extends AppCompatActivity {
    private ManagerProductos crud;
    private List<Producto> listaProd;
    private FloatingActionButton floatBtn;
    private SearchView searchView;
    private ListProductoAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_producto);

        initializeViews();
        setupSearchView();
        init(); // Listar productos

        floatBtn.setOnClickListener(v -> showProductManagementDialog(null));
    }

    private void initializeViews() {
        floatBtn = findViewById(R.id.floatBtn);
        crud = new ManagerProductos(this, "compras", 1);
        searchView = findViewById(R.id.search_viewProd1);
        searchView.clearFocus();
        searchView.setQueryHint("search products ..");
    }

    private void setupSearchView() {
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
        });
    }

    public void init() {
        Producto[] productos = crud.allProductos();
        listaProd = new ArrayList<>();

        if (productos != null) {
            listaProd = Arrays.asList(productos);
            listAdapter = new ListProductoAdapter(listaProd, this,false, this::showProductManagementDialog);

            RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
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
        List<Producto> filterList = new ArrayList<>();
        for (Producto producto : listaProd) {
            if (producto.getDescripcion().toLowerCase().contains(query.toLowerCase())) {
                filterList.add(producto);
            }
        }

        if (filterList.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        } else {
            listAdapter.setFilterList(filterList);
        }
    }
}