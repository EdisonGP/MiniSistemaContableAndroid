package com.example.crud_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import Entities.User;
import Manager.ManagerUser;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ManagerUser crud;
    String idUsuario;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layoutA);
        navigationView =(NavigationView) findViewById(R.id.nav_viewA);
        toolbar=(Toolbar) findViewById(R.id.toolbarA);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_homeA);

        // Clase que administra el CRUD
        crud = new ManagerUser(this, "compras", 1);

        //Obtener los parametros de SharedPreferences
        SharedPreferences pref= getSharedPreferences("datos", Context.MODE_PRIVATE);
        idUsuario=pref.getString("idUser","");
        User user = crud.getUser(Integer.parseInt(idUsuario));
        isAdmin = verificarSiEsAdmin(user); // Método que verifica el rol

        // Asigna el menú dinámicamente según el rol
        navigationView.getMenu().clear(); // Borra cualquier menú previo
        navigationView.inflateMenu(isAdmin ? R.menu.main_menu_admin : R.menu.main_menu_client);

    }
    @Override
    public void onBackPressed(){ //Para cuando presiona sobre el menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (isAdmin) { // Inicio para ADMINISTRADOR
            switch (itemId) {
                case R.id.nav_homeA:
                    break;
                case R.id.show_pedidosA:
                    Intent intento1 = new Intent(this, AdminShowPedidoFacturaActivity.class);
                    intento1.putExtra("categoria", "pedido");
                    startActivity(intento1);
                    break;
                case R.id.invoiceA:
                    Intent intento2 = new Intent(this, Compra_FacturacionActivity.class);
                    intento2.putExtra("base", "menu");
                    startActivity(intento2);
                    break;
                case R.id.show_invoiceA:
                    Intent intento3 = new Intent(this, AdminShowPedidoFacturaActivity.class);
                    intento3.putExtra("categoria", "factura");
                    startActivity(intento3);
                    break;
                case R.id.gestion_productosA:
                    startActivity(new Intent(this, AdminProductActivity.class));
                    break;
                case R.id.show_clientesA:
                    startActivity(new Intent(this, AdminShowClientsActivity.class));
                    break;
                case R.id.logoutA:
                    startActivity(new Intent(this, MainActivity.class));
                    break;
            }
        } else { //Inicio para Cliente
            switch (itemId) {
                case R.id.nav_home:
                    break;
                case R.id.shop_client:
                    Intent intento1 = new Intent(this, Compra_FacturacionActivity.class);
                    intento1.putExtra("base", "menu");
                    startActivity(intento1);
                    break;
                case R.id.client_information:
                    startActivity(new Intent(this, ClientInformationActivity.class));
                    break;
                case R.id.share:
                    Toast.makeText(getBaseContext(), "Share your ideas", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.logout:
                    SharedPreferences.Editor editor = getSharedPreferences("datos", MODE_PRIVATE).edit();
                    editor.clear().apply(); // Elimina las preferencias del usuario
                    startActivity(new Intent(this, MainActivity.class));
                    break;
            }
        }
        return true;
    }

    public boolean verificarSiEsAdmin(User user){
        if (user.getCategoria().equals("administrador")) {
            return true;
        }else{
            return false;
        }

    }
}