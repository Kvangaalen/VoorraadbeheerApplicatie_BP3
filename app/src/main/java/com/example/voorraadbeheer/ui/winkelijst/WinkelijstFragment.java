package com.example.voorraadbeheer.ui.winkelijst;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Lijst;
import com.example.vooraadbeheer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.voorraadbeheer.Database.DataContract.Lijst.TABLE_Lijst;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.TABLE_Productlijst;

public class WinkelijstFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterLijst adapter;
    private FloatingActionButton FloatingActionButton;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_winkellijst, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterLijst(getContext(), getLijst(this));
        recyclerView.setAdapter(adapter);
        FloatingActionButton = root.findViewById(R.id.add_button);

        FloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View root) {
                Navigation.findNavController(root).navigate(R.id.action_nav_winkellijst_to_nav_newlijst);
            }
        });

        adapter.setOnItemClickListener(new AdapterLijst.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Lijst obj, int item) {
                toonProductenvanLijst(obj, root);
            }
        });

        // optie knop....
        adapter.setOnMoreButtonClickListener(new AdapterLijst.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, Lijst obj, MenuItem item) {
                if (item.getTitle().equals("Verwijderen")) {
                    deleteLijst(obj, root);
                } else if (item.getTitle().equals("Bewerken")) {
                    bewerkLijst(obj, view);

                }
            }
        });

        return root;
    }

    // verwijderen van Winkelijst met bijbehorden producten op de lijst.
    private void deleteLijst(Lijst obj, View root) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String eannummer = obj.getLijstnaam();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Verwijderen bevestigen");
            builder.setMessage("Weet je het zeker?");
            // verwijder button
            builder.setPositiveButton("Verwijderen", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        long result = db.delete(TABLE_Lijst, "lijstnaam=?", new String[]{eannummer});
                        long result1 = db.delete(TABLE_Productlijst, "lijstnaam=?", new String[]{eannummer});
                        if (result == -1 && result1 == -1) {
                            Toast.makeText(getContext(), "Failed to Delete.", Toast.LENGTH_SHORT).show();
                        } else {
                            Navigation.findNavController(root).navigate(R.id.action_nav_winkellijst_self);
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR" + e);
                    } finally {
                        db.close();
                        dialog.dismiss();
                    }
                }
            });
            // annuleer button
            builder.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (NullPointerException nullPointerException) {
            System.out.println("ERROR");
        }
    }

    // producten toonen van de lijst
    private void toonProductenvanLijst(Lijst obj, View root) {
        overviewProductLijstFragment fragment2 = new overviewProductLijstFragment(obj);
        Navigation.findNavController(root).navigate(R.id.action_nav_winkellijst_to_nav_productlijst);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }

    // bewerken van een lijst
    private void bewerkLijst(Lijst obj, View root) {
        editLijstFragment fragment2 = new editLijstFragment(obj);
        Navigation.findNavController(root).navigate(R.id.action_nav_winkellijst_to_nav_editlijst);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }

    // ophalen van alle lijsten
    private List<Lijst> getLijst(WinkelijstFragment winkelijstFragment) {
        List<Lijst> Lijsten = new ArrayList<>();
        DatabaseHelper dbH = new DatabaseHelper(this.getContext());
        SQLiteDatabase db = dbH.getReadableDatabase();
        try {
            String sql = "select * from " + TABLE_Lijst;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String lijstnaam = cursor.getString(cursor.getColumnIndex("lijstnaam"));
                    Lijst l = new Lijst(lijstnaam);
                    Lijsten.add(l);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            System.out.println("ERROR" + e);
        } finally {
            db.close();
        }
        return Lijsten;
    }
}