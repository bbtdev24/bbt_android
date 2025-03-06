package com.project.bbt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.project.bbt.Item.LoginItem;
import com.project.bbt.Item.deptmodel;
import com.project.bbt.Item.menu_payment;
import com.project.bbt.Item.namanikmodel;
import com.project.bbt.Item.statuscutikhusus;
import com.project.bbt.Item.statuscutitahunan;
import com.project.bbt.Item.statusdinasfullday;
import com.project.bbt.Item.statusdinasnonfullday;
import com.project.bbt.Item.statusizinfullday;
import com.project.bbt.Item.statusizinnonfullday;
import com.project.bbt.R;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import cn.pedant.SweetAlert.SweetAlertDialog;
import in.myinnos.imagesliderwithswipeslibrary.Animations.DescriptionAnimation;
import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView;
import in.myinnos.imagesliderwithswipeslibrary.Tricks.ViewPagerEx;

import static android.app.PendingIntent.getActivity;
import static com.project.bbt.izin.txt_nomor;


public class menu extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    SliderLayout image_place_holder;
    public static String branchLokasi, department;
    private boolean reloadNeed = true;
    String statusUpdate;
    SweetAlertDialog warning;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    ImageButton biodata, izin, kehadiran, dinas, cuti, payslip, resignbutton, qrcode, other, video;
    TextView izincount, izinnoncount, jumlahizin, dinascount, dinasnoncount, jumlahdinas,
            cutitahunancount, cutikhususcount, txt_jabatan2, txt_depart;
    public static TextView txt_alpha, text_perusahaan, text_jabatan, txt_lokasi, txt_dept, txt_nama, txt_nik;
    private List<namanikmodel> movieItemList;
    private List<deptmodel> dept;
    HashMap<String, String> url_maps;
    String nik_baru, level_jabatan_karyawan;

    private List<statusizinfullday> movieItemList1;
    private List<statusizinnonfullday> movieItemList2;
    private List<statusdinasfullday> movieItemList3;
    private List<statusdinasnonfullday> movieItemList4;
    private List<statuscutitahunan> movieItemList5;
    private List<statuscutikhusus> movieItemList6;
    SharedPreferences sharedPreferences;
    ReviewInfo reviewInfo;
    ReviewManager manager;
    UpdateManager mUpdateManager;
    String total_data_api_pemberitahuan;

    private static final int REQUEST_LOCATION = 1;
    private int installedVersionCode; // Version code of the installed app
    private int latestVersionCode;    // Latest version code fetched from the backend
    ImageButton kontak, absen;

    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;
    ImageView bellIcon;
    TextView notificationBadge;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_SHOW_DIALOG = "showDialog";
    private SharedPreferences.Editor editor;
    private static final String getURL = "https://ess.banktanah.id/bbt_api/rest_server/mobile_eis_2/gambar.php";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        NukeSSLCerts.nuke();

        bellIcon = findViewById(R.id.bell_icon);
        notificationBadge = findViewById(R.id.notification_badge);

        // Cek SharedPreferences apakah dialog perlu ditampilkan
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean showDialog = preferences.getBoolean(KEY_SHOW_DIALOG, true);

        // Inisialisasi Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Contoh: Kirim event ke Firebase Analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "email");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

        // Test Crashlytics (Jangan lupa hapus di production)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().log("App started!");

        // Simulasi crash (cek di Firebase Crashlytics)
