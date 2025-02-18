package Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.Display;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import Entities.Producto;

public class ProductPopupWindow {
    private Context context;
    private PopupWindow popupWindow;
    private Producto currentProduct; // null para Creacion e inicializado para Actualizacion
    private OnProductActionListener listener;
    private boolean isProductAvailable;

    // Interface for callbacks
    public interface OnProductActionListener {
        void onProductAction(Producto product, ActionType actionType);
    }

    public enum ActionType {
        CREATE,
        UPDATE,
        DELETE
    }

    public ProductPopupWindow (Context context, Producto product, OnProductActionListener listener) {
        this.context = context;
        this.currentProduct = product;
        this.listener = listener;
        setupDialog();
    }

    private void setupDialog() {
        // Screen dimensions
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // Main container
        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setLayoutParams(new LinearLayout.LayoutParams(width - dpToPx(32), LinearLayout.LayoutParams.WRAP_CONTENT));
        mainContainer.setBackgroundColor(Color.WHITE);
        mainContainer.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Header
        TextView title = new TextView(context);
        title.setText(currentProduct == null ? "Crear Producto" : "Modificar Producto");
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, dpToPx(16));

        // Input fields
        EditText descriptionInput = createInput("Descripción", currentProduct != null ? currentProduct.getDescripcion() : "");
        EditText priceInput = createInput("Precio", currentProduct != null ? String.valueOf(currentProduct.getPrice()) : "");
        EditText stockInput = createInput("Stock", currentProduct != null ? String.valueOf(currentProduct.getStock()) : "");

        // Radio group for availability
        LinearLayout radioContainer = new LinearLayout(context);
        radioContainer.setOrientation(LinearLayout.HORIZONTAL);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton availableRadio = new RadioButton(context);
        availableRadio.setText("Disponible");
        availableRadio.setId(View.generateViewId());
        availableRadio.setTextSize(16);
        RadioButton unavailableRadio = new RadioButton(context);
        unavailableRadio.setText("No disponible");
        unavailableRadio.setId(View.generateViewId());
        unavailableRadio.setTextSize(16);

        // Establecer el estado inicial basado en el producto existente o valor por defecto
        if (currentProduct != null) {
            String status = currentProduct.getStatus();
            isProductAvailable = Boolean.parseBoolean(status); // Convierte el string a boolean
        } else {
            isProductAvailable = true; // Valor por defecto para nuevo producto
        }

        // Establecer la selección inicial
        if (isProductAvailable) {
            availableRadio.setChecked(true);
            unavailableRadio.setChecked(false);
        } else {
            availableRadio.setChecked(false);
            unavailableRadio.setChecked(true);
        }

        radioGroup.addView(availableRadio);
        radioGroup.addView(unavailableRadio);

        // Configurar el listener para cambios en la selección
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            isProductAvailable = (checkedId == availableRadio.getId());
        });
        radioContainer.addView(radioGroup);

       // Detectar cambios en la selección
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == availableRadio.getId()) {
                isProductAvailable = true;
            } else if (checkedId == unavailableRadio.getId()) {
                isProductAvailable = false;
            }
        });


        // Buttons container
        LinearLayout buttonContainer = new LinearLayout(context);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setPadding(0, dpToPx(16), 0, 0);

        // Create/Update button
        Button primaryButton = new Button(context);
        primaryButton.setText(currentProduct == null ? "Guardar" : "Actualizar");
        primaryButton.setBackgroundColor(Color.parseColor("#12BABD"));
        primaryButton.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams primaryButtonParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        primaryButton.setLayoutParams(primaryButtonParams);

        primaryButton.setOnClickListener(v -> {
            if (validateInputs(descriptionInput, priceInput, stockInput)) {
                Producto product;
                ActionType actionType;

                if (currentProduct == null) {
                    // Crear nuevo producto
                    product=new Producto();
                    product.setDescripcion(descriptionInput.getText().toString());
                    product.setStock(stockInput.getText().toString());
                    product.setStatus(String.valueOf( isProductAvailable));
                    product.setPrice(priceInput.getText().toString());
                    actionType = ActionType.CREATE;

                } else {
                    // Actualizar producto existente
                    currentProduct.setDescripcion(descriptionInput.getText().toString());
                    currentProduct.setStock(stockInput.getText().toString());
                    currentProduct.setStatus( (Integer.parseInt(stockInput.getText().toString())==0) ? "false" : String.valueOf( isProductAvailable) );
                    currentProduct.setPrice(priceInput.getText().toString());
                    product = currentProduct;
                    actionType = ActionType.UPDATE;
                }

                listener.onProductAction(product, actionType);
                dismiss();
            }
        });

        buttonContainer.addView(primaryButton);

        // Delete button
        if (currentProduct != null) {
            Button deleteButton = new Button(context);
            deleteButton.setText("Eliminar");
            deleteButton.setBackgroundColor(Color.RED);
            deleteButton.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            deleteButtonParams.setMarginStart(dpToPx(8));
            deleteButton.setLayoutParams(deleteButtonParams);

            deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmar eliminación")
                        .setMessage("¿Está seguro que desea eliminar este producto?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            listener.onProductAction(currentProduct, ActionType.DELETE);
                            dismiss();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });

            buttonContainer.addView(deleteButton);
        }

        // Add all views to main container
        mainContainer.addView(title);
        mainContainer.addView(descriptionInput);
        mainContainer.addView(priceInput);
        mainContainer.addView(stockInput);
        mainContainer.addView(radioContainer);
        mainContainer.addView(buttonContainer);

        popupWindow = new PopupWindow(
                mainContainer,
                width - dpToPx(32),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(dpToPx(4));
    }
    private boolean validateInputs(EditText description, EditText price, EditText stock) {
        String descriptionText = description.getText().toString().trim();
        String priceText = price.getText().toString().trim();
        String stockText = stock.getText().toString().trim();

        if (descriptionText.isEmpty()) {
            description.setError("La descripción es requerida");
            return false;
        }

        if (priceText.isEmpty()) {
            price.setError("El precio es requerido");
            return false;
        }
        if (!priceText.matches("^\\d+(\\.\\d+)?$")) {
            price.setError("Ingrese un precio válido (Ejemplo: 10 o 10.99)");
            return false;
        }

        if (stockText.isEmpty()) {
            stock.setError("El stock es requerido");
            return false;
        }
        if (!stockText.matches("^\\d+$")) {
            stock.setError("El stock debe ser un número entero");
            return false;
        }

        return true;
    }

    private EditText createInput(String hint, String initialValue) {
        EditText input = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60));
        params.setMargins(0, 0, 0, dpToPx(16));
        input.setLayoutParams(params);
        input.setHint(hint);
        input.setText(initialValue);
        input.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        input.setBackground(createEditTextBackground());
        return input;
    }

    private GradientDrawable createEditTextBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setStroke(dpToPx(1), Color.LTGRAY);
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    public void show(View anchorView) {
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
        dimBackground();
    }

    private void dimBackground() {
        View container = popupWindow.getContentView().getRootView();
        Context context = container.getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}