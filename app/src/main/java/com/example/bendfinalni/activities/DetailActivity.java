package com.example.bendfinalni.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bendfinalni.R;
import com.example.bendfinalni.adapters.DrawerAdapter;
import com.example.bendfinalni.adapters.PevaciAdapter;
import com.example.bendfinalni.db.DatabaseHelper;
import com.example.bendfinalni.db.model.Bend;
import com.example.bendfinalni.db.model.Pevaci;
import com.example.bendfinalni.dialogs.AboutDialog;
import com.example.bendfinalni.model.NavigationItems;
import com.example.bendfinalni.tools.InputOcena;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TOAST_MESSAGE = "toast";

    private int position = 1;
    private Bend bend = null;
    private Pevaci pevaci = null;

    private DatabaseHelper databaseHelper = null;

    private ListView listViewDetail = null;

    private ForeignCollection<Pevaci> pevaciForeignCollection = null;
    private List<Pevaci> pevaciList = null;
    private PevaciAdapter pevaciAdapter = null;
    private Intent intentPosition = null;
    private int idPosition = 0;

    private Spannable message1 = null;
    private Spannable message2 = null;
    private Spannable message3 = null;
    private Toast toast = null;
    private View toastView = null;
    private TextView textToast = null;

    private SharedPreferences sharedPreferences = null;
    boolean showMessage = false;

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private RelativeLayout drawerPane;
    private ArrayList<NavigationItems> drawerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        navigationDrawer();

        prikaziDetaljeBenda();

    }

    private void prikaziDetaljeBenda() {

        intentPosition = getIntent();
        idPosition = intentPosition.getExtras().getInt("id");

        try {
            bend = getDatabaseHelper().getBend().queryForId(idPosition);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextView naziv = findViewById(R.id.detail_naziv_benda);
        message1 = new SpannableString("Naziv Benda: ");
        message2 = new SpannableString(bend.getNaziv());
        spannableStyle();
        naziv.setText(message1);
        naziv.append(message2);

        TextView mesto = findViewById(R.id.detail_mesto_benda);
        message1 = new SpannableString("Mesto Benda:  ");
        message2 = new SpannableString(bend.getAdresaBenda());
        spannableStyle();
        mesto.setText(message1);
        mesto.append(message2);

        TextView vrstaMuzike = findViewById(R.id.detail_vrsta_muzike_benda);
        message1 = new SpannableString("Vrsta Muzike Benda: ");
        message2 = new SpannableString(bend.getTipMuzike());
        spannableStyle();
        vrstaMuzike.setText(message1);
        vrstaMuzike.append(message2);

        TextView datum = findViewById(R.id.godina_nastanka_benda);
        message1 = new SpannableString("Godina Nastanka:  ");
        message2 = new SpannableString(bend.getDatumPocetkaBenda());
        spannableStyle();
        datum.setText(message1);
        datum.append(message2);


        listViewDetail = findViewById(R.id.list_view_DETAIL);
        try {
            pevaciForeignCollection = getDatabaseHelper().getBend().queryForId(idPosition).getPevaci();
            pevaciList = new ArrayList<>(pevaciForeignCollection);
            pevaciAdapter = new PevaciAdapter(this, pevaciList);
            listViewDetail.setAdapter(pevaciAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void dodajPevaca() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dodaj_pevaca);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final EditText editNaziv = dialog.findViewById(R.id.dodaj_pevaca_naziv);
        final EditText editRodjenje = dialog.findViewById(R.id.dodaj_pevaca_datumRodj);
        final EditText editOcena = dialog.findViewById(R.id.dodaj_pevaca_ocena);

        /** Provera da li je unos od 0 do 10 jer imamo 10 zvezdica za rating bar ! */
        InputFilter limitFilter = new InputOcena(0, 10);
        editOcena.setFilters(new InputFilter[]{limitFilter});

        Button confirm = dialog.findViewById(R.id.dodaj_pevaca_button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNaziv.getText().toString().isEmpty()) {
                    editNaziv.setError("Naziv ne sme biti Prazan");
                    return;
                }
                if (editRodjenje.getText().toString().isEmpty() || !isValidDate(editRodjenje.getText().toString())) {
                    editRodjenje.setError("Format Datuma: dd-MM-yyy sa - !");
                    return;
                }
                if (editOcena.getText().toString().isEmpty()) {
                    editOcena.setError("Ocena mora od 0 do 10 !");
                    return;
                }

                String naziv = editNaziv.getText().toString();
                String rodjenje = editRodjenje.getText().toString();
                float ocena = Float.parseFloat(editOcena.getText().toString());

                intentPosition = getIntent();
                idPosition = intentPosition.getExtras().getInt("id");
                try {
                    bend = getDatabaseHelper().getBend().queryForId(idPosition);

                    pevaci = new Pevaci();
                    pevaci.setNazivPevaca(naziv);
                    pevaci.setDatumRodj(rodjenje);
                    pevaci.setOcenaPevaca(ocena);
                    pevaci.setBend(bend);

                    getDatabaseHelper().getPevaci().create(pevaci);
                    dialog.dismiss();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);

                    message1 = new SpannableString("Uspesno kreiran Pevac sa nazivom: ");
                    message2 = new SpannableString(pevaci.getNazivPevaca());
                    spannableStyle();

                    if (showMessage) {
                        toast = Toast.makeText(DetailActivity.this, "", Toast.LENGTH_LONG);
                        toastView = toast.getView();

                        textToast = toastView.findViewById(android.R.id.message);
                        textToast.setText(message1);
                        textToast.append(message2);
                        toast.show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        });

        Button cancel = dialog.findViewById(R.id.dodaj_pevaca_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void izbrisiPevaca() {
        intentPosition = getIntent();
        idPosition = intentPosition.getExtras().getInt("id");

        try {
            bend = getDatabaseHelper().getBend().queryForId(idPosition);
            pevaciForeignCollection = getDatabaseHelper().getBend().queryForId(idPosition).getPevaci();
            pevaciList = new ArrayList<>(pevaciForeignCollection);
            getDatabaseHelper().getPevaci().delete(pevaciList);
            getDatabaseHelper().getBend().delete(bend);
            onBackPressed();

            message1 = new SpannableString("Uspesno Izbrisan Bend: ");
            message2 = new SpannableString(bend.getNaziv());
            spannableStyle();

            if (showMessage) {
                toast = Toast.makeText(DetailActivity.this, "", Toast.LENGTH_LONG);
                toastView = toast.getView();

                textToast = toastView.findViewById(android.R.id.message);
                textToast.setText(message1);
                textToast.append(message2);
                toast.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void izmenaBenda() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.izmena_benda);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final EditText editNaziv = dialog.findViewById(R.id.izmeni_bend_naziv);
        final EditText editVrstaMuzike = dialog.findViewById(R.id.izmeni_bend_vrsta_muzike);
        final EditText editGodina = dialog.findViewById(R.id.izmeni_bend_godina_nastanka);
        final EditText editMesto = dialog.findViewById(R.id.izmeni_bend_mesto);

        Button confirm = dialog.findViewById(R.id.izmeni_bend_button_confirm);
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
                        || !isValidDate1(editGodina.getText().toString())) {
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

                bend.setNaziv(naziv);
                bend.setDatumPocetkaBenda(godina);
                bend.setAdresaBenda(mesto);
                bend.setTipMuzike(vrstaMuzike);

                try {
                    getDatabaseHelper().getBend().update(bend);
                    dialog.dismiss();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);

                    message1 = new SpannableString("Uspesna Izmena | Nov Naziv Benda: ");
                    message2 = new SpannableString(bend.getNaziv());

                    message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    message2.setSpan(new ForegroundColorSpan(Color.RED), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (showMessage) {
                        Toast toast = Toast.makeText(DetailActivity.this, "", Toast.LENGTH_LONG);
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

        Button cancel = dialog.findViewById(R.id.izmeni_bend_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void spannableStyle() {
        message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message2.setSpan(new ForegroundColorSpan(getColor(R.color.colorRED)), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void consultPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);
        showMessage = sharedPreferences.getBoolean(TOAST_MESSAGE, true);
    }

    @Override
    protected void onResume() {
        consultPreferences();
        super.onResume();
    }

    /**
     * Navigaciona Fioka
     */
    private void navigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar_DETAIL);
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
        drawerListView = findViewById(R.id.nav_list_DETAIL);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new DetailActivity.DrawerItemClickListener());

        drawerTitle = getTitle();
        drawerLayout = findViewById(R.id.drawer_layout_DETAIL);
        drawerPane = findViewById(R.id.drawer_pane_DETAIL);

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
                onBackPressed();
                overridePendingTransition(0, 0);
            } else if (position == 1) {
                Intent intent = new Intent(DetailActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (position == 2) {
                AlertDialog aboutDialog = new AboutDialog(DetailActivity.this).prepareDialog();
                aboutDialog.show();
            }

            drawerLayout.closeDrawer(drawerPane);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_detail_delete:
                izbrisiPevaca();
                break;
            case R.id.menu_detail_add_pevac:
                dodajPevaca();
                break;
            case R.id.menu_detail_update:
                izmenaBenda();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean isValidDate1(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Proveravamo datum
     */
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
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
