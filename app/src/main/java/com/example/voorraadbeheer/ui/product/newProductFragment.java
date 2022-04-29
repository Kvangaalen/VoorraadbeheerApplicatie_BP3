package com.example.voorraadbeheer.ui.product;

import android.content.ContentValues;
import android.database.Cursor;
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

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.vooraadbeheer.R;

public class newProductFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    EditText ET_productnaam, ET_eannummer;
    String eannnummer;
    Button add_button;
    View root;
    public newProductFragment(String contents) {
        this.eannnummer = contents;
    }

    public newProductFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_new_product, container, false);
        ET_productnaam = root.findViewById(R.id.ED_productnaam);
        ET_eannummer = root.findViewById(R.id.ED_eannumer);
        add_button = root.findViewById(R.id.add_Product);
        Spinner spinner = root.findViewById(R.id.ED_groep);

        // spinner product groep
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.productgroep, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //  invullen van eannummer
        if ((this.eannnummer == null)) {
        } else {
            ET_eannummer.setText(this.eannnummer);
        }

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toevoegenproduct();
            }
        });

        return root;
    }

    private void toevoegenproduct() {
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();
        String productnaam = ET_productnaam.getText().toString();
        String eannnummer = ET_eannummer.getText().toString();
        Spinner spinner = (Spinner) root.findViewById(R.id.ED_groep);
        String eenheid = spinner.getSelectedItem().toString();

        // check of product al bestaat
        if (checkifProductExists(eannnummer)) {
            Toast.makeText(getContext(), "Product bestaat al", Toast.LENGTH_SHORT).show();
        } else{
            if(!productnaam.isEmpty() && !eannnummer.isEmpty()){
                // data base insert
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataContract.Producten.COLUMN_NAME_PRODUCTNAAM, productnaam);
                contentValues.put(DataContract.Producten.COLUMN_NAME_EANNUMER, eannnummer);
                contentValues.put(DataContract.Producten.COLUMN_NAME_PRODUCTGROEP, eenheid);
                long result = db.insert(DataContract.Producten.TABLE_Producten, null, contentValues);
                if (result == -1) {
                    Toast.makeText(getContext(), "Mislukt", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Product toegevoegd aan productlijst.", Toast.LENGTH_SHORT).show();
                    add_button.setEnabled(false);
                    Navigation.findNavController(root).navigate(R.id.action_nav_newproduct_to_nav_product);
                }
            } else{
                Toast.makeText(getContext(), "vul alle velden in.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // controleren of product al bestaat in database.
    private boolean checkifProductExists(String eannnummer) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbH.getWritableDatabase();
            String sql  = "SELECT * FROM " + DataContract.Producten.TABLE_Producten + " where eannnummer = '" +  eannnummer + "';";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    return true;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

}