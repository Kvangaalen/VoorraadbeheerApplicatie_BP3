package com.example.voorraadbeheer.ui.product;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Product;
import com.example.vooraadbeheer.R;

import static com.example.voorraadbeheer.Database.DataContract.Producten.COLUMN_NAME_PRODUCTGROEP;
import static com.example.voorraadbeheer.Database.DataContract.Producten.COLUMN_NAME_PRODUCTNAAM;
import static com.example.voorraadbeheer.Database.DataContract.Producten.TABLE_Producten;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.COLUMN_NAME_EANNUMER;

public class editProductFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private View root;
    EditText ETproductnaam, ETeannnummer;
    String productnaam, neweannummer, productgroep, eannummer;
    Button updateButton;
    Spinner productspinner;
    public editProductFragment() { }

    public editProductFragment(Product m) {
        this.productnaam = m.getProductnaam();
        this.productgroep = m.getProductgroep();
        this.eannummer = m.getEannnummer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_updateproduct, container, false);
        ETproductnaam = root.findViewById(R.id.ET_productnaam);
        ETeannnummer = root.findViewById(R.id.ET_eannummer);
        updateButton = root.findViewById(R.id.update_button);
        productspinner = root.findViewById(R.id.ET_productgroep);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.productgroep, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productspinner.setAdapter(adapter);
        productspinner.setOnItemSelectedListener(this);

        // invul velden standaard invullen
        ETproductnaam.setText(this.productnaam);
        ETeannnummer.setText(this.eannummer);
        productspinner.setSelection(getIndex(productspinner, this.productgroep));

        // update knop
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdate();
            }
        });

        return root;
    }

    // standaard waarde instellen
    private int getIndex(Spinner spinner, String productgroep) {
        for (int i=0; i< spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(productgroep)){
                return i;
            }
        }
        return 0;
    }

    // functie voor het opslaan van de bewerking
    private void saveUpdate() {
        productnaam = ETproductnaam.getText().toString().trim();
        neweannummer = ETeannnummer.getText().toString().trim();
        Spinner spinner = (Spinner) root.findViewById(R.id.ET_productgroep);
        productgroep = spinner.getSelectedItem().toString();
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_PRODUCTNAAM, productnaam);
        cv.put(COLUMN_NAME_EANNUMER, neweannummer);
        cv.put(COLUMN_NAME_PRODUCTGROEP, productgroep);
        try {
            long result = db.update(TABLE_Producten, cv, "eannnummer=?", new String[]{eannummer});
            if (result == -1) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Navigation.findNavController(this.root).navigate(R.id.action_edit_product_to_nav_product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }
    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}