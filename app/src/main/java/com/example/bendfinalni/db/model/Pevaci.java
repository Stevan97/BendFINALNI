package com.example.bendfinalni.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Pevaci.PEVACI)
public class Pevaci {


    public static final String PEVACI = "pevaci";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAZIV_PEVACA = "nazivPevaca";
    private static final String FIELD_DATUMRODJ = "datumRodjenjaPevaca";
    private static final String FIELD_OCENA = "ocenaPevaca";
    private static final String FIELD_BEND = "bend";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAZIV_PEVACA)
    private String nazivPevaca;

    @DatabaseField(columnName = FIELD_DATUMRODJ)
    private String datumRodj;

    @DatabaseField(columnName = FIELD_OCENA)
    private float ocenaPevaca;

    @DatabaseField(columnName = FIELD_BEND, foreignAutoRefresh = true, foreign = true)
    private Bend bend;

    public Pevaci() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazivPevaca() {
        return nazivPevaca;
    }

    public void setNazivPevaca(String nazivPevaca) {
        this.nazivPevaca = nazivPevaca;
    }

    public String getDatumRodj() {
        return datumRodj;
    }

    public void setDatumRodj(String datumRodj) {
        this.datumRodj = datumRodj;
    }

    public float getOcenaPevaca() {
        return ocenaPevaca;
    }

    public void setOcenaPevaca(float ocenaPevaca) {
        this.ocenaPevaca = ocenaPevaca;
    }

    public Bend getBend() {
        return bend;
    }

    public void setBend(Bend bend) {
        this.bend = bend;
    }

    public String toString() {
        return nazivPevaca;
    }
}
