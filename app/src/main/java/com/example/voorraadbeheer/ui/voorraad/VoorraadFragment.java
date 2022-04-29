package com.example.voorraadbeheer.ui.voorraad;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.vooraadbeheer.R;
import com.example.voorraadbeheer.ui.product.newProductFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class VoorraadFragment extends Fragment {
    LinearLayout productin, overzicht;
    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_voorraad, container, false);
        productin = rootView.findViewById(R.id.productin);
        overzicht = rootView.findViewById(R.id.overzichtvoorraad);

        //LinearLayout button Product In
        productin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scancode();
            }
        });

        //LinearLayout button Overzicht voorraad
        overzicht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_voorraad_to_nav_overzichtvoorraad);
            }
        });

        return rootView;
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

    // opent het scherm om een product toe tevoegen aan voorraad table.
    private void voorraadInvoeren(String contents) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Navigation.findNavController(this.rootView).navigate(R.id.action_nav_voorraad_to_nav_voorraadToevoegen);
        addVoorraadFragment ScanFragment = new addVoorraadFragment(contents);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, ScanFragment);
        fragmentTransaction.commit();

    }

    // opent het scherm om een product toe tevoegen aan Product table.
    private void newProduct(String contents) {
        newProductFragment newProductFragment = new newProductFragment(contents);
        Navigation.findNavController(this.rootView).navigate(R.id.action_nav_voorraad_to_nav_newproduct);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, newProductFragment);
        fragmentTransaction.commit();
    }

}