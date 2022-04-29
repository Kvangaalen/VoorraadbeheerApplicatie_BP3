package com.example.voorraadbeheer.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String eannnummer, productnaam, productgroep;

    public Product(String eannnummer, String productnaam, String productgroep) {
        this.eannnummer = eannnummer;
        this.productnaam = productnaam;
        this.productgroep = productgroep;
    }

    protected Product(Parcel in) {
        eannnummer = in.readString();
        productnaam = in.readString();
        productgroep = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getEannnummer() {
        return eannnummer;
    }

    public void setEannnummer(String eannnummer) {
        this.eannnummer = eannnummer;
    }

    public String getProductnaam() {
        return productnaam;
    }

    public void setProductnaam(String productnaam) {
        this.productnaam = productnaam;
    }

    public String getProductgroep() {
        return productgroep;
    }

    public void setProductgroep(String productgroep) {
        this.productgroep = productgroep;
    }

    public String toString(){ return productnaam; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eannnummer);
        dest.writeString(productnaam);
        dest.writeString(productgroep);
    }
}
