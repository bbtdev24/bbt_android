package com.project.bbt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class detail_daily extends AppCompatActivity {
    TextView tanggal_date, jam, keterangan, jam_realisasi, keterangan_realisasi;
    EditText jam_mulai, catatan, editalasan, pengganti_pekerjaan;
    Button batal, simpan;
    String latitude, longitude;
    AutoCompleteTextView status;
    LocationManager locationManager;
    ProgressDialog pDialog;
    LinearLayout timesession, realisasi;
    TextInputLayout start, finish, pengganti;
    TextInputLayout uraian, alasan, kategori;
    SharedPreferences sharedPreferences;

    EditText kategori_kegiatan;

    MaterialButton clear;

    String status_pekerjaan [] = {"Ya", "Tidak"};

    AutoCompleteTextView durasi_jam;

    String jams [] = {"1 Jam", "2 Jam",
            "3 Jam", "4 Jam",
            "5 Jam", "6 Jam",
            "7 Jam", "8 Jam",};

    MaterialToolbar dailynBar;
    NavigationView navigation;
    DrawerLayout drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_daily);
        NukeSSLCerts.nuke();
//        AndroidBug5497Workaround.assistActivity(this);
        kategori_kegiatan = findViewById(R.id.kategori_kegiatan);
        uraian = findViewById(R.id.uraian);
        alasan = findViewById(R.id.alasan);
        editalasan = findViewById(R.id.editalasan);
        clear = findViewById(R.id.clear);
        kategori = findViewById(R.id.kategori);

        pengganti = findViewById(R.id.pengganti);
        pengganti_pekerjaan = findViewById(R.id.pengganti_pekerjaan);

        tanggal_date = findViewById(R.id.tanggal_date);
        status = findViewById(R.id.status);
        jam = findViewById(R.id.jam);
        keterangan = findViewById(R.id.keterangan);
        jam_mulai = findViewById(R.id.jam_mulai);
        durasi_jam = findViewById(R.id.durasijam);

        catatan = findViewById(R.id.catatan);
        final Handler handler2 = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                getLocation();
                handler2.postDelayed(this, 3000);
            }
        };
        handler2.postDelayed(refresh, 3000);

        batal = findViewById(R.id.batal);
        simpan = findViewById(R.id.simpan);
        timesession = findViewById(R.id.timesession);
        realisasi = findViewById(R.id.realisasi);

        jam_realisasi = findViewById(R.id.jam_realisasi);
        keterangan_realisasi = findViewById(R.id.keterangan_realisasi);

        start = findViewById(R.id.start);
        finish = findViewById(R.id.finish);

        dailynBar = findViewById(R.id.dailynBar);
        navigation = findViewById(R.id.navigation);
        drawer_layout = findViewById(R.id.drawer_layout);

        dailynBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catatan.setText("");
                editalasan.setText("");
                durasi_jam.setText("");
                pengganti_pekerjaan.setText("");
            }
        });

        durasi_jam.setAdapter(new ArrayAdapter<String>(detail_daily.this, android.R.layout.simple_dropdown_item_1line, jams));

        durasi_jam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    durasi_jam.showDropDown();
            }
        });

        durasi_jam.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                durasi_jam.showDropDown();
                return false;
            }
        });

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // initiate a Navigation View
        View headerView = navView.getHeaderView(0);
        final TextView hari = headerView.findViewById(R.id.hari);
        final TextView kondisi = headerView.findViewById(R.id.kondisi);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                hari.setText(currentDateandTime);
                handler.postDelayed(this, 1000);

                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

                if (timeOfDay >= 0 && timeOfDay < 9) {
                    kondisi.setText("Selamat Pagi");
                } else if (timeOfDay >= 9 && timeOfDay < 15) {
                    kondisi.setText("Selamat Siang");
                } else if (timeOfDay >= 15 && timeOfDay < 18) {
                    kondisi.setText("Selamat Sore");
                } else {
                    kondisi.setText("Selamat Malam");
                }
            }
        };
        runnable.run();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null; // create a Fragment Object
                int itemId = menuItem.getItemId(); // get selected menu item's id
                if (itemId == R.id.nav_home) {
                    Intent i = new Intent(getApplicationContext(), menu.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.password) {
                    Intent i = new Intent(getApplicationContext(), setting.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.ketentuan) {
                    Intent i = new Intent(getApplicationContext(), ketentuan.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.nav_exit) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            detail_daily.this);
                    alertDialogBuilder.setTitle("Anda yakin untuk Logout ?");
                    alertDialogBuilder
                            .setMessage("Klik Ya untuk keluar!")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent intent = new Intent(detail_daily.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else if (itemId == R.id.nav_calendar) {
                    Intent i = new Intent(getApplicationContext(), kalendar_event.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.commit(); // commit the changes
                    drawer_layout.closeDrawers(); // close the all open Drawer Views
                    return true;
                }
                return false;
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        status.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, status_pekerjaan));

        status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    status.showDropDown();
            }
        });

        status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(status.getText().toString().equals("Ya")){
                    uraian.setVisibility(View.VISIBLE);
                    alasan.setVisibility(View.GONE);
                    pengganti.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    finish.setVisibility(View.VISIBLE);
                    kategori.setVisibility(View.VISIBLE);
                } else {
                    uraian.setVisibility(View.GONE);
                    alasan.setVisibility(View.VISIBLE);
                    pengganti.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    finish.setVisibility(View.GONE);
                    kategori.setVisibility(View.GONE);
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        GPSTracker gpsTracker = new GPSTracker(detail_daily.this);
        if(gpsTracker.canGetLocation()){
            longitude = (String.valueOf(gpsTracker.getLongitude()));
            latitude = (String.valueOf(gpsTracker.getLatitude()));
        }else{
            gpsTracker.showSettingsAlert();
        }

    }

    public static String tanggalhari(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        return convetDateFormat.format(date);
    }

    private void showDialog () {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog () {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}