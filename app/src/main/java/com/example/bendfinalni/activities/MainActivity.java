package com.example.bendfinalni.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bendfinalni.R;
import com.example.bendfinalni.adapters.DrawerAdapter;
import com.example.bendfinalni.db.DatabaseHelper;
import com.example.bendfinalni.db.model.Bend;
import com.example.bendfinalni.dialogs.AboutDialog;
import com.example.bendfinalni.model.NavigationItems;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TOAST_MESSAGE = "toast";

    private int position = 1;
    private Bend bend = null;

    private DatabaseHelper databaseHelper = null;

    private ListView listViewMain = null;
    private List<Bend> bendList = null;
    private ArrayAdapter<Bend> bendArrayAdapter = null;

    private SharedPreferences sharedPreferences = null;
    boolean showMessage = false;

    private Spannable message1 = null;
    private Spannable message2 = null;

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private RelativeLayout drawerPane;
    private ArrayList<NavigationItems> drawerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawer();

        prikaziListuBenda();

    }

    private void prikaziListuBenda() {
        listViewMain = findViewById(R.id.list_view_MAIN);
        try {
            bendList = getDatabaseHelper().getBend().queryForAll();
            bendArrayAdapter = new ArrayAdapter<>(this, R.layout.list_array_adapter, R.id.list_array_text_view, bendList);
            listViewMain.setAdapter(bendArrayAdapter);
            listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    bend = (Bend) listViewMain.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("id", bend.getId());
                    startActivity(intent);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        listViewMain = findViewById(R.id.list_view_MAIN);
        if (listViewMain != null) {
            bendArrayAdapter = (ArrayAdapter<Bend>) listViewMain.getAdapter();
            if (bendArrayAdapter != null) {
                try {
                    bendList = getDatabaseHelper().getBend().queryForAll();
                    bendArrayAdapter.clear();
                    bendArrayAdapter.addAll(bendList);
                    bendArrayAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dodajBend() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dodaj_bend);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final EditText editNaziv = dialog.findViewById(R.id.add_bend_naziv);
        final EditText editVrstaMuzike = dialog.findViewById(R.id.add_bend_vrsta_muzike);
        final EditText editGodina = dialog.findViewById(R.id.add_bend_godina_nastanka);
        final EditText editMesto = dialog.findViewById(R.id.add_bend_mesto);

        Button confirm = dialog.findViewById(R.id.add_bend_button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNaziv.getText().toString().isEmpty()) {
                    editNaziv.setError("Polje naziv ne sme biti prazno!");
                    return;
                }
                if (editVrstaMuzike.getText().toString().isEmpty()) {
                    editVrstaMuzike.setError("Polje Vrsta Muzike ne sme biti prazno");
                    return;
                }
                if (editGodina.getText().toString().isEmpty() || editGodina.getText().toString().length() > 4
                        || !isValidDate(editGodina.getText().toString())) {
                    editGodina.setError("Format godine: yyyy");
                    return;
                }
                if (editMesto.getText().toString().isEmpty()) {
                    editMesto.setError("Polje mesto ne sme biti prazno!");
                    return;
                }

                String naziv = editNaziv.getText().toString();
                String vrstaMuzike = editVrstaMuzike.getText().toString();
                String godina = editGodina.getText().toString();
                String mesto = editMesto.getText().toString();

                bend = new Bend();
                bend.setNaziv(naziv);
                bend.setDatumPocetkaBenda(godina);
                bend.setAdresaBenda(mesto);
                bend.setTipMuzike(vrstaMuzike);

                try {
                    getDatabaseHelper().getBend().create(bend);
                    dialog.dismiss();
                    refresh();

                    message1 = new SpannableString("Uspesno kreiran Bend:  ");
                    message2 = new SpannableString(bend.getNaziv());

                    message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    message2.setSpan(new ForegroundColorSpan(Color.RED), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (showMessage) {
                        Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
                        View toastView = toast.getView();

                        TextView toastText = toastView.findViewById(android.R.id.message);
                        toastText.setText(message1);
                        toastText.append(message2);
                        toast.show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });


        Button cancel = dialog.findViewById(R.id.add_bend_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void consultPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        showMessage = sharedPreferences.getBoolean(TOAST_MESSAGE, true);
    }

    /**
     * Navigaciona Fioka
     */
    private void navigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar_MAIN);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerItems.add(new NavigationItems("Bend", "Prikazuje listu Bendova", R.drawable.ic_show_all));
        drawerItems.add(new NavigationItems("Podesavanja", "Otvara Podesavanja Aplikacije", R.drawable.ic_settings_black_24dp));
        drawerItems.add(new NavigationItems("Informacije", "Informacije o Aplikaciji", R.drawable.ic_about_app));

        DrawerAdapter drawerAdapter = new DrawerAdapter(this, drawerItems);
        drawerListView = findViewById(R.id.nav_list_MAIN);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        drawerTitle = getTitle();
        drawerLayout = findViewById(R.id.drawer_layout_MAIN);
        drawerPane = findViewById(R.id.drawer_pane_MAIN);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                super.onDrawerClosed(drawerView);
            }
        };

    }

    /**
     * OnItemClick iz NavigacioneFioke.
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                refresh();
            } else if (position == 1) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (position == 2) {
                AlertDialog aboutDialog = new AboutDialog(MainActivity.this).prepareDialog();
                aboutDialog.show();
            }

            drawerLayout.closeDrawer(drawerPane);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_dodaj_bend:
                dodajBend();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Proveravamo datum
     */
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        refresh();
        super.onRestart();
    }

    private void firstTimeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.first_time_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button ok = dialog.findViewById(R.id.first_time_button_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        refresh();
        consultPreferences();

        if (sharedPreferences.getBoolean("firstrun", true)) {
            firstTimeDialog();
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }

        super.onResume();
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", position);
    }

}
