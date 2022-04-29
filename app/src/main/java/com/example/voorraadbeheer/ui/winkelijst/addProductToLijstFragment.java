package com.example.voorraadbeheer.ui.winkelijst;

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

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Product;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;

import static com.example.voorraadbeheer.Database.DataContract.Producten.TABLE_Producten;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.COLUMN_NAME_EANNUMER;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.COLUMN_NAME_LIJSTNAAM;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.TABLE_Productlijst;

public class addProductToLijstFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button add_Product;
    private Spinner spinnerProduct, spinnerEenheid;
    private EditText edhoeveelheid, eenheid;
    int hoeveelheid;
    String lijstnaam;
    private View view;

    public addProductToLijstFragment(String lijstnaam) {
        this.lijstnaam = lijstnaam;
    }
    public addProductToLijstFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_toevoegen_lijst, container, false);
        add_Product = view.findViewById(R.id.add_Lijst);
        spinnerProduct = view.findViewById(R.id.etproduct);
        edhoeveelheid = view.findViewById(R.id.ET_hoeveelheid);
        spinnerEenheid = view.findViewById(R.id.ET_eenheid);
        // spinner productnaam
        loadSpinnerProducten();

        // spinner eenheid
        loadSpinnerEenheid();

        //knop toevoegen product
        add_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toevoegenvanproductaanlijst();
            }
        });

        return view;
    }

    private void toevoegenvanproductaanlijst() {
        try {
            Product productnaam = (Product) spinnerProduct.getSelectedItem();
            System.out.println(productnaam.getEannnummer());
            hoeveelheid = Integer.parseInt(edhoeveelheid.getText().toString());
            spinnerEenheid = (Spinner) view.findViewById(R.id.ET_eenheid);
            String eenheid = spinnerEenheid.getSelectedItem().toString();
            // spinner
            if (checkIfProductOnList(productnaam.getEannnummer())){
                Toast.makeText(getContext(), "Dit product staat op de lijst", Toast.LENGTH_SHORT).show();
            } else{
                DatabaseHelper dbH = new DatabaseHelper(getContext());
                SQLiteDatabase db = dbH.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME_EANNUMER, productnaam.getEannnummer());
                contentValues.put(DataContract.Productlijst.COLUMN_NAME_EENHEID, eenheid);
                contentValues.put(DataContract.Productlijst.COLUMN_NAME_HOEVEELHEID, hoeveelheid);
                contentValues.put(DataContract.Productlijst.COLUMN_NAME_LIJSTNAAM, lijstnaam);
                long result = db.insert(TABLE_Productlijst, null, contentValues);
                if (result == -1) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "gelukt", Toast.LENGTH_SHORT).show();
                    add_Product.setEnabled(false);
                    Toast.makeText(getContext(), productnaam.getProductnaam() + eenheid + hoeveelheid + lijstnaam, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException NFE) {
            Toast.makeText(getContext(), "Veleden zijn leeg", Toast.LENGTH_SHORT).show();
        }
    }

    // Controleren of product in productlijst.
    private boolean checkIfProductOnList(String eannnummer) {
        String[] columns = {COLUMN_NAME_EANNUMER, COLUMN_NAME_LIJSTNAAM};
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getReadableDatabase();
        String selection = COLUMN_NAME_EANNUMER + " = ?" + " AND " + COLUMN_NAME_LIJSTNAAM + " = ?";
        String[] selectionArgs = {eannnummer,  lijstnaam };
        Cursor cursor = db.query(TABLE_Productlijst,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    // ophalen van alle producten in table product voor spinnerProduct
    public ArrayList<Product> getAllProducts() {
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getReadableDatabase();
        ArrayList<Product> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_Producten;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String producteannummer = cursor.getString(cursor.getColumnIndex("eannnummer"));
                    String productnnaam = cursor.getString(cursor.getColumnIndex("productnaam"));
                    String productgroep = cursor.getString(cursor.getColumnIndex("productgroep"));
                    Product p = new Product(producteannummer, productnnaam, productgroep);
                    list.add(p);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return list;
    }

    // spinner proudcten
    private void loadSpinnerProducten() {
        ArrayList<Product> p = (ArrayList<Product>) getAllProducts();
        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(getContext(), android.R.layout.simple_spinner_dropdown_item, p);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(adapter);
    }

    // spinner eenheid
    private void loadSpinnerEenheid() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.eenheid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEenheid.setAdapter(adapter);
        spinnerEenheid.setOnItemSelectedListener(this);
    }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

}