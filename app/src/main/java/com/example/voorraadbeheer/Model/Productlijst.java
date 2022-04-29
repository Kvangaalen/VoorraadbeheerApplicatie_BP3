package com.example.voorraadbeheer.Model;

public class Productlijst {

    private String eannummer, lijstnaam, eenheid;
    private  int hoeveelheid;

    public Productlijst(String lijstnaam, String eannummer, String eenheid, int Hoeveelheid) {
        this.eannummer = eannummer;
        this.lijstnaam =lijstnaam;
        this.eenheid = eenheid;
        this.hoeveelheid = Hoeveelheid;
    }

    public String getEannummer() {
        return eannummer;
    }

    public void setEannummer(String eannummer) {
        this.eannummer = eannummer;
    }

    public String getLijstnaam() {
        return lijstnaam;
    }

    public void setLijstnaam(String lijstnaam) {
        this.lijstnaam = lijstnaam;
    }

    public String getEenheid() {
        return eenheid;
    }

    public void setEenheid(String eenheid) {
        this.eenheid = eenheid;
    }

    public int getHoeveelheid() {
        return hoeveelheid;
    }

    public void setHoeveelheid(int hoeveelheid) {
        this.hoeveelheid = hoeveelheid;
    }
}
