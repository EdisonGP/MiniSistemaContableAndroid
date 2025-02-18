package com.example.crud_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Adapter.ListProductoAdapter;
import Entities.User;
import Manager.ManagerPedidoFactura;
import Manager.ManagerUser;
import Manager.ManagerDetallePedidoFactura;
import Manager.ManagerProductos;
import Entities.DetallePedidoFactura;
import Entities.Producto;

public class Compra_FacturacionActivity extends AppCompatActivity {
    static ManagerProductos mProducto;
    static ManagerPedidoFactura mPedidoFactura;
    static ManagerDetallePedidoFactura mDetalle;
    ManagerUser mUsuario;
    List<Producto> listaProd;
    static List<DetallePedidoFactura> productosFacturaDetalle;
    ListProductoAdapter listAdapter;
    SearchView searchView;
    Spinner spinner;
    //AutoCompleteTextView spinner ;
    Button btnGuardarPedido_Factura,btnCancelarPedido_Factura,btnDetallePedido_Factura;
    static TextView txtSubtotalValue;
    static TextView txtIvaValue;
    static TextView txtTotalValue;
    static double totalFactura;
    static double totalIva;
    int cant,idProducto;
    static boolean productosSeleccionadoParaFactura;
    String idUsuario;
    boolean isAdmin;
    User clienteSeleccionado;
    static long idFactura ; // Variable para almacenar el ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_facturacion);

