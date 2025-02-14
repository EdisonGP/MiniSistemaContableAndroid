package com.example.crud_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ListClientesAdapter;
import Entities.User;
import Manager.ManagerUser;

public class AdminShowClientsActivity extends AppCompatActivity{
    ManagerUser crud;
    List<User> listClientes;
    SearchView searchView;
    ListClientesAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_clients);

        crud = new ManagerUser(this, "compras", 1);
        searchView = (SearchView) findViewById(R.id.search_viewCli);
        searchView.clearFocus();
        searchView.setQueryHint("search clients ..");
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
        init(); //Llama a método listar clientes
    }
    public void init() {
        User[] clientes = crud.allUser("cliente");
        listClientes = new ArrayList<>();

        if (clientes != null) {
            listClientes = Arrays.asList(clientes);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                listAdapter = new ListClientesAdapter(listClientes, this,crud, new ListClientesAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(User cli) {
                        clienteSelected(cli);
                    }
                });
            }
            RecyclerView recyclerView = findViewById(R.id.listRecyclerViewCli);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);
        }
    }

    public void clienteSelected(User cli){
        ClienteDescriptionPopup modal = new ClienteDescriptionPopup(this, cli);
        modal.show(findViewById(android.R.id.content));
    };

    public void filterList(String query){
        List<User> filterList= new ArrayList<>();
        for (int i = 0; i <listClientes.size() ; i++) {
            if(listClientes.get(i).getApellido().toLowerCase().contains(query.toLowerCase())){
                filterList.add(listClientes.get(i));
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(getBaseContext(), "No data found", Toast.LENGTH_SHORT).show();
        }else{
            listAdapter.setFilterList( filterList);
        }
    }
}