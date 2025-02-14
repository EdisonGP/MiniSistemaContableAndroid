package com.example.crud_app;

import static com.example.crud_app.MainActivity.userSharePreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import Entities.User;
import Manager.ManagerUser;


public class ClientInformationActivity extends AppCompatActivity {
    EditText txtNombre, txtApellido, txtCorreo, txtUsuario, txtClave;
    ManagerUser crud;
    Button btnActualizarCli, btnEliminarCli;
    String idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));

        // Layout principal
        ConstraintLayout mainLayout = new ConstraintLayout(this);
        mainLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));


        // Card para contener el formulario
        CardView cardView = new CardView(this);
        cardView.setId(View.generateViewId());
        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(40, 40, 40, 40);

        ConstraintLayout.LayoutParams cardParams = new ConstraintLayout.LayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));
        cardParams.setMargins(32, 32, 32, 32);
        cardView.setLayoutParams(cardParams);


        // Layout interno de la card
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));


        TextView title = new TextView(this);
        title.setText("Información de Cliente");
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#2C3E50"));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 60);
        cardContent.addView(title);

        //Obtener los parametros de SharedPreferences
        SharedPreferences pref= getSharedPreferences("datos", Context.MODE_PRIVATE);
        idCliente=pref.getString("idUser","");
        txtNombre = createEditText(pref.getString("nombreUser",""));
        txtApellido = createEditText(pref.getString("apellidoUser",""));
        txtCorreo = createEditText(pref.getString("correoUser",""));
        txtUsuario = createEditText(pref.getString("usuarioUser",""));
        txtClave = createEditText(pref.getString("claveUser",""));
        txtClave.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Agregar los campos al layout;
        cardContent.addView(txtNombre);
        cardContent.addView(txtApellido);
        cardContent.addView(txtCorreo);
        cardContent.addView(txtUsuario);
        cardContent.addView(txtClave);


        btnActualizarCli=createButton("Actualizar");
        btnEliminarCli=createButton("Eliminar");
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 60, 0, 0);
        btnActualizarCli.setLayoutParams(buttonParams);
        btnEliminarCli.setLayoutParams(buttonParams);
        cardContent.addView(btnActualizarCli);
        cardContent.addView(btnEliminarCli);

        cardView.addView(cardContent);
        mainLayout.addView(cardView);
        scrollView.addView(mainLayout);
        setContentView(scrollView);

        // Clase que administra el CRUD
        crud = new ManagerUser(this, "compras", 1);

        // Configurar listener del botón
        btnActualizarCli.setOnClickListener(v -> {
            if (validateFields()) {
                String nombre = txtNombre.getText().toString();
                String apellido = txtApellido.getText().toString();
                String correo = txtCorreo.getText().toString();
                String usuario = txtUsuario.getText().toString();
                String clave = txtClave.getText().toString();

                if (crud.updateUser(Integer.parseInt(idCliente),nombre,apellido,correo,usuario,clave)) {
                    User cli=crud.getUser(Integer.parseInt(idCliente));
                    userSharePreferences(cli,pref );
                    Intent intento =new Intent(ClientInformationActivity.this, MenuActivity.class);
                    startActivity(intento);
                    Toast.makeText(getBaseContext(), "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminarCli.setOnClickListener(v->{
            if (crud.deleteUser(Integer.parseInt(idCliente))) {
                Intent intento1 =new Intent(ClientInformationActivity.this, MainActivity.class);
                startActivity(intento1);
                Toast.makeText(getBaseContext(), "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Button createButton(String name){
        Button btn = new Button(this);
        btn.setText(name);
        btn.setTextSize(16);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setBackgroundColor((name.equals("Actualizar") ? Color.rgb(2,117,18): Color.rgb(229,29,5)));
        btn.setTextColor(Color.WHITE);
        btn.setPadding(32, 24, 32, 24);
        btn.setElevation(4);
        return btn;
    }
    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setText(hint);
        editText.setTextSize(16);
        editText.setPadding(32, 24, 32, 24);
        editText.setCompoundDrawablePadding(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 24, 0, 24);
        editText.setLayoutParams(params);

        return editText;
    }

    private boolean validateFields() {
        boolean isValid = true;
        EditText[] fields = {txtNombre, txtApellido, txtCorreo, txtUsuario, txtClave};

        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                field.setError("Este campo es requerido");
                isValid = false;
            }
        }

        // Validación básica de correo
        if (!txtCorreo.getText().toString().trim().isEmpty() &&
                !android.util.Patterns.EMAIL_ADDRESS.matcher(txtCorreo.getText().toString()).matches()) {
            txtCorreo.setError("Correo electrónico inválido");
            isValid = false;
        }

        return isValid;
    }
}
