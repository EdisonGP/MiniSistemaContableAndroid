package com.example.crud_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PagoActivity extends AppCompatActivity {

    private TextView infoCliente,cedulaCliente, totalFactura;
    private EditText inputNumeroTarjeta, inputFechaExpiracion, inputCVV;
    private Button botonPagar;
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        // Vincular vistas
        infoCliente = findViewById(R.id.infoCliente);
        cedulaCliente = findViewById(R.id.cedulaCliente);
        totalFactura = findViewById(R.id.totalFactura);
        inputNumeroTarjeta = findViewById(R.id.inputNumeroTarjeta);
        inputFechaExpiracion = findViewById(R.id.inputFechaExpiracion);
        inputCVV = findViewById(R.id.inputCVV);
        botonPagar = findViewById(R.id.botonPagar);

        // Obtener datos del Intent
        Intent intent = getIntent();
        idCliente = Integer.parseInt(intent.getStringExtra("clientId"));
        infoCliente.setText("Cliente: "+intent.getStringExtra("clientName") + " " + intent.getStringExtra("clientApellido"));
        cedulaCliente.setText("Cedula: "+intent.getStringExtra("clientCedula"));
        totalFactura.setText("Total $: "+String.format("$%.2f", intent.getDoubleExtra("totalAmount", 0.0)));

        // Configurar el botón de pago
        botonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Con estos datos se puede crear una tabla en sqlite para almacenarlo.
                String numeroTarjeta = inputNumeroTarjeta.getText().toString().trim();
                String fechaExpiracion = inputFechaExpiracion.getText().toString().trim();
                String cvv = inputCVV.getText().toString().trim();

                // Validar campos
                if (numeroTarjeta.isEmpty() || fechaExpiracion.isEmpty() || cvv.isEmpty()) {
                    Toast.makeText(PagoActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();

                } else {
                    Compra_FacturacionActivity.saveFacturaPedido(getBaseContext(),idCliente, "pedido", "Pedido guardado");
                    Intent intento = new Intent(getBaseContext(), AdminShowPedidoFacturaActivity.class);
                    startActivity(intento);
                }
            }
        });
    }
}