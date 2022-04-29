package com.example.voorraadbeheer.Database;

import android.provider.BaseColumns;

public class DataContract {

    private DataContract(){

    }
    public static class Lijst implements BaseColumns {
        public static final String TABLE_Lijst = "lijst";
        public static final String COLUMN_NAME_LIJSTNAAM = "lijstnaam";
    }

    public static class Productlijst implements BaseColumns {
        public static final String TABLE_Productlijst = "productlijst";
        public static final String COLUMN_NAME_EANNUMER = "eannnummer";
        public static final String COLUMN_NAME_LIJSTNAAM = "lijstnaam";
        public static final String COLUMN_NAME_HOEVEELHEID = "hoeveelheid";
        public static final String COLUMN_NAME_EENHEID = "eenheid";
    }

    public static class Producten implements BaseColumns {
        public static final String TABLE_Producten = "producten";
        public static final String COLUMN_NAME_EANNUMER = "eannnummer";
        public static final String COLUMN_NAME_PRODUCTNAAM = "productnaam";
        public static final String COLUMN_NAME_PRODUCTGROEP = "productgroep";
    }

    public static class Voorraadproducten implements BaseColumns {
        public static final String TABLE_Voorraadprocuten = "voorraadproducten";
        public static final String COLUMN_NAME_EANNUMER = "eannnummer";
        public static final String COLUMN_NAME_HOUDBAARHEIDDATUM = "houdbaarheiddatum";
        public static final String COLUMN_NAME_HOEVEELHEID = "hoeveelheid";
        public static final String COLUMN_NAME_EENHEID = "eenheid";

    }

}
