package com.example.voorraadbeheer.ui.product;

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

import com.example.voorraadbeheer.Model.Product;
import com.example.vooraadbeheer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private ArrayList<Product> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener onItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;
    private AdapterProducts.OnItemLongClickListener OnItemLongClickListener;

    public AdapterProducts(Context context, List<Product> items) {
        this.items = (ArrayList<Product>) items;
        ctx = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoreButtonClickListener(OnMoreButtonClickListener OnMoreButtonClickListener) {
        this.onMoreButtonClickListener = OnMoreButtonClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Product ArrayList, int pos);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Product ArrayList, MenuItem item);
    }

    public interface OnItemLongClickListener {
        void onItemClick(View view, Product ArrayList, int pos);
    }

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
        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final Product o = items.get(position);
            view.productnaam.setText(o.getProductnaam());
            view.eamnummer.setText(o.getEannnummer());
            view.productgroep.setText(o.getProductgroep());

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

    // nodig voor aanmaken van view "AdapterProducts(getContext(), getProducts(this));", in overviewProductFragment.
    @Override
    public int getItemCount() {
        int uniqueItemIdCount = items.size();
        return uniqueItemIdCount;
    }

    private void onMoreButtonClick(final View view, final Product product) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, product, item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_people_more);
        popupMenu.show();
    }


}
