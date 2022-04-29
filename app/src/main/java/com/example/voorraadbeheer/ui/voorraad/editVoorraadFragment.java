package com.example.voorraadbeheer.ui.voorraad;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Voorraadproduct;
import com.example.vooraadbeheer.R;

import static android.content.ContentValues.TAG;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.COLUMN_NAME_HOEVEELHEID;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.COLUMN_NAME_EANNUMER;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.COLUMN_NAME_EENHEID;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.COLUMN_NAME_HOUDBAARHEIDDATUM;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.TABLE_Voorraadprocuten;

public class editVoorraadFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText ET_eannummer, ET_hoeveelheid;
    private int hoeveelheid;
    private String eannummer, houdbaarheid, strHoeveelheid;
    private String datum;

    private Button update_voorraad;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String eenheid;
    private View root;
    private Spinner spinner;

    // standaard methode
    public editVoorraadFragment() {
    }

    public editVoorraadFragment(Voorraadproduct obj) {
        this.eannummer = obj.getEannnummer();
        this.hoeveelheid = obj.getHoeveelheid();
        this.eenheid = obj.getEenheid();
        this.houdbaarheid = obj.getHoudbaarheiddatum();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bewerk_voorraad, container, false);
        ET_eannummer = root.findViewById(R.id.ET_eannummer);
        ET_hoeveelheid = root.findViewById(R.id.ET_hoeveelheid);
        update_voorraad = root.findViewById(R.id.update_voorraad);
        mDisplayDate = (TextView) root.findViewById(R.id.tvDate);
        spinner = (Spinner) root.findViewById(R.id.ET_eenheid);


        // spinner eenheid
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.eenheid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // standaard waarde
        ET_eannummer.setText(eannummer);
        mDisplayDate.setText(this.houdbaarheid);
        ET_hoeveelheid.setText(String.valueOf(new Integer(hoeveelheid)));
        spinner.setSelection(getIndex(spinner, this.eenheid));

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "-" + day + "-" + year;
                mDisplayDate.setText(date);
            }
        };

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDateEdit();
            }
        });

        update_voorraad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVoorraad();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "-" + day + "-" + year;
                mDisplayDate.setText(date);
            }
        };
        return root;
    }

    // dialog om datum in te stellen
    private void openDialogDateEdit() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // standaard waarde instellen van spinner
    private int getIndex(Spinner spinner, String productgroep) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(productgroep)) {
                return i;
            }
        }
        return 0;
    }

    // update van gegevens in database
    private void updateVoorraad() {
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();
        try {
            datum = mDisplayDate.getText().toString();
            String hoeveelheidST = ET_hoeveelheid.getText().toString().trim();
            strHoeveelheid = String.valueOf(new Integer(hoeveelheidST));
            eenheid = spinner.getSelectedItem().toString();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_EANNUMER, this.eannummer);
            cv.put(COLUMN_NAME_HOEVEELHEID, strHoeveelheid);
            cv.put(COLUMN_NAME_EENHEID, eenheid);
            cv.put(COLUMN_NAME_HOUDBAARHEIDDATUM, datum);
            long result = db.update(TABLE_Voorraadprocuten, cv, "eannnummer=?", new String[]{this.eannummer});
            if (result == -1) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Navigation.findNavController(this.root).navigate(R.id.action_nav_bewerkvoorraad_to_nav_overzichtvoorraad2);
            }
        } catch (NumberFormatException NFE) {
            Toast.makeText(getContext(), "Vul alle invoervelden in", Toast.LENGTH_SHORT).show();
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