//        findViewById(R.id.buttonCrash).setOnClickListener(v -> {
//            throw new RuntimeException("Test Crashlytics");
//        });

        mUpdateManager = UpdateManager.Builder(menu.this).mode(UpdateManagerConstant.IMMEDIATE);
        mUpdateManager.addUpdateInfoListener(new UpdateManager.UpdateInfoListener() {
            @Override
            public void onReceiveVersionCode(final int code) {
                mUpdateManager.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                menu.this);
                        alertDialogBuilder.setIcon(R.drawable.icon);
                        alertDialogBuilder.setTitle("Silahkan update versi aplikasi anda terlebih dahulu!");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int id) {
                                        finishAffinity();
                                        System.exit(0);
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }, 1500);
            }

            @Override
            public void onReceiveStalenessDays(final int days) {
                // Number of days passed since the user was notified of an update through the Google Play
                // If the user hasn't notified this will return -1 as days
                // You can decide the type of update you want to call
            }
        });

        try {
            installedVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        txt_nik = (TextView) findViewById(R.id.txt_nik);
        txt_nama = (TextView) findViewById(R.id.txt_nama);
        txt_alpha = (TextView) findViewById(R.id.txt_jabatan);
        jumlahizin = (TextView) findViewById(R.id.jumlahizin);
        text_jabatan = (TextView) findViewById(R.id.text_jabatan);
        jumlahdinas = (TextView) findViewById(R.id.jumlahdinas);
        text_perusahaan = (TextView) findViewById(R.id.text_perusahaan);
        txt_jabatan2 = (TextView) findViewById(R.id.txt_jabatan2);
        txt_depart = (TextView) findViewById(R.id.txt_depart);

        //resignbutton = (ImageButton) findViewById(R.id.resignbutton);
        //qrcode = (ImageButton) findViewById(R.id.qr);
        //Shifting = (ImageButton) findViewById(R.id.Shifting);
        //jumlahcuti = (TextView) findViewById(R.id.jumlahcuti);
        //video = findViewById(R.id.video);

        txt_nik.setText(string_nip_karyawan);
        txt_nama.setText(string_nama_karyawan);
        txt_depart.setText(string_jabatan_karyawan);
        txt_alpha.setText(string_id_jabatan);

        String nipwithoutdot = string_nip_karyawan.replaceAll("[^0-9]", "");
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(nipwithoutdot);

        other = (ImageButton) findViewById(R.id.other);
        biodata = (ImageButton) findViewById(R.id.biodata);
        izin = (ImageButton) findViewById(R.id.izin);
        kehadiran = (ImageButton) findViewById(R.id.kehadiran);
        dinas = (ImageButton) findViewById(R.id.dinas);
        cuti = (ImageButton) findViewById(R.id.cuti);

        payslip = (ImageButton) findViewById(R.id.slipgaji);
        izincount = (TextView) findViewById(R.id.izincount);
        izincount.setVisibility(View.GONE);
        izinnoncount = (TextView) findViewById(R.id.izinnoncount);
        izinnoncount.setVisibility(View.GONE);
        txt_lokasi = (TextView) findViewById(R.id.txt_lokasi);
        txt_dept = (TextView) findViewById(R.id.txt_dept);

        absen = (ImageButton) findViewById(R.id.absen);
        kontak = (ImageButton) findViewById(R.id.kontak);

        cutitahunancount = (TextView) findViewById(R.id.cutitahunancount);
        cutitahunancount.setVisibility(View.GONE);
        cutikhususcount = (TextView) findViewById(R.id.cutikhususcount);
        cutikhususcount.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }

        }

        dinascount = (TextView) findViewById(R.id.dinascount);
        dinascount.setVisibility(View.GONE);
        dinasnoncount = (TextView) findViewById(R.id.dinasnoncount);
        dinasnoncount.setVisibility(View.GONE);

        Drawable image = (Drawable) getResources().getDrawable(R.drawable.border_image);

        image_place_holder = (SliderLayout) findViewById(R.id.image_place_holder);

//        Intent intent = getIntent();
//
//        // Check if the intent contains data
//        if (intent != null && intent.getAction() != null && intent.getAction().equals("OPEN_ACTIVITY")) {
//            String extraValue = intent.getStringExtra("extra_key");
//            System.out.println("Extra = " + extraValue);
//        }

        FirebaseApp.initializeApp(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray movieArray = obj.getJSONArray("data");

                    for (int i = 0; i < movieArray.length(); i++) {
                        final JSONObject movieObject = movieArray.getJSONObject(i);
                        String imageUrl = movieObject.getString("image_url");

                        // Buat TextSliderView dengan imageUrl langsung
                        TextSliderView textSliderView = new TextSliderView(menu.this);
                        textSliderView
                                .descriptionSize(12)
                                .image(imageUrl)
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(menu.this);

                        // Add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle().putString("extra", imageUrl);
                        textSliderView.getBundle().putBoolean("noBackgroundOverlay", true);

                        image_place_holder.addSlider(textSliderView);
                        image_place_holder.setPresetTransformer(SliderLayout.Transformer.DepthPage);
                        image_place_holder.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        image_place_holder.setCustomAnimation(new DescriptionAnimation());
                        image_place_holder.setDuration(3000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Maaf, ada kesalahan", Toast.LENGTH_SHORT).show();

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image_place_holder.setClipToOutline(true);
        }

        movieItemList = new ArrayList<>();
        if (sharedPreferences.contains(LoginItem.KEY_NIK)) {
            getnama();
        }

        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);
        System.out.println("nik baru = " + nik_baru);

        getdept();

        biodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, manu_profil.class);
                startActivity(i);
            }
        });
        izin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, izin.class);
                i.putExtra(LoginItem.KEY_JABATAN, level_jabatan_karyawan);
                startActivity(i);

