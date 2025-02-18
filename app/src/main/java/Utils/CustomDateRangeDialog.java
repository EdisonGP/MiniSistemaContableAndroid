package Utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class CustomDateRangeDialog {
    private final Context context;
    private AlertDialog dialog;
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private OnDateRangeSelectedListener listener;

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(String fechaInicio, String fechaFin);
    }

    public CustomDateRangeDialog(Context context) {
        this.context = context;
    }

    public void show(OnDateRangeSelectedListener listener) {
        this.listener = listener;
        createAndShowDialog();
    }

    private void createAndShowDialog() {
        // Crear el contenedor principal
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dpToPx(24), dpToPx(16), dpToPx(24), dpToPx(16));
        mainLayout.setBackgroundColor(Color.WHITE);

        // Crear y configurar los campos de fecha
        etFechaInicio = createDateInput("Fecha inicio");
        etFechaFin = createDateInput("Fecha fin");

        // Añadir campos al layout principal
        mainLayout.addView(etFechaInicio);
        mainLayout.addView(createSpacer(16));
        mainLayout.addView(etFechaFin);
        mainLayout.addView(createSpacer(24));

        // Crear contenedor para botones
        LinearLayout buttonContainer = new LinearLayout(context);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.END);

        // Crear botones
        Button btnCancelar = createButton("Cancelar", false);
        Button btnBuscar = createButton("Buscar", true);

        // Añadir botones al contenedor
        buttonContainer.addView(btnCancelar);
        buttonContainer.addView(createHorizontalSpacer(8));
        buttonContainer.addView(btnBuscar);
        mainLayout.addView(buttonContainer);

        // Configurar eventos de click para los campos de fecha
        etFechaInicio.setOnClickListener(v -> showDatePicker(etFechaInicio));
        etFechaFin.setOnClickListener(v -> showDatePicker(etFechaFin));

        // Configurar eventos de click para los botones
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnBuscar.setOnClickListener(v -> validateAndReturn());

        // Crear y mostrar el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Filtrar por Fecha");
        builder.setView(mainLayout);

        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(createDialogBackground());
        }
        dialog.show();
    }

    private EditText createDateInput(String hint) {
        EditText editText = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(56));

        editText.setLayoutParams(params);
        editText.setHint(hint);
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        editText.setBackground(createInputBackground());
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return editText;
    }

    private Button createButton(String text, boolean isPrimary) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(40));

        button.setLayoutParams(params);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        button.setPadding(dpToPx(16), 0, dpToPx(16), 0);

        if (isPrimary) {
            button.setBackground(createPrimaryButtonBackground());
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackground(createSecondaryButtonBackground());
            button.setTextColor(Color.parseColor("#1976D2"));
        }

        return button;
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", dayOfMonth, (month + 1), year);
                    editText.setText(fechaSeleccionada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void validateAndReturn() {
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFin = etFechaFin.getText().toString();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(context, "Por favor seleccione ambas fechas",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.onDateRangeSelected(fechaInicio, fechaFin);
        }
        dialog.dismiss();
    }

    private View createSpacer(int heightDp) {
        View spacer = new View(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(heightDp)));
        return spacer;
    }

    private View createHorizontalSpacer(int widthDp) {
        View spacer = new View(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(widthDp), LinearLayout.LayoutParams.MATCH_PARENT));
        return spacer;
    }

    private GradientDrawable createInputBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setStroke(dpToPx(1), Color.parseColor("#E0E0E0"));
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    private GradientDrawable createPrimaryButtonBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#1976D2"));
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    private GradientDrawable createSecondaryButtonBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    private GradientDrawable createDialogBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(dpToPx(8));
        return drawable;
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
