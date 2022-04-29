package com.example.voorraadbeheer.ui.voorraad;

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
import com.example.voorraadbeheer.Model.Voorraadproduct;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterVoorraadProducts extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private ArrayList<Voorraadproduct> items = new ArrayList<>();
    private Context ctx;
    private AdapterVoorraadProducts.OnItemClickListener onItemClickListener;
    private AdapterVoorraadProducts.OnMoreButtonClickListener onMoreButtonClickListener;
    private AdapterVoorraadProducts.OnItemLongClickListener OnItemLongClickListener;
    public AdapterVoorraadProducts(Context context, List<Voorraadproduct> items) {
        this.items = (ArrayList<Voorraadproduct>) items;
        ctx = context;
    }

    public void setOnItemClickLongListener(OnItemLongClickListener OnItemClickLongListener) {
        this.OnItemLongClickListener = OnItemClickLongListener;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoreButtonClickListener(OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Voorraadproduct obj, int item);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Voorraadproduct obj, MenuItem item);

    }

    public interface OnItemLongClickListener{
        void onItemClick(ViewHolder view, Voorraadproduct obj, int item);
    }

    // standaard onClick methode
    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eamnummer, productnaam, productgroep;
        public ImageButton more;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            eamnummer = (TextView) v.findViewById(R.id.producteannummer);
            productnaam = (TextView) v.findViewById(R.id.Lijstnaam);
            productgroep = (TextView) v.findViewById(R.id.productgroep);
            more = (ImageButton) v.findViewById(R.id.imageButton);
            lyt_parent = (View) v.findViewById(R.id.mainLayout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // aangeroepen door RecyclerView .....?
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Voorraadproduct o = items.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
                String naam = null;
                DatabaseHelper dbH = new DatabaseHelper(ctx.getApplicationContext());
                SQLiteDatabase db = dbH.getReadableDatabase();
                String sql = "select * from " + DataContract.Producten.TABLE_Producten + " Where  eannnummer = " + o.getEannnummer() + "";
                // ophalen van productnaam
                Cursor cursor = db.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    do {
                        naam = cursor.getString(cursor.getColumnIndex("productnaam"));
                    }  while (cursor.moveToNext());
                } else {
                    naam = "Product is verwijderd.";
                }

            view.productnaam.setText(naam);
            view.eamnummer.setText(o.getHoeveelheid() + " " + o.getEenheid());
            view.productgroep.setText(o.getHoudbaarheiddatum());

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
                public boolean onLongClick(View v) {
                    OnItemLongClickListener.onItemClick(view, o, position);
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



    @Override
    public int getItemCount() {
        int uniqueItemIdCount = items.size();
        return uniqueItemIdCount;
    }

    private void onMoreButtonClick(final View view, final Voorraadproduct Voorraadproduct) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, Voorraadproduct, item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_people_more);
        popupMenu.show();
    }

}