//                throw new RuntimeException("Test Crashlytics");

            }
        });

        kehadiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showCustomDialog();
//
                if ("1".equalsIgnoreCase(txt_alpha.getText().toString())) {
                    Intent i = new Intent(menu.this, detail_absensi.class);
                    startActivity(i);
                } else if ("2".equalsIgnoreCase(txt_alpha.getText().toString())) {
                    Intent i = new Intent(menu.this, detail_absensi.class);
                    startActivity(i);
                } else if ("3".equalsIgnoreCase(txt_alpha.getText().toString())) {
                    Intent i = new Intent(menu.this, detail_absensi.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(menu.this, spv_absensi.class);
                    startActivity(i);
                }
            }
        });

        dinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, dinas.class);
                startActivity(i);
            }
        });

        cuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, cuti.class);
                startActivity(i);
            }
        });

//        Shifting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ("1".equalsIgnoreCase(txt_alpha.getText().toString())) {
//                    Intent i = new Intent(menu.this, shifting.class);
//                    startActivity(i);
//                } else if ("2".equalsIgnoreCase(txt_alpha.getText().toString())) {
//                    Intent i = new Intent(menu.this, shifting.class);
//                    startActivity(i);
//                } else if ("3".equalsIgnoreCase(txt_alpha.getText().toString())) {
//                    Intent i = new Intent(menu.this, shifting.class);
//                    startActivity(i);
//                } else {
//                    Intent i = new Intent(menu.this, spv_shifting.class);
//                    startActivity(i);
//                }
//            }
//        });

        payslip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showCustomDialog();

                Intent intent = new Intent(menu.this, menu_payment.class);
                startActivity(intent);

            }
        });

//        resignbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(menu.this, "Maaf, Fitur Resign sedang ada maintenance", Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(menu.this, resign.class);
////                startActivity(intent);
//            }
//        });


