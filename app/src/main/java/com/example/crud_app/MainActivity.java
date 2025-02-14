package com.example.crud_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Entities.User;
import Manager.ManagerUser;

public class MainActivity extends AppCompatActivity {

    EditText txtUser,txtPassword;
    Button btnLogin;
    ManagerUser crud;
    TextView btnCreateCount;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser=(EditText) findViewById(R.id.txtUser);
        txtPassword=(EditText) findViewById(R.id.txtPassword);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        btnCreateCount=(TextView) findViewById(R.id.btnCrearCuenta);
        SpannableString line = new SpannableString("Create new account"); //Subrayado
        line.setSpan(new UnderlineSpan(), 0, line.length(), 0);
        btnCreateCount.setText(line);

        pref= getSharedPreferences("datos", Context.MODE_PRIVATE);
        crud=new ManagerUser(this, "compras",1);

        if(crud.countUser()==0){
            crud.insertUser("1006598657","Laura","Campos","laura@gmail.com","admin","admin","administrador");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=txtUser.getText().toString();
                String password=txtPassword.getText().toString();

                if((user.equals("admin") && password.equals("admin"))){   //LOGIN DE VENDEDOR
                    if (crud.login(user,password,"administrador")==null){
                        Toast.makeText(getBaseContext(), "No existe el usuario",Toast.LENGTH_SHORT).show();
                    }else{
                        User usuario=crud.login(user,password,"administrador");
                        userSharePreferences(usuario,pref );
                        if(usuario.getUsuario().equals("admin") && usuario.getClave().equals("admin")){
                            Intent intento =new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intento);
                        }
                    }
                }else {        //LOGIN DE CLIENTE
                    if (crud.login(user, password,"cliente") == null) {
                        Toast.makeText(getBaseContext(), "No existe el cliente", Toast.LENGTH_SHORT).show();
                    } else {
                        User cli = crud.login(user, password,"cliente");
                        userSharePreferences(cli,pref );
                        Intent intento = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intento);
                    }
                }
                txtUser.setText("");
                txtPassword.setText("");
            }
        });

        btnCreateCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento =new Intent(MainActivity.this,RegisterClientActivity.class);
                startActivity(intento);
            }
        });
    }

    public static void userSharePreferences(User cli,SharedPreferences pre){
        SharedPreferences pref=pre;
        SharedPreferences.Editor editor = pref.edit();//Se va ha guardar las preferencias
        editor.putString("idUser", String.valueOf(cli.getIdUsuario()));
        editor.putString("cedulaUser", cli.getCedula());
        editor.putString("nombreUser", cli.getNombre());
        editor.putString("apellidoUser", cli.getApellido());
        editor.putString("correoUser", cli.getCorreo());
        editor.putString("usuarioUser", cli.getUsuario());
        editor.putString("claveUser", cli.getClave());
        editor.putString("categoriaUser", cli.getCategoria());
        editor.commit();
    }
}