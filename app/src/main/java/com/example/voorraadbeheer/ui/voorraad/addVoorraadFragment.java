package com.example.voorraadbeheer.ui.voorraad;


import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
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

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.vooraadbeheer.R;

import static android.content.ContentValues.TAG;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.COLUMN_NAME_EANNUMER;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.COLUMN_NAME_HOUDBAARHEIDDATUM;
import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.TABLE_Voorraadprocuten;

public class addVoorraadFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View root;
    private String barcode, eenheid, datum;
    private EditText barcodetext, ET_hoeveelheid;
    private Button buttonSave;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean bestaatAl;

    public addVoorraadFragment(String barcode) {
        this.barcode = barcode;
    }
    // standaard methode
    public addVoorraadFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStatee) {
        root = inflater.inflate(R.layout.fragment_scan, container, false);
        mDisplayDate = (TextView) root.findViewById(R.id.tvDate);
        barcodetext = root.findViewById(R.id.ET_eannummer);
        ET_hoeveelheid = root.findViewById(R.id.ET_hoeveelheid);
        buttonSave = root.findViewById(R.id.add_Product);
        Spinner spinner = root.findViewById(R.id.ET_eenheid);

        // spinner eenheid
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.eenheid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // standaard waarde
        barcodetext.setText(this.barcode);

        // datum klik
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDateEdit();
            }
        });

        // opslaan knop
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVoorraad();
            }
        });

        // als datum in ingesteld is
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

    // toevoegen van product aan voorraad.
    private void saveVoorraad() {
        barcode = barcodetext.getText().toString().trim();
        datum = mDisplayDate.getText().toString();
        // controlleren of het product met ingevulde datum al op voorraad is.
        if (datumEnEannummerBestaat(barcode, datum)) {
            Toast.makeText(getContext(), "Product bestaat al", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int hoeveelheid = Integer.parseInt(ET_hoeveelheid.getText().toString());
                Spinner spinner = (Spinner) root.findViewById(R.id.ET_eenheid);
                eenheid = spinner.getSelectedItem().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataContract.Voorraadproducten.COLUMN_NAME_EENHEID, eenheid);
                contentValues.put(COLUMN_NAME_EANNUMER, barcode);
                contentValues.put(COLUMN_NAME_HOUDBAARHEIDDATUM, datum);
                contentValues.put(DataContract.Voorraadproducten.COLUMN_NAME_HOEVEELHEID, hoeveelheid);
                DatabaseHelper dbH = new DatabaseHelper(getContext());
                dbH.close();
                SQLiteDatabase db = dbH.getWritableDatabase();
                long result = db.insert(TABLE_Voorraadprocuten, null, contentValues);
                if (result == -1) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    buttonSave.setEnabled(false);
                }
                db.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    // controleren of product met datum op voorraad is.
    private boolean datumEnEannummerBestaat(String barcode, String datum) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(this.getContext());
            SQLiteDatabase db = dbH.getWritableDatabase();
            String sql  = "SELECT * FROM " + DataContract.Voorraadproducten.TABLE_Voorraadprocuten + " where eannnummer = '" +  barcode + "' and houdbaarheiddatum = '" + datum + "';";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    bestaatAl = true;
                    String houdbaarheiddatum = cursor.getString(cursor.getColumnIndex("houdbaarheiddatum"));
                    System.out.println(houdbaarheiddatum);
                } while (cursor.moveToNext());
            } else {
                bestaatAl = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bestaatAl;
    }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    // nodig voor spinner "spinner.setOnItemSelectedListener(this);"
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

}