package com.example.voorraadbeheer.ui.winkelijst;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voorraadbeheer.Database.DataContract;
import com.example.voorraadbeheer.Database.DatabaseHelper;
import com.example.voorraadbeheer.Model.Productlijst;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterLijstproducten extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private ArrayList<Productlijst> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener onItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;
    private OnItemClickLongListener OnItemLongClickListener;

    public AdapterLijstproducten(Context context, List<Productlijst> items) {
        this.items = (ArrayList<Productlijst>) items;
        ctx = context;
    }
    public void setOnMoreButtonClickListener(OnMoreButtonClickListener OnMoreButtonClickListener) {
        this.onMoreButtonClickListener = (OnMoreButtonClickListener) OnMoreButtonClickListener;
    }

    public void setOnItemLongClickListener(OnItemClickLongListener OnItemLongClickListener) {
        this.OnItemLongClickListener = OnItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Productlijst obj, int item);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Productlijst obj, MenuItem item);
    }

    public interface OnItemClickLongListener {
        void onItemLongClick(View view, Productlijst o, int position) ;
    }

    // standaard onClick methode
    @Override
    public void onClick(View v) { }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eamnummer, productnaam, productgroep, onStock;
        public ImageButton more;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            eamnummer = (TextView) v.findViewById(R.id.producteannummer);
            productnaam = (TextView) v.findViewById(R.id.Lijstnaam);
            productgroep = (TextView) v.findViewById(R.id.productgroep);
            onStock = (TextView) v.findViewById(R.id.onStock);
            more = (ImageButton) v.findViewById(R.id.imageButton);
            lyt_parent = (View) v.findViewById(R.id.mainLayout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_productlijst, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // aangeroepen door RecyclerView .....?
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final Productlijst o = items.get(position);
            String naam = null;
            DatabaseHelper dbH = new DatabaseHelper(ctx.getApplicationContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String sql = "select * from " + DataContract.Producten.TABLE_Producten + " Where  eannnummer = '" + o.getEannummer() + "'";
            Cursor cursor = db.rawQuery(sql, null);
            // ophalen van productnaam
            if (cursor.moveToFirst()) {
                do {
                    naam = cursor.getString(cursor.getColumnIndex("productnaam"));
                }  while (cursor.moveToNext());
            } else {
                naam = "Product is verwijderd.";
            }
            cursor.close();
            // controleren of product op voorraad is.
            if (itemOnStockCheck(o.getEannummer())){
                view.onStock.setText("Op voorraad");
            } else {
                view.onStock.setText("Niet voorraad");
            }

            view.productnaam.setText(naam);
            view.eamnummer.setText(o.getEannummer());
            view.productgroep.setText(o.getHoeveelheid() + " " + o.getEenheid());

            // set OnClick Listener
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener == null) return;
                    onItemClickListener.onItemClick(view, o, position);
                }
            });

            // set LongClick Listener
            view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    OnItemLongClickListener.onItemLongClick(view, o, position);
                    return false;
                }
            });

            // set onClick more Listener
            view.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onMoreButtonClickListener == null) return;
                    onMoreButtonClick(view, o);
                }
            });
        }

    }

    // controleren of product op voorraadlijst staat.
    private boolean itemOnStockCheck(String eannummer) {
        Cursor cursor = null;
        try {
            DatabaseHelper dbH = new DatabaseHelper(ctx.getApplicationContext());
            SQLiteDatabase db = dbH.getReadableDatabase();
            String sql = "select * from " + DataContract.Voorraadproducten.TABLE_Voorraadprocuten + " Where  eannnummer = '" + eannummer + "'";

            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    return true;
                }  while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return false;
    }

    // nodig voor aanmaken van view "AdapterProducts(getContext(), getProducts(this));", in overviewProductenToLijstFragement.
    @Override
    public int getItemCount() {
        int uniqueItemIdCount = items.size();
        return uniqueItemIdCount;
    }

    // popup image button "More"
    private void onMoreButtonClick(final View view, final Productlijst Productlijst) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, Productlijst, item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_people_more);
        popupMenu.show();
    }

}