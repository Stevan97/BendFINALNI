package com.example.bendfinalni.db.model;

import android.renderscript.Script;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Bend.TABL_BEND)
public class Bend {

    public static final String TABL_BEND = "bend";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAZIV = "naziv";
    private static final String FIELD_TIP_MUZIKE = "tipMuzike";
    private static final String FIELD_DATUM_BEND = "kreiranBendDatum";
    private static final String FIELD_MESTO = "mesto";
    private static final String FIELD_PEVACI = "pevaci";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAZIV)
    private String naziv;

    @DatabaseField(columnName = FIELD_TIP_MUZIKE)
    private String tipMuzike;

    @DatabaseField(columnName = FIELD_DATUM_BEND)
    private String datumPocetkaBenda;

    @DatabaseField(columnName = FIELD_MESTO)
    private String adresaBenda;

    @ForeignCollectionField(columnName = FIELD_PEVACI, eager = true)
    private ForeignCollection<Pevaci> pevaci;

    public Bend() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTipMuzike() {
        return tipMuzike;
    }

    public void setTipMuzike(String tipMuzike) {
        this.tipMuzike = tipMuzike;
    }

    public String getDatumPocetkaBenda() {
        return datumPocetkaBenda;
    }

    public void setDatumPocetkaBenda(String datumPocetkaBenda) {
        this.datumPocetkaBenda = datumPocetkaBenda;
    }

    public String getAdresaBenda() {
        return adresaBenda;
    }

    public void setAdresaBenda(String adresaBenda) {
        this.adresaBenda = adresaBenda;
    }

    public ForeignCollection<Pevaci> getPevaci() {
        return pevaci;
    }

    public void setPevaci(ForeignCollection<Pevaci> pevaci) {
        this.pevaci = pevaci;
    }

    public String toString() {
        return naziv;
    }

}
