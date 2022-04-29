package com.example.voorraadbeheer.ui.winkelijst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voorraadbeheer.Model.Lijst;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterLijst extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private ArrayList<Lijst> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener onItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;
    private OnItemClickLongListener OnItemLongClickListener;

    public AdapterLijst(Context context, List<Lijst> items) {
        this.items = (ArrayList<Lijst>) items;
        ctx = context;
    }

    public void setOnMoreButtonClickListener(OnMoreButtonClickListener OnMoreButtonClickListener) {
        this.onMoreButtonClickListener = OnMoreButtonClickListener;
    }

    public void setOnItemLongClickListener(OnItemClickLongListener OnItemLongClickListener) {
        this.OnItemLongClickListener = OnItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Lijst obj, int item);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Lijst ArrayList, MenuItem item);
    }

    public interface OnItemClickLongListener {
        void onItemLongClick(View view, Lijst o, int position) ;
    }

    // standaard onClick methode
    @Override
    public void onClick(View v) { }

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
            lyt_parent = (View) v.findViewById(R.id.mainLayout); // View on view???
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.boodschappenlijst_row, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // aangeroepen door RecyclerView .....?
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final Lijst o = items.get(position);
            view.productnaam.setText(o.getLijstnaam());

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

    // nodig voor aanmaken van view "AdapterProducts(getContext(), getProducts(this));", in winkellijstfragment.
    @Override
    public int getItemCount() {
        int uniqueItemIdCount = items.size();
        return uniqueItemIdCount;
    }

    // popup image button "More"
    private void onMoreButtonClick(final View view, final Lijst Lijst) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, Lijst, item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_people_more);
        popupMenu.show();
    }



}
