package com.example.voorraadbeheer.ui.product;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Product;
import com.example.vooraadbeheer.R;
import com.example.voorraadbeheer.ui.voorraad.addVoorraadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import static com.example.voorraadbeheer.Database.DataContract.Producten.TABLE_Producten;


public class overviewProductFragment extends Fragment {
    private DatabaseHelper dbHelper = new DatabaseHelper(getContext());
    private RecyclerView recyclerView;
    private AdapterProducts adapter;
    private FloatingActionButton button;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_product, container, false);
        button = root.findViewById(R.id.add_button);

        initComponent(root);
        return root;
    }

    private void initComponent(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterProducts(getContext(), getProducts(this));
        recyclerView.setAdapter(adapter);
        button = root.findViewById(R.id.add_button);

        // lang indrukken van FloatingActionButton
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scancode();
                return false;
            }
        });

        // indrukken van FloatingActionButton
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View root) {
                Navigation.findNavController(root).navigate(R.id.action_nav_product_to_nav_newproduct2);
            }
        });

        // indrukken van AdapterProducts
        adapter.setOnItemClickListener(new AdapterProducts.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Product obj, int pos) {
                bewerkenProduct(obj);
            }
        });

        // klik op menuitems
        adapter.setOnMoreButtonClickListener(new AdapterProducts.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, Product obj, MenuItem item) {
                if (item.getTitle().equals("Verwijderen")) {
                    verwijderenProduct(obj);
                } else if (item.getTitle().equals("Bewerken")) {
                    bewerkenProduct(obj);
                }
            }
        });

    }

    // verwijderen van product uit database.
    private void verwijderenProduct(Product obj) {
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
                        long result = db.delete(TABLE_Producten, "eannnummer=?", new String[]{eannummer});
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
        } finally {
            dbHelper.close();
        }
    }


    // open van barcodescanner
    private void scancode() {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan de barcode");
        integrator.setBeepEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.initiateScan();
    }

    // return van scan
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                checkProductexists(result.getContents());
            }
        }
    }

    // Controle of scan bestaat in product table
    private void checkProductexists(String contents) {
        try {
            DatabaseHelper dbH = new DatabaseHelper(this.getContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String sql = "select * from " + DataContract.Producten.TABLE_Producten + " Where  eannnummer = " + contents + "";
            Cursor cursor = db.rawQuery(sql, null);
            // Aanroepen functie voor toevoegen van nieuwe product aan voorraad table
            if (cursor.moveToFirst()) {
                do { voorraadInvoeren(contents);}
                while (cursor.moveToNext());
            }
            // Aanroepen functie voor toevoegen van nieuwe product aan product table
            else {
                newProduct(contents);
            }
        } catch (NullPointerException nullPointerException) {
            System.out.println("ERROR");
        }

    }

    // ophalen van producten
    public List<Product> getProducts(overviewProductFragment productFragment) {
        List<Product> Produchten = new ArrayList<>();
        try {
            DatabaseHelper dbH = new DatabaseHelper(this.getContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String sql = "select * from " + DataContract.Producten.TABLE_Producten;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String producteannummer = cursor.getString(cursor.getColumnIndex("eannnummer"));
                    String productnnaam = cursor.getString(cursor.getColumnIndex("productnaam"));
                    String productgroep = cursor.getString(cursor.getColumnIndex("productgroep"));
                    Product p = new Product(producteannummer, productnnaam, productgroep);
                    Produchten.add(p);
                }
                while (cursor.moveToNext());
            }
        } catch (NullPointerException nullPointerException) {
            System.out.println("ERROR");
        }
        return Produchten;
    }

    // opent het scherm om een product toe te voegen aan voorraad table.
    private void voorraadInvoeren(String contents) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Navigation.findNavController(this.root).navigate(R.id.action_nav_product_to_nav_voorraadToevoegen);
        addVoorraadFragment ScanFragment = new addVoorraadFragment(contents);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, ScanFragment);
        fragmentTransaction.commit();
    }

    // opent het scherm om een product toe te voegen aan Product table.
    private void newProduct(String contents) {
        newProductFragment newProductFragment = new newProductFragment(contents);
        Navigation.findNavController(this.root).navigate(R.id.action_nav_product_to_nav_newproduct2);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, newProductFragment);
        fragmentTransaction.commit();
    }

    // opent het scherm om een product te bewerrken
    private void bewerkenProduct(Product obj) {
        editProductFragment fragment2 = new editProductFragment(obj);
        Navigation.findNavController(this.root).navigate(R.id.action_nav_product_to_edit_product);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
        fragmentTransaction.commit();
    }



}
