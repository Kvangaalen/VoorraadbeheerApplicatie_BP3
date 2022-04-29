package com.example.voorraadbeheer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Data.db";
    private Context context;

    private static final String SQL_CREATE_PRODUCT = "create table " + DataContract.Producten.TABLE_Producten + " ("
            + DataContract.Producten.COLUMN_NAME_EANNUMER + " text, "
            + DataContract.Producten.COLUMN_NAME_PRODUCTNAAM + " text, "
            + DataContract.Producten.COLUMN_NAME_PRODUCTGROEP + " text)";

    private static final String SQL_CREATE_Voorraadproducten = "create table " + DataContract.Voorraadproducten.TABLE_Voorraadprocuten + " ("
            + DataContract.Voorraadproducten.COLUMN_NAME_EANNUMER + " text, "
            + DataContract.Voorraadproducten.COLUMN_NAME_HOEVEELHEID+ " text, "
            + DataContract.Voorraadproducten.COLUMN_NAME_EENHEID+ " text, "
            + DataContract.Voorraadproducten.COLUMN_NAME_HOUDBAARHEIDDATUM + " text)";

    private static final String SQL_CREATE_Lijst = "create table " + DataContract.Lijst.TABLE_Lijst + " ("
            + DataContract.Lijst.COLUMN_NAME_LIJSTNAAM + " text)";

    private static final String SQL_CREATE_Productlijst = "create table " + DataContract.Productlijst.TABLE_Productlijst + " ("
            + DataContract.Productlijst.COLUMN_NAME_EANNUMER + " text, "
            + DataContract.Productlijst.COLUMN_NAME_LIJSTNAAM+ " text, "
            + DataContract.Productlijst.COLUMN_NAME_HOEVEELHEID+ " text, "
            + DataContract.Productlijst.COLUMN_NAME_EENHEID + " text)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_Voorraadproducten);
        db.execSQL(SQL_CREATE_PRODUCT);
        db.execSQL(SQL_CREATE_Lijst);
        db.execSQL(SQL_CREATE_Productlijst);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