//        qrcode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(menu.this, qrscanner.class);
//                startActivity(intent);
//            }
//        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, lainnya.class);
                startActivity(i);
            }
        });

        absen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, menu_absen_mobile.class);
                startActivity(i);
            }
        });

        kontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu.this, kontak_hr.class);
                startActivity(i);
            }
        });

        dept = new ArrayList<>();
        movieItemList1 = new ArrayList<>();
        movieItemList2 = new ArrayList<>();

        movieItemList3 = new ArrayList<>();
        movieItemList4 = new ArrayList<>();

        movieItemList5 = new ArrayList<>();
        movieItemList6 = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(
                menu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                menu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        manager = ReviewManagerFactory.create(this);
        askRatings();

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Mendapatkan totalData dari SharedPreferences, default 0 jika belum ada data
        int savedTotalData = sharedPreferences.getInt("totalData", 0);

        // Panggil API untuk mendapatkan totalData terbaru
        get_training_badge(savedTotalData);

        bellIcon.setOnClickListener(view -> {
            // Tampilkan ListView notifikasi
            // Misalnya: notificationListView.setVisibility(View.VISIBLE);
            notificationBadge.setVisibility(View.GONE); // Hilangkan badge

            // Cek apakah total_data_api_pemberitahuan null atau kosong
            if (total_data_api_pemberitahuan != null && !total_data_api_pemberitahuan.isEmpty()) {
                editor.putInt("totalData", Integer.parseInt(total_data_api_pemberitahuan));
                editor.apply();
            } else {
                // Atur nilai default jika null/kosong
                editor.putInt("totalData", 0);
                editor.apply();
            }

            Intent i = new Intent(menu.this, menu_bell_training.class);
            startActivity(i);
        });


        System.out.println("savedTotalData " + savedTotalData);

        get_kelengkapan_data_pegawai();

        // Cek & minta izin lokasi
//        checkLocationPermission();

    }



    private void postNotifCutiTahunan() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Cuti Tahunan Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    private void postNotifCutiKhusus() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Cuti Khusus Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    private void postNotifDinasNonFullDay() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Dinas Non Full Day Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    private void postNotifDinasFullDay() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Dinas Full Day Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    void askRatings() {
        manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    manager.launchReviewFlow(menu.this, reviewInfo).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
//                            Toast.makeText(menu.this, "Rating Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(menu.this, "Review Completed, Thank You!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(menu.this, "In-App Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getdept() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + nik_baru,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {

                                JSONObject movieObject = movieArray.getJSONObject(i);

                                deptmodel movieItem = new deptmodel(movieObject.getString("jabatan_karyawan"));

                                dept.add(movieItem);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                )
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getnama() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + nik_baru,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                      Toast.makeText(menu.this, "OK", Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {

                                JSONObject movieObject = movieArray.getJSONObject(i);

                                namanikmodel movieItem = new namanikmodel(
                                        movieObject.getString("nama_karyawan_struktur"),
                                        movieObject.getString("jabatan_karyawan"),
                                        movieObject.getString("idLokasi"),
                                        movieObject.getString("idJabatan"),
                                        movieObject.getString("idDivisi"));


//                                movieObject.getString("nama_karyawan_struktur"),
//                                        movieObject.getString("level_jabatan_karyawan"),
//                                        movieObject.getString("lokasi_struktur"),
//                                        movieObject.getString("jabatan_struktur"),
//                                        movieObject.getString("perusahaan_struktur"));

//                                txt_nama.setText(movieItem.getNama_karyawan_struktur());
//                                txt_alpha.setText(movieItem.getLevel_jabatan_karyawan());

                                text_jabatan.setText(movieItem.getJabatan_struktur());
                                txt_lokasi.setText(movieItem.getLokasi_struktur());
                                text_perusahaan.setText(movieItem.getPerusahaan_struktur());

                                getToken(txt_lokasi.getText().toString(), movieObject.getString("idLokasi"));

//                                branchLokasi = movieObject.getString("idLokasi");
//                                department = movieObject.getString("nama_departement");

                                txt_alpha.setVisibility(View.INVISIBLE);
                                text_jabatan.setVisibility(View.INVISIBLE);
                                txt_lokasi.setVisibility(View.INVISIBLE);
                                text_perusahaan.setVisibility(View.INVISIBLE);

                                String nik = movieObject.getString("nik_baru");

                                int beta = Integer.parseInt(txt_alpha.getText().toString());
                                if (text_jabatan.getText().toString().equals("jabatan")) {
                                    getnama();
                                } else if (4 <= beta && beta <= 10) {
                                    getDetail();
                                }

                                if (4 <= beta && beta <= 7) {
                                    DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");
                                    Date date = new Date();
                                    System.out.println("jam = " + dateFormat.format(date));

                                    String dateStr = "20:00:00";
                                    SimpleDateFormat dateForm = new SimpleDateFormat("kk:mm:ss");
                                    Date convertedDate = new Date();
                                    try {
                                        convertedDate = dateForm.parse(dateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    DateFormat dateFormat2 = new SimpleDateFormat("kk:mm:ss");

                                    String actual = dateFormat.format(date);
                                    String limit = dateFormat2.format(convertedDate);

                                    String[] parts = actual.split(":");
                                    Calendar cal1 = Calendar.getInstance();
                                    cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                                    cal1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                                    cal1.set(Calendar.SECOND, Integer.parseInt(parts[2]));

                                    parts = limit.split(":");
                                    Calendar cal2 = Calendar.getInstance();
                                    cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                                    cal2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                                    cal2.set(Calendar.SECOND, Integer.parseInt(parts[2]));

                                    cal2.add(Calendar.DATE, 1);

                                    if (cal1.after(cal2)) {
                                        Date c = Calendar.getInstance().getTime();
                                        System.out.println("Current time => " + c);

                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        String formattedDate = df.format(c);
                                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/Absenmobile/index_keterangan?nik_baru=" + nik + "&tanggal=" + formattedDate,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {

                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

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

                                        stringRequest2.setRetryPolicy(
                                                new DefaultRetryPolicy(
                                                        500000,
                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                ));
                                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                                        requestQueue2.add(stringRequest2);
                                    }
                                }

//                                if(!text_jabatan.getText().toString().equals("jabatan")){
//                                        int alpha = Integer.parseInt(txt_alpha.getText().toString());
//                                        if (4 <= alpha && alpha <= 10) {
//                                            Thread t = new Thread() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        while (!isInterrupted()) {
//                                                            Thread.sleep(300000);
////                                                            7200000
//                                                            runOnUiThread(new Runnable() {
//                                                                @Override
//                                                                public void run() {
//                                                                    getDetail();
//                                                                    System.out.println("hasil =" + txt_alpha.getText().toString());
//                                                                }
//                                                            });
//                                                        }
//                                                    } catch (InterruptedException e) {
//                                                    }
//                                                }
//                                            };
//                                            t.start();
//                                        }
//                                }

                                if ("1".equalsIgnoreCase(txt_alpha.getText().toString())) {
                                    jumlahizin.setVisibility(View.INVISIBLE);
                                    jumlahdinas.setVisibility(View.INVISIBLE);
//                                    jumlahcuti.setVisibility(View.INVISIBLE);
                                } else if ("2".equalsIgnoreCase(txt_alpha.getText().toString())) {
                                    jumlahizin.setVisibility(View.INVISIBLE);
                                    jumlahdinas.setVisibility(View.INVISIBLE);
//                                    jumlahcuti.setVisibility(View.INVISIBLE);
                                } else if ("3".equalsIgnoreCase(txt_alpha.getText().toString())) {
                                    jumlahizin.setVisibility(View.INVISIBLE);
                                    jumlahdinas.setVisibility(View.INVISIBLE);
//                                    jumlahcuti.setVisibility(View.INVISIBLE);
                                }

                                movieItemList.add(movieItem);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                )
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getDetail() {
        String lokasi = text_jabatan.getText().toString();
        if (!lokasi.equals("jabatan")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/izin_full_day/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number1 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    statusizinfullday movieItem = new statusizinfullday(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_full_day"));

                                    if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_full_day").contains("0") && movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString()))
                                            number1++;
                                        {
                                            izincount.setText(String.valueOf(number1));
                                        }
                                    } else if (txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_full_day").contains("0"))
                                            number1++;
                                        {
                                            izincount.setText(String.valueOf(number1));

                                        }
                                    }
                                    movieItemList1.add(movieItem);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            izincount.setText("0");
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

            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/izin_non_full_day/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number2 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    statusizinnonfullday movieItem = new statusizinnonfullday(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_non_full"));

                                    if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_non_full").contains("0") && movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString()))
                                            number2++;
                                        {
                                            izinnoncount.setText(String.valueOf(number2));
                                        }
                                    } else if (txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_non_full").contains("0"))
                                            number2++;
                                        {
                                            izinnoncount.setText(String.valueOf(number2));
                                        }
                                    }
                                    movieItemList2.add(movieItem);
                                }
                                jumlahizin();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            jumlahizin();
                            izinnoncount.setText("0");
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

            StringRequest stringRequest3 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/dinas_full_day/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number3 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    statusdinasfullday movieItem = new statusdinasfullday(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_full_day"));

                                    if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_full_day").contains("0") && movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString()))
                                            number3++;
                                        {
                                            dinascount.setText(String.valueOf(number3));
                                        }
                                    } else if (txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_full_day").contains("0"))
                                            number3++;
                                        {
                                            dinascount.setText(String.valueOf(number3));
                                        }
                                    }
                                    movieItemList3.add(movieItem);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dinascount.setText("0");
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

            StringRequest stringRequest4 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/dinas_non_full_day/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number4 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    final statusdinasnonfullday movieItem = new statusdinasnonfullday(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_non_full"));

                                    if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_non_full").contains("0") && movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString()))
                                            number4++;
                                        {
                                            dinasnoncount.setText(String.valueOf(number4));
                                        }
                                    } else if (txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_non_full").contains("0"))
                                            number4++;
                                        {
                                            dinasnoncount.setText(String.valueOf(number4));
                                        }
                                    }

                                    movieItemList4.add(movieItem);
                                }
                                jumlahdinas();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            jumlahdinas();
                            dinasnoncount.setText("0");
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

            StringRequest stringRequest5 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/cuti_tahunan/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number5 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {
                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    statuscutitahunan movieItem = new statuscutitahunan(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_cuti_tahunan"));

                                    if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_cuti_tahunan").contains("0") && movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString()))
                                            number5++;
                                        {
                                            cutitahunancount.setText(String.valueOf(number5));
                                        }
                                    } else if (txt_lokasi.getText().toString().equals("Pusat")) {
                                        if (movieObject.getString("status_cuti_tahunan").contains("0"))
                                            number5++;
                                        {
                                            cutitahunancount.setText(String.valueOf(number5));
                                        }
                                    }

                                    movieItemList5.add(movieItem);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            cutitahunancount.setText("0");
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

            StringRequest stringRequest6 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/cuti_khusus/index_atasan?jabatan_struktur=" + lokasi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray movieArray = obj.getJSONArray("data");
                                int number6 = 0;

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    statuscutikhusus movieItem = new statuscutikhusus(
                                            movieObject.getString("lokasi_struktur"),
                                            movieObject.getString("status_cuti_khusus"));

                                    if (movieObject.getString("status_cuti_khusus").contains("0")) {
                                        if (!txt_lokasi.getText().toString().equals("Pusat")) {
                                            if (movieObject.getString("lokasi_struktur").equals(txt_lokasi.getText().toString())) {
                                                number6++;
                                                cutikhususcount.setText(String.valueOf(number6));
                                            }
                                        } else {
                                            number6++;
                                            cutikhususcount.setText(String.valueOf(number6));
                                        }
                                    }


                                    movieItemList6.add(movieItem);
                                }
                                jumlahcuti();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            jumlahcuti();
                            cutikhususcount.setText("0");
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
                    )
            );
            stringRequest2.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            stringRequest3.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            stringRequest4.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            stringRequest5.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            stringRequest6.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
            requestQueue.add(stringRequest2);
            requestQueue.add(stringRequest3);
            requestQueue.add(stringRequest4);
            requestQueue.add(stringRequest5);
            requestQueue.add(stringRequest6);

        }
    }

    private void postNotifIzinNonFullDay() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Izin Non Full Day Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    private void postNotifIzinFullDay() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_token?no_jabatan_karyawan=" + txt_nomor.getText().toString() + "&lokasi_hrd=" + txt_lokasi.getText().toString(),
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


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    private void postNotifikasi(String device_token) {
                        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/Push_Notification/push_notif_eis.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

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

                                params.put("device", device_token);
                                params.put("body", "Pengajuan Izin Full Day Atas Nama " + txt_nama.getText().toString() + " Siap Untuk Di Approve");

                                System.out.println("notif = " + params);


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
                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
                        requestQueue2.add(stringRequest2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    @Override
    protected void onPause() {
        super.onPause();
//        int beta = Integer.parseInt(txt_alpha.getText().toString());
//
//        if (4 <= beta && beta <= 10) {
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("restartservice");
//            broadcastIntent.setClass(this, Restarter.class);
//            this.sendBroadcast(broadcastIntent);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        image_place_holder.stopAutoCycle();
//        int beta = Integer.parseInt(txt_alpha.getText().toString());
//
//        if (4 <= beta && beta <= 10) {
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("restartservice");
//            broadcastIntent.setClass(this, Restarter.class);
//            this.sendBroadcast(broadcastIntent);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reloadNeed) {
            getStatus();
        }

        this.reloadNeed = false;

        getnama();
        getDetail();

        statusUpdate = "lanjutkan";

    }

    private void getStatus() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index?nik_baru=" + nik_baru,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {

                                JSONObject movieObject = movieArray.getJSONObject(i);


                                if (movieObject.getString("status_update").equals("0")) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            menu.this);
                                    alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.icon));
                                    alertDialogBuilder
                                            .setTitle("Perhatian")
                                            .setMessage(Html.fromHtml("Anda belum melengkapi <b>Data Karyawan</b>. Silahkan lakukan update dan lengkapi data tersebut"))
                                            .setIcon(R.drawable.icon)
                                            .setCancelable(false)
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(menu.this, biodata.class);
                                                    startActivity(intent);
                                                    reloadNeed = true;
                                                }
                                            })
                                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    finish();
                                                    finishAffinity();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();


                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                )
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        getDetail();
    }

    private void jumlahizin() {
        int num1 = Integer.valueOf(izinnoncount.getText().toString());
        int num2 = Integer.valueOf(izincount.getText().toString());
        int sum = num1 + num2;
        System.out.println("angka1 =" + num1);
        System.out.println("angka2 =" + num2);

        System.out.println("angka =" + sum);
        jumlahizin.setText(Integer.toString(sum));

    }

    private void jumlahdinas() {
        int num3 = (int) Double.parseDouble(dinascount.getText().toString());
        int num4 = (int) Double.parseDouble(dinasnoncount.getText().toString());

        int sum2 = num3 + num4;
        jumlahdinas.setText(Integer.toString(sum2));

    }

    private void jumlahcuti() {
        int num5 = (int) Double.parseDouble(cutikhususcount.getText().toString());
        int num6 = (int) Double.parseDouble(cutitahunancount.getText().toString());

        int sum3 = num5 + num6;
        System.out.println("Test Cuti =" + sum3);
//        jumlahcuti.setText(Integer.toString(sum3));

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Anda yakin untuk Keluar ?");
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void getToken(final String s, final String szBranch) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }

                //Token
                String token = task.getResult();
                updateDeviceId(s, szBranch, token);

                Log.d(TAG, "onComplete: " + token);
            }
        });
    }

    private void updateDeviceId(final String lokasi, final String szBranch, final String token) {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/master/Notifikasi/index_cekDeviceId",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

                String appVersion = BuildConfig.VERSION_NAME;

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("nik_baru", string_nip_karyawan);
                params.put("nama_karyawan", string_nama_karyawan);
                params.put("kode_cabang", "1");

                params.put("lokasi", string_lokasi_karyawan);
                params.put("device_brand", Build.BRAND);
                params.put("device_model", Build.MODEL);
                params.put("device_sdk", String.valueOf(Build.VERSION.SDK_INT));
                params.put("device_version", Build.VERSION.RELEASE);
                params.put("apps_version", appVersion);

                params.put("apps_last_open", currentDateandTime2);
                params.put("device_token", token);


                return params;
            }

        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue requestQueue2 = Volley.newRequestQueue(menu.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }


    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

