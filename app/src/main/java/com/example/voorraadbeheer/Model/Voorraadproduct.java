package com.example.voorraadbeheer.Model;

public class Voorraadproduct {

    private String eannnummer;
    private String eenheid;

    private String houdbaarheiddatum;
    private int hoeveelheid;

    public Voorraadproduct(String eannnummer, String houdbaarheiddatum, int hoeveelheid, String eenheid) {
        this.eannnummer = eannnummer;
        this.houdbaarheiddatum = houdbaarheiddatum;
        this.hoeveelheid = hoeveelheid;
        this.eenheid = eenheid;
    }



    public String getEannnummer() {
        return eannnummer;
    }

    public void setEannnummer(String eannnummer) {
        this.eannnummer = eannnummer;
    }

    public String getEenheid() {
        return eenheid;
    }

    public void setEenheid(String eenheid) {
        this.eenheid = eenheid;
    }

    public String getHoudbaarheiddatum() {
        return houdbaarheiddatum;
    }

    public void setHoudbaarheiddatum(String houdbaarheiddatum) {
        this.houdbaarheiddatum = houdbaarheiddatum;
    }

    public int getHoeveelheid() {
        return hoeveelheid;
    }

    public void setHoeveelheid(int hoeveelheid) {
        this.hoeveelheid = hoeveelheid;
    }
}
