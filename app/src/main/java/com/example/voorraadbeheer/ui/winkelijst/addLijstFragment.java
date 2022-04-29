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
import androidx.navigation.Navigation;

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.vooraadbeheer.R;

public class addLijstFragment extends Fragment {
    private Button Toevoegenlijst;
    private String lijstnaam;
    private EditText editText_lijstnaam;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_lijst, container, false);
        editText_lijstnaam = view.findViewById(R.id.ED_lijstnaam);
        Toevoegenlijst = view.findViewById(R.id.add_Lijst);

        Toevoegenlijst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View root) {
                addnewLijst();
            }
        });

        return view;
    }

    // maken van een nieuwe lijst.
    private void addnewLijst() {
        DatabaseHelper dbH = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbH.getWritableDatabase();
        lijstnaam = editText_lijstnaam.getText().toString();
        try {
            // controleren of de lijstnaam al bestaat en of het invoer veld niet leeg is.
            if (!Checkiflijstbestaat(lijstnaam) && !lijstnaam.isEmpty()) {
                    System.out.println("lijst bestaat nog niet");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataContract.Lijst.COLUMN_NAME_LIJSTNAAM, lijstnaam);
                    long result = db.insert(DataContract.Lijst.TABLE_Lijst, null, contentValues);
                    if (result == -1) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "gelukt", Toast.LENGTH_SHORT).show();
                        Toevoegenlijst.setEnabled(false);
                        Navigation.findNavController(view).navigate(R.id.action_nav_newlijst_to_nav_winkellijst);
                    }
            } else {
                if(lijstnaam.isEmpty()){
                    Toast.makeText(getContext(), "Vul een lijstnaam in.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "De lijst naam bestaat al", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e) {
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