//    private void get_training_badge() {
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://203.100.57.36/project/ess-api-android-bt/rest_server/api/training/index_get_training?nama_pegawai=" + string_nama_karyawan + "&no_urut_pegawai=" + string_no_urut_karyawan + "&nip_pegawai=" + string_nip_karyawan + "&id_jabatan_pegawai=" + string_id_jabatan,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//
//                            // Ambil nilai total_data dari JSON
//                            int totalData = obj.getInt("total_data");
//
//                            // Set total_data sebagai teks di notificationBadge
//                            notificationBadge.setText(String.valueOf(totalData));
//
//                            // Opsional: Sembunyikan badge jika total_data = 0
//                            if (totalData == 0) {
//                                notificationBadge.setVisibility(View.GONE);
//                            } else {
//                                notificationBadge.setVisibility(View.VISIBLE);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", "admin", "Dev24");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
//        stringRequest.setRetryPolicy(
//                new DefaultRetryPolicy(
//                        10000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                ));
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }

    private void get_training_badge(int savedTotalData) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://203.100.57.36/project/ess-api-android-bt/rest_server/api/training/index_get_training?nama_pegawai=" + string_nama_karyawan + "&no_urut_pegawai=" + string_no_urut_karyawan + "&nip_pegawai=" + string_nip_karyawan + "&id_jabatan_pegawai=" + string_id_jabatan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            int totalData = obj.getInt("total_data");

                            // Jika totalData dari API lebih besar dari savedTotalData, berarti ada notifikasi baru
                            if (totalData > savedTotalData) {
                                int newNotifications = totalData - savedTotalData;
                                notificationBadge.setText(String.valueOf(newNotifications));
                                notificationBadge.setVisibility(View.VISIBLE); // Tampilkan badge dengan jumlah notifikasi baru
                            } else {
                                notificationBadge.setVisibility(View.GONE); // Tidak ada notifikasi baru, sembunyikan badge
                            }

                            total_data_api_pemberitahuan = String.valueOf(totalData);

                            // Update savedTotalData dengan totalData terbaru