        //Inicialización de variables
        txtSubtotalValue =(TextView) findViewById(R.id.txtSubtotalValue);
        txtIvaValue =(TextView) findViewById(R.id.txtIvaValue);
        txtTotalValue =(TextView) findViewById(R.id.txtTotalValue);
        productosSeleccionadoParaFactura=false;
        btnGuardarPedido_Factura=(Button)findViewById(R.id.btnGuardar);
        btnCancelarPedido_Factura=(Button)findViewById(R.id.btnCancelar);
        btnDetallePedido_Factura=(Button)findViewById(R.id.btnVerDetalle);
        mProducto=new ManagerProductos(this, "compras",1);
        mPedidoFactura =new ManagerPedidoFactura(this, "compras",1);
        mDetalle=new ManagerDetallePedidoFactura(this, "compras",1);
        productosFacturaDetalle=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.spinner);

        searchView= (SearchView)findViewById(R.id.search_viewProdFacturacion);
        searchView.clearFocus();
        searchView.setQueryHint("search products ..");
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

        //Saber si el que esta en este Activity es el Administrador o un Ciente
        mUsuario = new ManagerUser(this, "compras", 1);
        SharedPreferences pref= getSharedPreferences("datos", Context.MODE_PRIVATE);
        idUsuario=pref.getString("idUser","");
        User user = mUsuario.getUser(Integer.parseInt(idUsuario));
        isAdmin = verificarSiEsAdmin(user); // Método que verifica el rol
        init(); //Llama a método listar productos

        if(isAdmin) {
            initClient(); //Llama a método listar clientes
            btnGuardarPedido_Factura.setText("SAVE");
        }else{
            //Ocultado Vistas de seleccion de cliente utilizado en facturacion desde Administracion
            TextView textView = findViewById(R.id.textSelectClient);
            textView.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            btnGuardarPedido_Factura.setText("NEXT");
        }

        //Recibe los parámetros de regreso desde el Detalle
        Bundle bundle = this.getIntent().getExtras();
        if(bundle.getString("base").equals("detalle")){
            bundle = getIntent().getBundleExtra("lista");
            ArrayList<DetallePedidoFactura> list= (ArrayList<DetallePedidoFactura>) bundle.getSerializable("detalle");
            productosFacturaDetalle=list;
            productosSeleccionadoParaFactura=true;
            mostrarCamposCalculados(productosFacturaDetalle);
        }

        btnGuardarPedido_Factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (isAdmin) { //Genera factura
                        saveFacturaPedido(getBaseContext(),clienteSeleccionado.getIdUsuario(), "factura", "Factura guardada");    //Guarda factura generado por el vendedor
                        Intent intento = new Intent(getBaseContext(), AdminShowPedidoFacturaActivity.class);
                        startActivity(intento);

                    } else { //Genera Pedido,pero antes debe ir a realizar el pago
                        SharedPreferences pref= getSharedPreferences("datos", Context.MODE_PRIVATE);//Obtengo al cliente logeado
                        Intent intent = new Intent(Compra_FacturacionActivity.this, PagoActivity.class);
                        intent.putExtra("clientId", pref.getString("idUser",""));
                        intent.putExtra("clientCedula", pref.getString("cedulaUser",""));
                        intent.putExtra("clientName", pref.getString("nombreUser",""));
                        intent.putExtra("clientApellido", pref.getString("apellidoUser",""));
                        intent.putExtra("totalAmount",totalFactura);
                        startActivity(intent);
                    }
            }
        });

        btnCancelarPedido_Factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LimpiarCampos();
                Toast.makeText(getBaseContext(), "Factura cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        btnDetallePedido_Factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento =new Intent(Compra_FacturacionActivity.this, VerDetallePedidoFacturaActivity.class);
                intento.putExtra("opcion",isAdmin ? "2" : "1");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                intento.putExtra("fecha", sdf.format(new Date()));
                intento.putExtra("total",String.valueOf(totalFactura));
                if(isAdmin){intento.putExtra("cliente",clienteSeleccionado.getApellido()+ " "+clienteSeleccionado.getNombre()+"-"+clienteSeleccionado.getCedula());}
                if(isAdmin){intento.putExtra("idFactura",String.valueOf(idFactura));}
                Bundle bundle = new Bundle();
                ArrayList<DetallePedidoFactura> lista = new ArrayList<>();
                for (int i = 0; i < productosFacturaDetalle.size(); i++) {
                    lista.add(productosFacturaDetalle.get(i));
                }
                bundle.putSerializable("detalle", lista);
                intento.putExtra("lista",bundle);
                startActivity(intento);
            }
        });

    }
    public void init(){
        Producto[] productos = mProducto.allProductos();
        listaProd= new ArrayList<>();

        if(productos!=null) {
            listaProd= Arrays.asList(productos);
            listAdapter = new ListProductoAdapter(listaProd, this, true,new ListProductoAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(Producto prod) {
                    productSelected(prod);
                }
            });
            RecyclerView recyclerView = findViewById(R.id.listRecyclerViewTwoColum);
            recyclerView.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.HORIZONTAL,false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);

            //Modificar altura de RecyclerVie
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            float density = getResources().getDisplayMetrics().density;
            int pixelHeight = (int) (400 * density);
            params.height = pixelHeight;
            recyclerView.setLayoutParams(params);
        }
    }


    public void initClient(){

        User[] clientes = mUsuario.allUser("cliente");
        List<User> lista= new ArrayList<>();

        if(clientes!=null) {
            lista= Arrays.asList(clientes);
        }
        ArrayAdapter< User> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,lista);
        spinner.setAdapter(adapter);

        // Capturar el primer elemento por defecto
        if (!lista.isEmpty()) {
            clienteSeleccionado = lista.get(0);
        }

        // Listener para capturar la selección del usuario

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clienteSeleccionado = (User) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Este método se llama si no hay selección
                clienteSeleccionado = null;
            }
        });
    }


    public void productSelected (Producto prod){
        AlertDialog.Builder e=new AlertDialog.Builder(Compra_FacturacionActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.modal_cantidad_compra,null);
        e.setView(view);

        AlertDialog ad=e.create();
        ad.show();

        Producto p=prod;
        idProducto=prod.getId();
        EditText cantidad=(EditText) view.findViewById(R.id.txtCantidadPedido);
        Button btnSeleccionarProducto=(Button) view.findViewById(R.id.btnSeleccionarProducto);

        cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cantidad.getText().hashCode() == s.hashCode()){
                    if(!(cantidad.getText().toString().matches("[+-]?\\d*(\\.\\d+)?"))){ //Verifica si es dígito
                        cantidad.setError(null);
                        cantidad.setError("Ingresar un número");
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSeleccionarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cant = Integer.parseInt(cantidad.getText().toString());
                Producto producto = mProducto.getProducto(idProducto);

                if ((Integer.parseInt(producto.getStock()) - cant) >= 0) { //Verifica que alcance los productos solicitados
                    double subtotal = Math.round((cant * Double.parseDouble(p.getPrice())) * 100.0) / 100.0;
                    productosFacturaDetalle.add(new DetallePedidoFactura(idProducto, p.getDescripcion(), cant, String.valueOf(subtotal)));
                    productosSeleccionadoParaFactura = true;
                    ad.dismiss();
                    mostrarCamposCalculados(productosFacturaDetalle);
                    Toast.makeText(getBaseContext(), "Producto seleccionado", Toast.LENGTH_SHORT).show();

                }else{
                    int stockDisponible=Integer.parseInt(producto.getStock())-0;
                    Toast.makeText(getBaseContext(), "Producto insuficiente, puedes escoger solamente "+stockDisponible+" productos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void filterList(String query){
        List<Producto> filterList= new ArrayList<>();
        for (int i = 0; i <listaProd.size() ; i++) {
            if(listaProd.get(i).getDescripcion().toLowerCase().contains(query.toLowerCase())){
                filterList.add(listaProd.get(i));
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(getBaseContext(), "No data found", Toast.LENGTH_SHORT).show();
        }else{
            listAdapter.setFilterList(filterList);
        }
    }

    public void mostrarCamposCalculados(List<DetallePedidoFactura>lista){
        double subTotal=0;
        double iva=0;
        for (DetallePedidoFactura dt: lista) {
            subTotal+= Double.parseDouble(dt.getSubtotal());
            iva+= Double.parseDouble((dt.getSubtotal()))*0.12;
        }
        totalIva=Math.round(((iva)) * 100.0) / 100.0;
        totalFactura=Math.round(((subTotal+iva)) * 100.0) / 100.0;
        txtSubtotalValue.setText(Math.round(((subTotal)) * 100.0) / 100.0+"");
        txtIvaValue.setText(totalIva+"");
        txtTotalValue.setText(totalFactura+"");
    }

    public static void LimpiarCampos(){
        productosFacturaDetalle=new ArrayList<>();
        totalFactura = 0;
        totalIva = 0;
        txtSubtotalValue.setText("");
        txtIvaValue.setText("");
        txtTotalValue.setText("");
    }

    public static void saveFacturaPedido(Context context, int idCliente, String categoria, String sms){
        if (productosSeleccionadoParaFactura) { //Valida antes de guardar que haya un producto seleccionado
            String fechaCadena = new SimpleDateFormat("dd-MM-yyyy").format(new Date());         //Obtener la fecha actual del sistema
            idFactura = mPedidoFactura.insertPedidoFactura(String.valueOf(idCliente), "", String.valueOf(totalFactura), String.valueOf(totalIva), fechaCadena, categoria);

            if (idFactura != -1) {
                mDetalle.insertPedidoFacturaDetalle(productosFacturaDetalle, idFactura);      //Guardar Detalle
                mProducto.updateStockProducto(productosFacturaDetalle,"reduce");    //Reducir Stock
                productosSeleccionadoParaFactura = false;     //Cambia de estado para que nuevamente seleccione productos para otra factura o pedido
                Toast.makeText(context, sms, Toast.LENGTH_SHORT).show();
                LimpiarCampos();      //Limpieza de datos
            }
        }else{
            Toast.makeText(context, "Debes seleccionar algún producto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intento = new Intent(Compra_FacturacionActivity.this, MenuActivity.class);
        startActivity(intento);
    }

    public boolean verificarSiEsAdmin(User user){
        if (user.getCategoria().equals("administrador")) {
            return true;
        }else{
            return false;
        }
    }
}