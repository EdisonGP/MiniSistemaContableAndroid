package Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.view.View;
import android.widget.AdapterView;

public class SearchSpinnerHelper {

    private Context context;
    private LinearLayout containerLayout;
    private EditText searchEditText;
    private ImageView searchIcon;
    private AppCompatSpinner spinner;
    private OnFilterSelectedListener filterListener;
    private OnSearchTextChangedListener searchListener;

    public interface OnFilterSelectedListener { //Metodo para filtrar las opciones de spinner
        void onFilterSelected(String filter);
    }

    public interface OnSearchTextChangedListener {  //Metodo para buscar un producto
        void onSearchTextChanged(String text);
    }

    public SearchSpinnerHelper(Context context) {
        this.context = context;
    }

    public LinearLayout setupSearchAndFilter(ConstraintLayout parentLayout,
                                             String[] filterOptions,
                                             OnFilterSelectedListener filterListener,
                                             OnSearchTextChangedListener searchListener) {
        if (parentLayout == null) {
            Log.e("SearchSpinnerHelper", "parentLayout cannot be null");
            return null;
        }

        if (filterOptions == null || filterOptions.length == 0) {
            Log.e("SearchSpinnerHelper", "filterOptions cannot be null or empty");
            return null;
        }

        try {
            this.filterListener = filterListener;
            this.searchListener = searchListener;

            // Crear LinearLayout contenedor con ID único
            containerLayout = new LinearLayout(context);
            containerLayout.setId(View.generateViewId());

            // Configurar el contenedor
            setupContainerLayout(parentLayout);

            // Crear y agregar componentes
            createAndAddSearchField();
            createAndAddSpinner(filterOptions);

            return containerLayout;
        } catch (Exception e) {
            Log.e("SearchSpinnerHelper", "Error setting up search and filter: " + e.getMessage());
            return null;
        }
    }

    private void setupContainerLayout(ConstraintLayout parentLayout) {
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);
        containerLayout.setBackgroundDrawable(createContainerBackground());
        containerLayout.setElevation(8);
        containerLayout.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));

        ConstraintLayout.LayoutParams containerParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), 0);
        containerLayout.setLayoutParams(containerParams);

        parentLayout.addView(containerLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(containerLayout.getId(), ConstraintSet.TOP,
                parentLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(containerLayout.getId(), ConstraintSet.START,
                parentLayout.getId(), ConstraintSet.START);
        constraintSet.connect(containerLayout.getId(), ConstraintSet.END,
                parentLayout.getId(), ConstraintSet.END);
        constraintSet.applyTo(parentLayout);
    }
    private void createAndAddSearchField() {
        // Crear un LinearLayout para contener el ícono y el EditText
        LinearLayout searchContainer = new LinearLayout(context);
        searchContainer.setOrientation(LinearLayout.HORIZONTAL);
        searchContainer.setBackgroundDrawable(createSearchFieldBackground());
        searchContainer.setGravity(Gravity.CENTER_VERTICAL);
        searchContainer.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
        containerParams.setMarginEnd(dpToPx(8));
        searchContainer.setLayoutParams(containerParams);

        // Crear y agregar el ícono de búsqueda
        searchIcon = new ImageView(context);
        searchIcon.setId(View.generateViewId());
        searchIcon.setImageResource(android.R.drawable.ic_menu_search);
        searchIcon.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_IN);

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24));
        iconParams.setMarginEnd(dpToPx(8));
        searchIcon.setLayoutParams(iconParams);

        searchContainer.addView(searchIcon);

        // Crear y agregar el EditText
        searchEditText = new EditText(context);
        searchEditText.setId(View.generateViewId());
        searchEditText.setBackground(null);
        searchEditText.setHint("Buscar productos...");
        searchEditText.setTextColor(Color.parseColor("#212121"));
        searchEditText.setHintTextColor(Color.parseColor("#9E9E9E"));
        searchEditText.setTextSize(14);
        searchEditText.setMaxLines(1);
        searchEditText.setSingleLine(true);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        searchEditText.setLayoutParams(editTextParams);

        // Configurar el TextWatcher
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchListener != null) {
                    searchListener.onSearchTextChanged(s.toString());
                }
            }
        });

        searchContainer.addView(searchEditText);
        containerLayout.addView(searchContainer);
    }

    private void createAndAddSpinner(String[] filterOptions) {
        spinner = new AppCompatSpinner(context);
        spinner.setId(View.generateViewId());
        spinner.setBackgroundDrawable(createSpinnerBackground());

        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        spinner.setLayoutParams(spinnerParams);

        // Crear y configurar el adaptador
        if (filterOptions == null || filterOptions.length == 0) {
            throw new IllegalArgumentException("filterOptions no puede ser null o vacío");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, filterOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#424242"));
                textView.setTextSize(14);
                textView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#212121"));
                textView.setTextSize(14);
                textView.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
                textView.setBackgroundColor(position % 2 == 0 ?
                        Color.parseColor("#FFFFFF") : Color.parseColor("#F5F5F5"));
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Configurar el listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (filterListener != null && parent != null && parent.getItemAtPosition(position) != null) {
                        String selectedFilter = parent.getItemAtPosition(position).toString();
                        if (selectedFilter != null) {
                            filterListener.onFilterSelected(selectedFilter);
                        }
                    }
                } catch (Exception e) {
                    Log.e("SearchSpinnerHelper", "Error en onItemSelected: " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });


        containerLayout.addView(spinner);
    }








    private GradientDrawable createContainerBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(dpToPx(8));
        drawable.setStroke(dpToPx(1), Color.parseColor("#E0E0E0"));
        return drawable;
    }

    private GradientDrawable createSearchFieldBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#F5F5F5"));
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    private GradientDrawable createSpinnerBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#F5F5F5"));
        drawable.setCornerRadius(dpToPx(4));
        return drawable;
    }

    private int dpToPx(int dp) {
        if (context == null) {
            throw new IllegalStateException("Context no puede ser null");
        }

        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Getters para acceder a las vistas
    public EditText getSearchEditText() {
        return searchEditText;
    }

    public AppCompatSpinner getSpinner() {
        return spinner;
    }
}