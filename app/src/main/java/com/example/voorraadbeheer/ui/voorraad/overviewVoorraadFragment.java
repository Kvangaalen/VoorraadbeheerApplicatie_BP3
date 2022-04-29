package com.example.voorraadbeheer.ui.voorraad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Voorraadproduct;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.voorraadbeheer.Database.DataContract.Voorraadproducten.TABLE_Voorraadprocuten;

public class overviewVoorraadFragment extends Fragment {
    private static Context context;
    View root;
    private RecyclerView recyclerView;
    private AdapterVoorraadProducts adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_overzicht_voorraad, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterVoorraadProducts(getContext(), getVoorraad(this));
        recyclerView.setAdapter(adapter);

        // klik op menuitems
        adapter.setOnMoreButtonClickListener(new AdapterVoorraadProducts.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, Voorraadproduct obj, MenuItem item) {
                if (item.getTitle().equals("Verwijderen")) {
                    deleteVoorraad(obj);
                } else if (item.getTitle().equals("Bewerken")) {
                    editVoorraad(obj);
                }
            }
        });

        return root;
    }

    // Scherm voor het bewerken van de voorraad.
    private void editVoorraad(Voorraadproduct obj) {
        editVoorraadFragment fragment2 = new editVoorraadFragment(obj);
        Navigation.findNavController(root).navigate(R.id.overzicht_to_bewerkvoorraad);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }

    // Verwijderen van product van voorraadlijst.
    private void deleteVoorraad(Voorraadproduct obj) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String eannummer = obj.getEannnummer();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Verwijderen bevestigen");
            builder.setMessage("Weet je het zeker?");
            // verwijder button
            builder.setPositiveButton("Verwijderen", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        long result = db.delete(TABLE_Voorraadprocuten, "eannnummer=?", new String[]{eannummer});
                        if (result == -1) {
                            Toast.makeText(getContext(), "Failed to Delete.", Toast.LENGTH_SHORT).show();
                        } else {
                            Navigation.findNavController(root).navigate(R.id.action_nav_product_self);
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

    // ophallen van de voorraad
    public List<Voorraadproduct> getVoorraad(overviewVoorraadFragment overviewVoorraadFragment) {
        List<Voorraadproduct> VoorpraadPrducten = new ArrayList<>();
        DatabaseHelper dbH = new DatabaseHelper(this.getContext());
        SQLiteDatabase db = dbH.getReadableDatabase();
        try {
            String sql = "select * from " + TABLE_Voorraadprocuten;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String eannnummer = cursor.getString(cursor.getColumnIndex("eannnummer"));
                    String houdbaarheiddatum = cursor.getString(cursor.getColumnIndex("houdbaarheiddatum"));
                    int hoeveelheid = cursor.getInt(cursor.getColumnIndex("hoeveelheid"));
                    String eenheid = cursor.getString(cursor.getColumnIndex("eenheid"));
                    Voorraadproduct v = new Voorraadproduct(eannnummer, houdbaarheiddatum, hoeveelheid, eenheid);
                    VoorpraadPrducten.add(v);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            System.out.println("ERROR" + e);
        } finally {
            db.close();
        }
        return VoorpraadPrducten;
    }

}