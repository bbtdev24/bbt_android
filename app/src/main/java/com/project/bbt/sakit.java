package com.project.bbt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.Item.LoginItem;
import com.project.bbt.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


import static com.project.bbt.izin.txt_nomor;
import static com.project.bbt.menu.permissions;
import static com.project.bbt.menu.txt_alpha;
import static com.project.bbt.menu.txt_lokasi;
import static com.project.bbt.menu.txt_nama;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class sakit extends AppCompatActivity {
    SweetAlertDialog Success;
    private SearchableSpinner spinner;
    ArrayList<String> Karyawan;
    EditText nopengajuan, editText, keterangan;
    Button pengajuan;
    private long mLastClickTime;
    TextView longitude1, latitude1;
    ProgressDialog pDialog;
    final int CODE_GALLERY_REQUEST = 999;
    ImageView gambar1;
    Bitmap bitmap;
    ImageButton add;
    public int numberOfLines = 1;
    private SimpleDateFormat dateFormatter;
    private Calendar date;
    SharedPreferences sharedPreferences;
    private List<EditText> editTextList = new ArrayList<EditText>();
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    DrawerLayout dLayout;
    Button upload;
    String string_nip_karyawan, string_lokasi_karyawan,
            string_jabatan_karyawan, string_nama_karyawan,
            string_nama_divisi, string_id_jabatan, string_id_lokasi,
            string_id_divisi, string_id_bagian;

    TextView kondisi;

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        Calendar twoDaysAgo = (Calendar) currentDate.clone();
        twoDaysAgo.add(Calendar.DATE, -3);

        date = currentDate.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                editText.setText(dateFormatter.format(date.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(sakit.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sakit);
        NukeSSLCerts.nuke();
        mLastClickTime = 0;
        setNavigationDrawer();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_id_lokasi = sharedPreferences.getString("str_id_lokasi", null);
        string_id_divisi = sharedPreferences.getString("str_id_divisi", null);
        string_id_bagian = sharedPreferences.getString("str_id_bagian", null);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Form Sakit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        Karyawan = new ArrayList<>();
        spinner = (SearchableSpinner) findViewById(R.id.karyawan);

        longitude1 = (TextView) findViewById(R.id.longitude);
        latitude1 = (TextView) findViewById(R.id.lat);


        nopengajuan = (EditText) findViewById(R.id.nopengajuan);
        editText = (EditText) findViewById(R.id.tanggal);
        keterangan = (EditText) findViewById(R.id.keterangan);
        gambar1 = (ImageView) findViewById(R.id.gambar1);

        pengajuan = (Button) findViewById(R.id.pengajuan);
        dateFormatter = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());

        nopengajuan.setFocusable(false);
        add = (ImageButton) findViewById(R.id.tambah);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final LinearLayout ll = (LinearLayout) findViewById(R.id.container);
//                final EditText tanggal1 = new EditText(sakit.this);
//                final ImageButton minus = new ImageButton(sakit.this);
//                tanggal1.setBackgroundResource(R.drawable.editext_rounded_outline);
//
//                tanggal1.setPadding(0,0,0,0);
//
//                double sizeInDP = 350.4649;
//                int marginInDp = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP, getResources()
//                                .getDisplayMetrics());
//
//                double sizeInDP2 = 50;
//                int marginInDp2 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, (float) sizeInDP2, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP3 = 60;
//                int marginInDp3 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP3, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP4 = 27;
//                int marginInDp4 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP4, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP5 = 295;
//                int marginInDp5 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP5, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP6 = -30;
//                int marginInDp6 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP6, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP7 = 10;
//                int marginInDp7 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP7, getResources()
//                                .getDisplayMetrics());
//
//                int sizeInDP8 = 16;
//                int marginInDp8 = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP8, getResources()
//                                .getDisplayMetrics());
//
//
//                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(marginInDp, marginInDp2);
//                lparams.setMargins(0, 20, 30, 0);
//
//                RelativeLayout.LayoutParams button = new RelativeLayout.LayoutParams(marginInDp8,marginInDp8);
//                button.setMargins(marginInDp5, marginInDp6, 30, 0);
//
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(1000, 115);
//                p.setMargins(0, 20, 30, 0);
//
//                tanggal1.setLayoutParams(p);
//                tanggal1.setId(numberOfLines);
//                tanggal1.setFocusable(false);
//                tanggal1.setPadding(marginInDp7,0,0,0);
//                tanggal1.setLayoutParams(lparams);
//                numberOfLines++;
//                editTextList.add(tanggal1);
//                ll.addView(tanggal1);
//
//                minus.setLayoutParams(button);
//                minus.setBackgroundResource(R.drawable.btn_erase);
//
//                minus.setId(numberOfLines);
//                numberOfLines++;
//                ll.addView(minus);
//
//                minus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ll.removeView(tanggal1);
//                        ll.removeView(minus);
//                        editTextList.remove(tanggal1);
//                    }
//                });


                // Buat EditText
//                final EditText tanggal1 = new EditText(sakit.this);
//                tanggal1.setBackgroundResource(R.drawable.editext_rounded_outline);
//                tanggal1.setPadding(39, 39, 39, 39); // Padding sesuai XML
//                tanggal1.setHint("Pilih Tanggal");
//                tanggal1.setClickable(true);
//                tanggal1.setFocusable(false);
//                tanggal1.setTextColor(Color.BLACK);
//
//                // LayoutParams untuk EditText
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//                p.setMargins(0, 0, 0, 0); // Margin sesuai XML
//                tanggal1.setLayoutParams(p);
//                tanggal1.setId(View.generateViewId()); // Set ID unik
//
//                // Tambah EditText ke LinearLayout
//                ll.addView(tanggal1);
//
//                // Buat ImageButton
//                final ImageButton minus = new ImageButton(sakit.this);
//                minus.setBackgroundResource(R.drawable.btn_erase);
//
//                // LayoutParams untuk ImageButton
//                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
//                        dpToPx(27), dpToPx(27) // Ukuran sesuai XML
//                );
//                buttonParams.setMargins(dpToPx(0), 0, 0, 0); // Margin sesuai XML
//                minus.setLayoutParams(buttonParams);
//                minus.setId(View.generateViewId()); // Set ID unik
//
//                // Tambah ImageButton ke LinearLayout
//                ll.addView(minus);

                // Buat RelativeLayout sebagai container

                final LinearLayout ll = (LinearLayout) findViewById(R.id.container);

                RelativeLayout container = new RelativeLayout(sakit.this);
                RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                container.setLayoutParams(containerParams);

                // Buat EditText
                final EditText tanggal1 = new EditText(sakit.this);
                tanggal1.setBackgroundResource(R.drawable.editext_rounded_outline);
                tanggal1.setPadding(39, 39, 39, 39); // Padding sesuai XML
                tanggal1.setHint("Pilih Tanggal");
                tanggal1.setClickable(true);
                tanggal1.setFocusable(false);
                tanggal1.setTextColor(Color.BLACK);

                // LayoutParams untuk EditText
                RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                editTextParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                editTextParams.addRule(RelativeLayout.LEFT_OF, View.generateViewId()); // Agar ada ruang untuk ImageButton
                editTextParams.setMargins(0, dpToPx(8), 0, 0); // Menambahkan margin top 8dp
                tanggal1.setLayoutParams(editTextParams);
                tanggal1.setId(View.generateViewId()); // Set ID unik

                // Tambah EditText ke RelativeLayout
                container.addView(tanggal1);

                // Buat ImageButton
                final ImageButton minus = new ImageButton(sakit.this);
                minus.setBackgroundResource(R.drawable.btn_erase);

                // LayoutParams untuk ImageButton
                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                        dpToPx(27), dpToPx(27) // Ukuran sesuai XML
                );
                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); // Menempatkan ImageButton di kanan
                buttonParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); // Agar ImageButton berada di tengah vertikal
                buttonParams.setMargins(0, 0, dpToPx(16), 0); // Menambahkan margin kanan 16dp
                minus.setLayoutParams(buttonParams);
                minus.setId(View.generateViewId()); // Set ID unik

                // Tambah ImageButton ke RelativeLayout
                container.addView(minus);

                // Tambahkan RelativeLayout ke LinearLayout utama
                ll.addView(container);

                // OnClickListener untuk ImageButton
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll.removeView(tanggal1);
                        ll.removeView(container);
                        // Hapus dari list jika ada
                    }
                });

                tanggal1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar currentDate = Calendar.getInstance();
                        Calendar twoDaysAgo = (Calendar) currentDate.clone();
                        twoDaysAgo.add(Calendar.DATE, -3);

                        date = currentDate.getInstance();

                        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                date.set(year, monthOfYear, dayOfMonth);
                                tanggal1.setText(dateFormatter.format(date.getTime()));
                            }
                        };
                        DatePickerDialog datePickerDialog = new DatePickerDialog(sakit.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });

                int c = editTextList.size();
                System.out.println("rows = " + c);

            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            final Handler handler = new Handler();
            Runnable refresh = new Runnable() {
                @Override
                public void run() {
                    getLocation();
                    handler.postDelayed(this, 3000);
                }
            };
            handler.postDelayed(refresh, 3000);
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });


        pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pengajuan.setEnabled(false);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        pengajuan.setEnabled(true);
                    }
                }, 1500);// set time as per your requirement

                if (editText.getText().toString().length() == 0) {
                    editText.setError("Masukkan Tanggal!");
                } else if (keterangan.getText().toString().length() == 0) {
                    keterangan.setError("Masukkan Keterangan!");
                } else if (gambar1.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Upload gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else if (spinner.getSelectedItem() == "PILIH PEGAWAI") {
                    Toast.makeText(getApplicationContext(), "pilih Pegawai terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else if (longitude1.getText().toString().equals("long") && (latitude1.getText().toString().equals("lat"))) {
                    Toast.makeText(getApplicationContext(), "Lokasi belum ditemukan", Toast.LENGTH_SHORT).show();
                } else {
                    if (editTextList.size() == 0) {
                        getNomorPengajuan();
                    } else {
                        for (int i = 0; i < editTextList.size(); i++) {
                            final String tanggal = editTextList.get(i).getText().toString();
                            if (tanggal.equals("null") || (tanggal == null) || tanggal.equals("")) {
                                Toast.makeText(getApplicationContext(), "Masukkan Tanggal", Toast.LENGTH_SHORT).show();
                                break;
                            } else if (i == editTextList.size() - 1) {
                                getNomorPengajuan();
                            }
                        }
                    }
                }
            }
        });


        AtomicLong mLastClickTime = new AtomicLong();
        spinner.setOnTouchListener((v, event) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime.get() < 1000) {
                spinner.setEnabled(false);
                return false;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    spinner.setEnabled(true);
                }
            }, 3000);
            mLastClickTime.set(SystemClock.elapsedRealtime());
            event.setAction(MotionEvent.ACTION_UP);
            spinner.onTouch(v, event);
            spinner.setEnabled(false);

            return true;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setEnabled(true);
            }
        });
        getLokasi();

        upload = (Button) findViewById(R.id.uploadbutton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(sakit.this,
                        permissions(),
                        CODE_GALLERY_REQUEST);
            }
        });
    }

    // Helper method untuk konversi dp ke px
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setNavigationDrawer() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // initiate a Navigation View
        View headerView = navView.getHeaderView(0);
        final TextView hari = headerView.findViewById(R.id.hari);
        kondisi = headerView.findViewById(R.id.kondisi);

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
                            sakit.this);
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
                                    Intent intent = new Intent(sakit.this, MainActivity.class);
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
                    dLayout.closeDrawers(); // close the all open Drawer Views
                    return true;
                }
                return false;
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access gallery!", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(path);
                bitmap = BitmapFactory.decodeStream(inputStream);
                gambar1.setImageBitmap(bitmap);
                gambar1.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(sakit.this, "Gambar sudah diupload", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        GPSTracker gpsTracker = new GPSTracker(sakit.this);
        if (gpsTracker.canGetLocation()) {
            latitude1.setText(String.valueOf(gpsTracker.getLatitude()));
            longitude1.setText(String.valueOf(gpsTracker.getLongitude()));
        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    private void getLokasi() {

        String lokasi = izin.txt_jabatan.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/karyawan/index_get_all_karyawan?lokasi_struktur=" + string_lokasi_karyawan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Karyawan.add("PILIH PEGAWAI");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String karyawan = jsonObject1.getString("nama_karyawan_struktur");
                            String nik = jsonObject1.getString("nik_baru");
                            String jabatan = jsonObject1.getString("dept_struktur");
                            Karyawan.add(karyawan + " (" + nik + ") ");
                            if (jabatan.equals("Board Of Director")) {
                                Karyawan.remove(karyawan + " (" + nik + ") ");
                            }
                        }
                    }
                    spinner.setTitle("Pilih Pegawai");
                    spinner.setAdapter(new ArrayAdapter<String>(sakit.this, android.R.layout.simple_spinner_dropdown_item, Karyawan));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(sakit.this, "Terjadi Kesalahan saat memuat data", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //OK-
    private void getNomorPengajuan() {
        pDialog = new ProgressDialog(this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/nomor_pengajuan/index_last_nomor_pengajuan", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            JSONObject finalObject3 = jsonArray.getJSONObject(jsonArray.length() - 1);
                            int a = finalObject3.getInt("no_pengajuan_full_day");

                            System.out.println("Test = " + a);

                            // Format ke 4 digit sebelum menampilkan
                            @SuppressLint("DefaultLocale") String formattedNumber = String.format("%04d", a + 1);
                            nopengajuan.setText(formattedNumber);

                            image();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(getApplicationContext(), "Maaf, Ada Kesalahan", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        requestQueue1.add(request);
    }

    //OK--
    private void image() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/php/upload_image_izin.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String status = json.getString("response");
                            if (status.contains("Success")) {
                                postfullfirst();
                            } else if (status.contains("Image not uploaded")) {
                                hideDialog();
                                // Menampilkan log ketika "Image not uploaded"
                                Log.e("UploadImage", "Response: Image not uploaded");
                                Toast.makeText(getApplicationContext(), "Maaf ada kesalahan", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("UploadImage", "JSONException: " + e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "Maaf ada kesalahan", Toast.LENGTH_SHORT).show();

                        //Jika terjadi error
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("VolleyError Image", "Error response code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError Image", "Error message: " + errorMsg);
                        } else {
                            Log.e("VolleyError Image", "Unexpected error: " + error.getMessage());
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                String pengajuan = nopengajuan.getText().toString();
                String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);
                String gambar = imagetoString(bitmap);

                params.put("no_pengajuan_full_day", pengajuan + "_" + nik_baru);
                params.put("foto", gambar);

                return params;
            }
        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }

    //OK--
    private void postfullfirst() {
        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/izin_full_day/index_insert_izin_full_day",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), "Berhasil Mengajukan Izin", Toast.LENGTH_LONG).show();

                        if (editTextList.size() == 0) {
                            postNotif();
                            Toast.makeText(sakit.this, "1 HARI CHECK", Toast.LENGTH_SHORT).show();

                            hideDialog();
                            Success = new SweetAlertDialog(sakit.this, SweetAlertDialog.SUCCESS_TYPE);
                            Success.setContentText("Data Sudah Ditambahkan");
                            Success.setCancelable(false);
                            Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            });
                            Success.show();

                        } else {
                            postfull();
                            Toast.makeText(sakit.this, "LEBIH 1 HARI CHECK", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "Maaf Ada Kesalahan", Toast.LENGTH_LONG).show();

                        // Jika terjadi error
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("VolleyError", "Error response code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Error message: " + errorMsg);
                        } else {
                            Log.e("VolleyError", "Unexpected error: " + error.getMessage());
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                String pengajuan = nopengajuan.getText().toString();
                String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);
                String tanggal = editText.getText().toString();
                String jabatan = txt_nomor.getText().toString();
                String karyawan = spinner.getSelectedItem().toString();
                String[] splited_text = karyawan.split(" \\(");
                karyawan = splited_text[0];
                String keterangan_sakit = keterangan.getText().toString();
                String gambar = nopengajuan.getText().toString();
                String longitudee = longitude1.getText().toString();
                String lat = latitude1.getText().toString();

                params.put("no_pengajuan_full_day", pengajuan);
                params.put("nik_full_day", nik_baru);
                params.put("jabatan_full_day", string_id_jabatan);
                params.put("jenis_full_day", "SA");

                params.put("start_full_day", convertFormat(tanggal));
                params.put("karyawan_pengganti", karyawan);
                params.put("ket_tambahan", keterangan_sakit);
                params.put("status_full_day", "0");

                params.put("status_full_day_2", "0");
                params.put("upload_full_day", gambar + "_" + nik_baru + ".jpeg");

                params.put("lat", lat);
                params.put("lon", longitudee);

                return params;
            }
        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }

    private void postfull() {

        for (int i = 0; i < editTextList.size(); i++) {

            final String tanggal = editTextList.get(i).getText().toString();
            final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/izin_full_day/index_insert_izin_full_day",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            postNotif();

                            hideDialog();
                            Success = new SweetAlertDialog(sakit.this, SweetAlertDialog.SUCCESS_TYPE);
                            Success.setContentText("Data Sudah Ditambahkan");
                            Success.setCancelable(false);
                            Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            });
                            Success.show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    String creds = String.format("%s:%s", "admin", "Dev24");
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                    String pengajuan = nopengajuan.getText().toString();
                    String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

                    String jabatan = txt_nomor.getText().toString();
                    String karyawan = spinner.getSelectedItem().toString();
                    String[] splited_text = karyawan.split(" \\(");
                    karyawan = splited_text[0];
                    String keterangan_sakit = keterangan.getText().toString();
                    String gambar = nopengajuan.getText().toString();
                    String longitudee = longitude1.getText().toString();
                    String lat = latitude1.getText().toString();

                    params.put("no_pengajuan_full_day", pengajuan);
                    params.put("nik_full_day", nik_baru);
                    params.put("jabatan_full_day", string_id_jabatan);
                    params.put("jenis_full_day", "SA");

                    params.put("start_full_day", convertFormat(tanggal));
                    params.put("karyawan_pengganti", karyawan);
                    params.put("ket_tambahan", keterangan_sakit);

                    params.put("status_full_day", "0");
                    params.put("status_full_day_2", "0");
                    params.put("upload_full_day", gambar + "_" + nik_baru + ".jpeg");
                    params.put("lat", lat);
                    params.put("lon", longitudee);

                    return params;
                }
            };
            stringRequest2.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            RequestQueue requestQueue2 = Volley.newRequestQueue(this);
            requestQueue2.add(stringRequest2);
        }
    }

    private void postNotif() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + string_id_jabatan + "&lokasi_hrd=" + string_id_lokasi + "&idDivisi=" + string_id_divisi + "&idBagian=" + string_id_bagian,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject movieObject = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                movieObject = movieArray.getJSONObject(i);

                                postNotifikasi(movieObject.getString("device_token"));

                                System.out.println("Device Token " + movieObject.getString("device_token"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_v1.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("postNotifikasi", "Response received: " + response);

                                        hideDialog();
                                        Success = new SweetAlertDialog(sakit.this, SweetAlertDialog.SUCCESS_TYPE);
                                        Success.setContentText("Data Sudah Ditambahkan");
                                        Success.setCancelable(false);
                                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                finish();
                                            }
                                        });
                                        Success.show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.e("postNotifikasi", "Error response: " + error.toString());
                                        if (error.networkResponse != null) {
                                            Log.e("postNotifikasi", "Status code: " + error.networkResponse.statusCode);
                                            Log.e("postNotifikasi", "Response data: " + new String(error.networkResponse.data));
                                        } else {
                                            Log.e("postNotifikasi", "Network response is null, possibly a network error or server unreachable.");
                                        }


                                        hideDialog();
                                        Success = new SweetAlertDialog(sakit.this, SweetAlertDialog.SUCCESS_TYPE);
                                        Success.setContentText("Data Sudah Ditambahkan");
                                        Success.setCancelable(false);
                                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                finish();
                                            }
                                        });
                                        Success.show();
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> params = new HashMap<String, String>();
                                String creds = String.format("%s:%s", "admin", "Dev24");
                                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                params.put("Authorization", auth);
                                Log.d("postNotifikasi", "Headers: " + params.toString());
                                return params;
                            }

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_YEAR, 2);

                                Date futureDate = calendar.getTime();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");

                                String currentDateandTime = dateFormat.format(futureDate);

                                params.put("device", device_token);

                                params.put("title", kondisi.getText().toString());
                                params.put("body", "Terdapat pengajuan IZIN SAKIT a/n " + txt_nama.getText().toString() + ", menunggu approval." + " *) Masa berlaku s/d " + currentDateandTime);

                                System.out.println("notif = " + params);

                                Log.d("postNotifikasi", "Parameters: " + params.toString());

                                return params;
                            }
                        };
                        stringRequest2.setRetryPolicy(
                                new DefaultRetryPolicy(
                                        500000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                )
                        );
                        RequestQueue requestQueue2 = Volley.newRequestQueue(sakit.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        Success = new SweetAlertDialog(sakit.this, SweetAlertDialog.SUCCESS_TYPE);
                        Success.setContentText("Data Sudah Ditambahkan");
                        Success.setCancelable(false);
                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                finish();
                            }
                        });
                        Success.show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return convetDateFormat.format(date);
    }

    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());
        super.onDestroy();
    }
}

//10.12.1.74
