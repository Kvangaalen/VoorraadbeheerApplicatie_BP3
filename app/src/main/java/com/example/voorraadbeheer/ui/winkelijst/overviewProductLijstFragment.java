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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Lijst;
import com.example.voorraadbeheer.Model.Productlijst;
import com.example.vooraadbeheer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.voorraadbeheer.Database.DataContract.Productlijst.COLUMN_NAME_LIJSTNAAM;
import static com.example.voorraadbeheer.Database.DataContract.Productlijst.TABLE_Productlijst;

public class overviewProductLijstFragment extends Fragment {
    private String lijstnaam;
    private FloatingActionButton FloatingActionButton;
    private RecyclerView recyclerView;
    private AdapterLijstproducten adapter;
    View root;
    public overviewProductLijstFragment(Lijst lijst) {
        this.lijstnaam = lijst.getLijstnaam();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_lijst_product, container, false);
        FloatingActionButton = root.findViewById(R.id.add_button);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterLijstproducten(getContext(), getLijstproducten(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnMoreButtonClickListener(new AdapterLijstproducten.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, Productlijst obj, MenuItem item) {
                if (item.getTitle().equals("Verwijderen")) {
                    verwijderProductWinkelijst(obj);
                } else if (item.getTitle().equals("Bewerken")) {
                    BewerkProductWinkellijst(obj);
                }
            }
        });

        // FloatingActionButton toevoegen product aan lijst
        FloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toevoegenProductAanLijst();
            }
        });

        return root;
    }

    // toevoegen van een product aan de lijst
    private void toevoegenProductAanLijst() {
        addProductToLijstFragment fragment2 = new addProductToLijstFragment(lijstnaam);
        Navigation.findNavController(root).navigate(R.id.action_nav_productlijst_to_nav_producttoevoegen);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }

    // Bewerken van een product van een lijst.
    private void BewerkProductWinkellijst(Productlijst obj) {
        editProductToLijstFragment fragment2 = new editProductToLijstFragment(obj);
        Navigation.findNavController(root).navigate(R.id.action_nav_productlijst_to_nav_producttoevoegen2);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }

    // verwijderen van een product van de lijst.
    private void verwijderProductWinkelijst(Productlijst obj) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String lijstnaam = obj.getLijstnaam();
            String eannummer = obj.getEannummer();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Verwijderen bevestigen");
            builder.setMessage("Weet je het zeker?");
            // verwijder button
            builder.setPositiveButton("Verwijderen", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                       String sql = "delete from "+TABLE_Productlijst+" where eannnummer = '" + eannummer + "' and lijstnaam = '"+ lijstnaam + "'";
                        db.execSQL(sql);
                       Toast.makeText(getContext(),"Product verwijderd", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(root).navigate(R.id.action_nav_productlijst_self);
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

    // ophalen van alle producten uit product table.
    private List<Productlijst> getLijstproducten(overviewProductLijstFragment overviewProductLijstFragment) {
        DatabaseHelper dbH = new DatabaseHelper(this.getContext());
        SQLiteDatabase db = dbH.getReadableDatabase();
        List<Productlijst> Productlijst = new ArrayList<>();
        try {
            String sql = "select * from " + TABLE_Productlijst + " where " +  COLUMN_NAME_LIJSTNAAM +"='"+lijstnaam+"'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String eannummer = cursor.getString(cursor.getColumnIndex("eannnummer"));
                    String lijstnaam = cursor.getString(cursor.getColumnIndex("lijstnaam"));
                    int hoeveelheid = cursor.getInt(cursor.getColumnIndex("hoeveelheid"));
                    String eenheid = cursor.getString(cursor.getColumnIndex("eenheid"));
                    Productlijst lp = new Productlijst(lijstnaam,eannummer, eenheid, hoeveelheid);
                    Productlijst.add(lp);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            System.out.println("ERROR" + e);
        } finally {
            db.close();
        }
        return Productlijst;
    }

}