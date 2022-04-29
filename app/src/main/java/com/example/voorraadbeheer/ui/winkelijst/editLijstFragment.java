package com.example.voorraadbeheer.ui.winkelijst;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Lijst;
import com.example.vooraadbeheer.R;
import com.example.voorraadbeheer.Database.DataContract;

public class editLijstFragment extends Fragment {
    private String lijstnaam;
    private EditText etLijstnaam;
    private Button btnEditlijst;

    public editLijstFragment(Lijst obj) {
        this.lijstnaam = obj.getLijstnaam();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bewerken_lijst, container, false);
        etLijstnaam = view.findViewById(R.id.ED_lijstnaam);
        btnEditlijst = view.findViewById(R.id.edit_lijst);
        etLijstnaam.setText(lijstnaam);

        // knop opslaan
        btnEditlijst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdate();
            }
        });

        return view;
    }

    // bewerken van de lijstnaam
    private void saveUpdate() {
        String newlijstnaam = etLijstnaam.getText().toString().trim();
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DataContract.Lijst.COLUMN_NAME_LIJSTNAAM, newlijstnaam);
        try {
            // controleren of lijstnaam niet leeg is.
            if (!Checkiflijstbestaat(lijstnaam) && !lijstnaam.isEmpty()) {
                // opslaan in productlijst table
                long result = db.update(DataContract.Productlijst.TABLE_Productlijst, cv, "lijstnaam=?", new String[]{this.lijstnaam});
                long result1 = db.update(DataContract.Lijst.TABLE_Lijst, cv, "lijstnaam=?", new String[]{this.lijstnaam});
                    if (result1 == -1 && result == -1) {
                        Toast.makeText(getContext(), "Failed2", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lijstnaam is aangepast", Toast.LENGTH_SHORT).show();
                    }
                }
            else {
                if(lijstnaam.isEmpty()){
                    Toast.makeText(getContext(), "Vul een lijstnaam in.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "De lijst naam bestaat al", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    // functie om te controleren of de lijst bestaat.
    private boolean Checkiflijstbestaat(String lijstnaam) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(getActivity());
            SQLiteDatabase db = dbH.getWritableDatabase();
            String sql  = "SELECT * FROM " + DataContract.Lijst.TABLE_Lijst + " where lijstnaam= '" +  lijstnaam + "';";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    return true;
                } while (cursor.moveToNext());
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}