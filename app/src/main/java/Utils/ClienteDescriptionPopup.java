package Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import Entities.User;

public class ClienteDescriptionPopup {
    private Context context;
    private PopupWindow popupWindow;
    private User currentClient;

    public ClienteDescriptionPopup(Context context, User client) {
        this.context = context;
        this.currentClient = client;
        setupDialog();
    }

    private void setupDialog() {
        // Screen dimensions
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Main container
        LinearLayout mainContainer = new LinearLayout(context);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setLayoutParams(new LinearLayout.LayoutParams(
                width - dpToPx(32),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mainContainer.setBackgroundColor(Color.WHITE);
        mainContainer.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        // Crear el header con imagen de perfil
        LinearLayout headerContainer = new LinearLayout(context);
        headerContainer.setOrientation(LinearLayout.VERTICAL);
        headerContainer.setGravity(Gravity.CENTER);
        headerContainer.setPadding(0, 0, 0, dpToPx(24));

        // Círculo de perfil con iniciales
        TextView initialsView = new TextView(context);
        String initials = getInitials(currentClient.getNombre(), currentClient.getApellido());
        initialsView.setText(initials);
        initialsView.setTextSize(24);
        initialsView.setTypeface(Typeface.DEFAULT_BOLD);
        initialsView.setTextColor(Color.WHITE);
        initialsView.setGravity(Gravity.CENTER);

        // Configurar el fondo circular
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.parseColor("#1976D2"));
        initialsView.setBackground(circle);

        // Establecer tamaño del círculo
        int circleSize = dpToPx(80);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleSize, circleSize);
        initialsView.setLayoutParams(circleParams);
        headerContainer.addView(initialsView);

        // Nombre completo
        TextView fullNameView = new TextView(context);
        fullNameView.setText(currentClient.getNombre() + " " + currentClient.getApellido());
        fullNameView.setTextSize(22);
        fullNameView.setTypeface(Typeface.DEFAULT_BOLD);
        fullNameView.setGravity(Gravity.CENTER);
        fullNameView.setPadding(0, dpToPx(16), 0, dpToPx(4));
        headerContainer.addView(fullNameView);

        // Usuario
        TextView userView = new TextView(context);
        userView.setText("@" + currentClient.getUsuario());
        userView.setTextSize(16);
        userView.setTextColor(Color.GRAY);
        userView.setGravity(Gravity.CENTER);
        headerContainer.addView(userView);

        mainContainer.addView(headerContainer);

        // Separador
        View divider = new View(context);
        divider.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
        dividerParams.setMargins(0, dpToPx(8), 0, dpToPx(16));
        divider.setLayoutParams(dividerParams);
        mainContainer.addView(divider);

        // Información detallada
        addInfoField(mainContainer, "Cédula", currentClient.getCedula());
        addInfoField(mainContainer, "Correo Electrónico", currentClient.getCorreo());

        // Configurar PopupWindow
        popupWindow = new PopupWindow(
                mainContainer,
                width - dpToPx(32),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(dpToPx(8));
    }

    private void addInfoField(LinearLayout container, String label, String value) {
        LinearLayout fieldContainer = new LinearLayout(context);
        fieldContainer.setOrientation(LinearLayout.VERTICAL);
        fieldContainer.setPadding(0, dpToPx(8), 0, dpToPx(8));

        // Label
        TextView labelView = new TextView(context);
        labelView.setText(label);
        labelView.setTextSize(14);
        labelView.setTextColor(Color.GRAY);
        fieldContainer.addView(labelView);

        // Value
        TextView valueView = new TextView(context);
        valueView.setText(value);
        valueView.setTextSize(16);
        valueView.setTypeface(Typeface.DEFAULT_BOLD);
        valueView.setPadding(0, dpToPx(4), 0, 0);
        fieldContainer.addView(valueView);

        container.addView(fieldContainer);
    }

    private String getInitials(String nombre, String apellido) {
        StringBuilder initials = new StringBuilder();
        if (nombre != null && nombre.length() > 0) {
            initials.append(nombre.charAt(0));
        }
        if (apellido != null && apellido.length() > 0) {
            initials.append(apellido.charAt(0));
        }
        return initials.toString().toUpperCase();
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