//                            editor.putInt("totalData", totalData);
//                            editor.apply();

//                            savedTotalData_API


                            System.out.println("savedTotalData_API " + totalData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showPemberitahuanDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pemberitahuan_kelengkapan_data);
        dialog.setCancelable(true);

        TextView tvPesan = dialog.findViewById(R.id.tv_pesan);
        CheckBox cbJanganTampilkanLagi = dialog.findViewById(R.id.cb_jangan_tampilkan_lagi);
        Button btnNanti = dialog.findViewById(R.id.btn_nanti);
        Button btnLengkapi = dialog.findViewById(R.id.btn_lengkapi);

        // Mengatur margin dialog agar memiliki margin 16dp di kiri dan kanan
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.horizontalMargin = 0.05f;  // Mengatur margin 16dp sesuai density
            window.setAttributes(layoutParams);
        }

        // Button "Nanti"
        btnNanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Button "Lengkapi data sekarang"
        btnLengkapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke Activity untuk lengkapi data
                Intent intent = new Intent(menu.this, biodata.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        // Checkbox untuk jangan tampilkan lagi
        cbJanganTampilkanLagi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Simpan pilihan pengguna di SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(KEY_SHOW_DIALOG, false);
                editor.apply();
            }
        });

        dialog.show();
    }

    public void get_kelengkapan_data_pegawai() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://203.100.57.36/project/ess-api-android-bt/rest_server/api/pemberitahuan/index_get_kelengkapan_data_pegawai?no_urut_pegawai=" + string_no_urut_karyawan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.optJSONArray("data");

                            if (movieArray != null && movieArray.length() > 0) {
                                JSONObject movieObject = movieArray.getJSONObject(0);

                                // Ambil data-data yang dibutuhkan
                                String data_induk = movieObject.getString("data_induk");
                                String data_detail = movieObject.getString("data_detail");
                                String data_keluarga = movieObject.getString("data_keluarga");
                                String data_pendidikan = movieObject.getString("data_pendidikan");
                                String data_pelatihan = movieObject.getString("data_pelatihan");
                                String data_pengalaman_kerja = movieObject.getString("data_pengalaman_kerja");

                                // Cek jika ada data yang bernilai "0"
                                if ("0".equals(data_induk) || "0".equals(data_detail) || "0".equals(data_keluarga) ||
                                        "0".equals(data_pendidikan) || "0".equals(data_pelatihan) || "0".equals(data_pengalaman_kerja)) {

                                    // Tampilkan dialog pemberitahuan jika ada data yang belum lengkap

                                    final Dialog dialog = new Dialog(menu.this);
                                    dialog.setContentView(R.layout.dialog_pemberitahuan_kelengkapan_data);
                                    dialog.setCancelable(true);

                                    TextView tvPesan = dialog.findViewById(R.id.tv_pesan);
                                    CheckBox cbJanganTampilkanLagi = dialog.findViewById(R.id.cb_jangan_tampilkan_lagi);
                                    Button btnNanti = dialog.findViewById(R.id.btn_nanti);
                                    Button btnLengkapi = dialog.findViewById(R.id.btn_lengkapi);

                                    Window window = dialog.getWindow();
                                    if (window != null) {
                                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        WindowManager.LayoutParams layoutParams = window.getAttributes();
                                        layoutParams.horizontalMargin = 0.05f;
                                        window.setAttributes(layoutParams);
                                    }

                                    // Button "Nanti"
                                    btnNanti.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    // Button "Lengkapi data sekarang"
                                    btnLengkapi.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(menu.this, biodata.class);

                                            // Tambahkan data yang bernilai "0" ke Intent
                                            intent.putExtra("data_induk", "0".equals(data_induk) ? "0" : "");
                                            intent.putExtra("data_detail", "0".equals(data_detail) ? "0" : "");
                                            intent.putExtra("data_keluarga", "0".equals(data_keluarga) ? "0" : "");
                                            intent.putExtra("data_pendidikan", "0".equals(data_pendidikan) ? "0" : "");
                                            intent.putExtra("data_pelatihan", "0".equals(data_pelatihan) ? "0" : "");
                                            intent.putExtra("data_pengalaman_kerja", "0".equals(data_pengalaman_kerja) ? "0" : "");

                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    });

                                    cbJanganTampilkanLagi.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                        if (isChecked) {
                                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                                            editor.putBoolean(KEY_SHOW_DIALOG, false);
                                            editor.apply();
                                        }
                                    });

                                    dialog.show();

                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Data pegawai tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                        Log.e("API Error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showCustomDialog() {
        // Inflate custom layout untuk dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_menu_belum_tersedia, null);

        // Buat AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);  // Set custom layout

        // Inisialisasi dialog dan tombol OK
        AlertDialog alertDialog = dialogBuilder.create();
        Button btnOk = dialogView.findViewById(R.id.button_ok);

        // Set klik listener untuk tombol OK
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();  // Tutup dialog
            }
        });

        // Tampilkan dialog
        alertDialog.show();
    }

}



