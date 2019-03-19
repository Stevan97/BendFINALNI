package com.example.bendfinalni.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.bendfinalni.db.model.Bend;
import com.example.bendfinalni.db.model.Pevaci;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "db.bend.final.";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private Dao<Bend, Integer> getmBend = null;
    private Dao<Pevaci, Integer> getmPevaci = null;

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Bend.class);
            TableUtils.createTable(connectionSource, Pevaci.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Pevaci.class, true);
            TableUtils.dropTable(connectionSource, Bend.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Bend, Integer> getBend() throws SQLException {
        if (getmBend == null) {
            getmBend = getDao(Bend.class);
        }
        return getmBend;
    }

    public Dao<Pevaci, Integer> getPevaci() throws SQLException {
        if (getmPevaci == null) {
            getmPevaci = getDao(Pevaci.class);
        }
        return getmPevaci;
    }

    @Override
    public void close() {
        getmPevaci = null;
        getmBend = null;
        super.close();
    }
}