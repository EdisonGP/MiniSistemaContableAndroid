package com.example.crud_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import Manager.ManagerUser;

public class RegisterClientActivity extends AppCompatActivity {
    EditText txtCedula, txtNombre, txtApellido, txtCorreo, txtUsuario, txtClave;
    ManagerUser crud;

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
        title.setText("Registro de Cliente");
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#2C3E50"));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 60);
        cardContent.addView(title);

        txtCedula = createEditText("Cédula");
        txtNombre = createEditText("Nombre");
        txtApellido = createEditText("Apellido");
        txtCorreo = createEditText("Correo");
        txtUsuario = createEditText("Usuario");
        txtClave = createEditText("Clave");
        txtClave.setInputType(InputType.TYPE_CLASS_TEXT | 
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Agregar los campos al layout
        cardContent.addView(txtCedula);
        cardContent.addView(txtNombre);
        cardContent.addView(txtApellido);
        cardContent.addView(txtCorreo);
        cardContent.addView(txtUsuario);
        cardContent.addView(txtClave);

        Button btnGuardar = new Button(this);
        btnGuardar.setText("GUARDAR");
        btnGuardar.setTextSize(16);
        btnGuardar.setTypeface(null, Typeface.BOLD);
        btnGuardar.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700));
        btnGuardar.setTextColor(Color.WHITE);
        btnGuardar.setPadding(32, 24, 32, 24);
        btnGuardar.setElevation(4);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 60, 0, 0);
        btnGuardar.setLayoutParams(buttonParams);
        cardContent.addView(btnGuardar);

        cardView.addView(cardContent);
        mainLayout.addView(cardView);
        scrollView.addView(mainLayout);
        setContentView(scrollView);

        // Clase que administra el CRUD
        crud = new ManagerUser(this, "compras", 1);

        // Configurar listener del botón
        btnGuardar.setOnClickListener(v -> {
            if (validateFields()) {
                String cedula = txtCedula.getText().toString();
                String nombre = txtNombre.getText().toString();
                String apellido = txtApellido.getText().toString();
                String correo = txtCorreo.getText().toString();
                String usuario = txtUsuario.getText().toString();
                String clave = txtClave.getText().toString();

                if (crud.insertUser(cedula, nombre, apellido, correo, usuario, clave,"cliente")) {
                    Toast.makeText(getBaseContext(), "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterClientActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
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
        EditText[] fields = {txtCedula, txtNombre, txtApellido, txtCorreo, txtUsuario, txtClave};

        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                field.setError("Este campo es requerido");
                isValid = false;
            }
        }

        // Validación para que la cédula sea solo numérica
        String cedula = txtCedula.getText().toString().trim();
        if (!cedula.isEmpty() && !cedula.matches("\\d+")) {
            txtCedula.setError("La cédula debe contener solo números");
            isValid = false;
        }

        // Validación básica de correo
        String correo = txtCorreo.getText().toString().trim();
        if (!correo.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Correo electrónico inválido");
            isValid = false;
        }

        return isValid;
    }

}