package com.example.voorraadbeheer.ui.winkelijst;

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
import com.example.voorraadbeheer.Model.Productlijst;
import com.example.vooraadbeheer.R;
import com.example.voorraadbeheer.Database.DataContract;


public class editProductToLijstFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Button bewerk;
    private Spinner spinnerEenheid;
    private EditText edhoeveelheid;
    private String eenheid,eannummer,lijstnaam;
    private int hoeveelheid;
    private String Eenheid;
    private View view;
    public editProductToLijstFragment(Productlijst obj){
        this.lijstnaam = obj.getLijstnaam();
        this.hoeveelheid = obj.getHoeveelheid();
        this.eenheid = obj.getEenheid();
        this.eannummer = obj.getEannummer();
    }
    public editProductToLijstFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bewerk_product_lijst, container, false);
        bewerk = view.findViewById(R.id.add_Lijst);
        edhoeveelheid = view.findViewById(R.id.ET_hoeveelheid);
        edhoeveelheid.setText(String.valueOf(new Integer(hoeveelheid)));
        spinnerEenheid = view.findViewById(R.id.speenheid);

        // spinner eenheid
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.eenheid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEenheid.setAdapter(adapter);
        spinnerEenheid.setOnItemSelectedListener(this);

        // instellen van standaard waarde.
        spinnerEenheid.setSelection(getIndex(spinnerEenheid, this.eenheid));

        // bewerk knop
        bewerk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdate();
            }
        });

        return view;
    }

    // opslaan van gegevens in productlijst table
    private void saveUpdate() {
        spinnerEenheid = (Spinner) view.findViewById(R.id.speenheid);
        Eenheid = spinnerEenheid.getSelectedItem().toString();
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();
        String inthoeveelheid = edhoeveelheid.getText().toString().trim();
        ContentValues cv = new ContentValues();
        cv.put(DataContract.Voorraadproducten.COLUMN_NAME_EANNUMER, this.eannummer);
        cv.put(DataContract.Productlijst.COLUMN_NAME_LIJSTNAAM, this.lijstnaam);
        cv.put(DataContract.Voorraadproducten.COLUMN_NAME_HOEVEELHEID, inthoeveelheid);
        cv.put(DataContract.Voorraadproducten.COLUMN_NAME_EENHEID, Eenheid);
        try {
            long result = db.update(DataContract.Productlijst.TABLE_Productlijst, cv, "eannnummer=?", new String[]{eannummer});
            if (result == -1) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            } else {
            Navigation.findNavController(this.view).navigate(R.id.action_nav_bewerkProductLijst_to_nav_winkellijst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // standaard waarde instellen van spinner
    private int getIndex(Spinner spinner, String productgroep) {
        for (int i=0; i< spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(productgroep)){
                return i;
            }
        }
        return 0;
    }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}