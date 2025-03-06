package com.project.bbt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_location_longlat extends FragmentActivity implements OnMapReadyCallback {
    private LocationCallback locationCallback;
    private static final String URL = "https://ess.banktanah.id/bbt_api/rest_server/api/absensi/index_get_longlat_user"; // Ganti dengan URL API lu
    private static final double RADIUS = 100.0; // Radius dalam meter
    String str_latitude, str_longitude;
    TextView longlat_user;
    private MapView mapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    Button bt_absen_check;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    String string_nip_karyawan, string_lokasi_karyawan,
            string_jabatan_karyawan, string_nama_karyawan,
            string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;

    SharedPreferences sharedPreferences;

    private TextView tvTime, tvDate, tv_nama_karyawan, tv_nip_karyawan;
    Button bt_getin;
    TextView tv_date_getin, tv_date_getout;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    ContentValues cv;
    Uri imageUri;
    ImageView img_background_foto_user;
    RelativeLayout relative_background_foto_user;
    Bitmap bitmap_user_photo;
    Button bt_submit_dokumen;
    String str_getin_or_getout;
    private TextView tv_time_getin, tv_time_getout, tv_longlat, tv_jabatan, tv_divisi,
    tv_latitude, tv_longitude;
    ProgressDialog pDialog;
    String formattedDate;
    String str_only_date;
    String getin, getout;
    String noUrut,status,idlokasi_karyawan_absen;
    TextView tv_near_location;

    private AlertDialog progressDialog;
    private static final int MY_PERMISSIONS_REQUEST = 100; // Request code izin


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_location_longlat);
        NukeSSLCerts.nuke();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        //Ambil data yang dikirim dari intent
        getin = getIntent().getStringExtra("getin");
        getout = getIntent().getStringExtra("getout");

        //Gunakan data tersebut sesuai kebutuhan, misalnya ditampilkan di log:
        Log.d("IntentData", "getin: " + getin + ", getout: " + getout);

        tv_near_location = findViewById(R.id.tv_near_location);

        bt_absen_check = findViewById(R.id.id_testing_longlat);
        longlat_user = findViewById(R.id.longlat_user);

        relative_background_foto_user = findViewById(R.id.relative_background_foto_user);
        img_background_foto_user = findViewById(R.id.img_background_foto_user);

        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        bt_getin = findViewById(R.id.bt_getin);
        tv_date_getin = findViewById(R.id.tv_date_getin);
        tv_date_getout = findViewById(R.id.tv_date_getout);
        tv_nama_karyawan = findViewById(R.id.tv_name);
        tv_nip_karyawan = findViewById(R.id.tv_id);
        tv_jabatan = findViewById(R.id.tv_jabatan);
        tv_divisi = findViewById(R.id.tv_divisi);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);

        tv_time_getin = findViewById(R.id.tv_time_getin);
        tv_time_getout = findViewById(R.id.tv_time_getout);

        // Inisialisasi TextView untuk longlat
        tv_longlat = findViewById(R.id.tv_longlat);
        tv_nama_karyawan.setText(string_nama_karyawan);
        tv_nip_karyawan.setText(string_nip_karyawan);
        tv_jabatan.setText(string_jabatan_karyawan);
        tv_divisi.setText(string_nama_divisi);

        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Dapatkan lokasi terakhir yang diketahui
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Date date = new Date();
        // Membuat SimpleDateFormat dengan pola yang diinginkan
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat only_date = new SimpleDateFormat("yyyy-MM-dd");

        // Memformat tanggal saat ini
        formattedDate = sdf.format(date);
        str_only_date = only_date.format(date);

        // Cek nilai getin dan getout
        if (getin == null) {
            getin = "0"; // Set default value jika null
        }
        if (getout == null) {
            getout = "0"; // Set default value jika null
        }

        // Log nilai untuk debugging
        Log.d("DEBUG", "getin: " + getin + ", getout: " + getout);

        // Cek kondisi
        if ("1".equals(getin) && "0".equals(getout)) {
            bt_absen_check.setText("CHECK IN");

        } else if ("1".equals(getin) && "1".equals(getout)) {
            bt_absen_check.setText("CHECK OUT");

        } else {
            bt_absen_check.setText("UNKNOWN STATE");
        }

        get_lokasi_karyawan();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {

            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Tambahkan marker di lokasi user
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Lokasi Saya"));

                // Setelah mendapat lokasi user, bisa langsung panggil fetchLocationData()
                tv_latitude.setText(String.valueOf(location.getLatitude()));
                tv_longitude.setText(String.valueOf(location.getLongitude()));

                bt_absen_check.setOnClickListener(v -> {
                    // Deteksi Fake GPS
                    if (location.isFromMockProvider()) {
                        Toast.makeText(this, "Fake GPS detected! Lokasi tidak valid.", Toast.LENGTH_SHORT).show();
                        // Blokir atau logout user (sesuai kebutuhan)
                        return;

                    } else {
                        fetchLocationData(location.getLatitude(), location.getLongitude());
                    }
                });
            }
        });
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Jari-jari bumi dalam kilometer

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Konversi ke meter
    }

    private void fetchLocationData_radiusOld(double userLatitude, double userLongitude) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        double minDistance = Double.MAX_VALUE;
                        String nearestLocationName = "";
                        boolean isInRadius = false;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject locationObject = jsonArray.getJSONObject(i);
                            double locationLatitude = locationObject.getDouble("latitude");
                            double locationLongitude = locationObject.getDouble("longitude");
                            String locationName = locationObject.getString("namaLokasi");
                            String locationRadius = locationObject.getString("radiusAbsen");

                            // Hitung jarak menggunakan Haversine
                            double distance = haversine(userLatitude, userLongitude, locationLatitude, locationLongitude);

                            // Cek jarak untuk menentukan lokasi terdekat
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestLocationName = locationName;
                            }
                        }

                        // Cek apakah lokasi terdekat berada di dalam radius yang ditentukan
                        isInRadius = minDistance <= RADIUS;

                        // Tampilkan pesan atau dialog sesuai dengan status isInRadius
                        if (isInRadius) {

                            Toast.makeText(menu_location_longlat.this, "Dekat dengan: " + nearestLocationName, Toast.LENGTH_SHORT).show();

                            tv_near_location.setText(nearestLocationName);

                            if (getin.equals("1") && getout.equals("0")) {

                                if (img_background_foto_user.getDrawable() == null) {

                                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {

                                        cv = new ContentValues();
                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
                                        startActivityForResult(intent, 1);

                                    } else {

                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    post_get_in();
                                }

                            } else if (getin.equals("1") && getout.equals("1")){

                                if (img_background_foto_user.getDrawable() == null) {

                                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {

                                        cv = new ContentValues();
                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(intent, 1);

                                    } else {

                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    post_get_out();
                                }
                            }

                        } else {
                            //JIKA DILUAR RADIUS
                            showOutOfRadiusDialog(nearestLocationName);
                        }

                        Log.d("NEAREST_LOCATION", "Lokasi terdekat: " + nearestLocationName + " dengan jarak: " + minDistance + " meter");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY_ERROR", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + android.util.Base64.encodeToString(creds.getBytes(), android.util.Base64.DEFAULT);
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

    private void fetchLocationData(double userLatitude, double userLongitude) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        double minDistance = Double.MAX_VALUE;
                        String nearestLocationName = "";
                        double nearestRadius = 0; // Menyimpan radius dari lokasi terdekat
                        boolean isInRadius = false;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject locationObject = jsonArray.getJSONObject(i);
                            double locationLatitude = locationObject.getDouble("latitude");
                            double locationLongitude = locationObject.getDouble("longitude");
                            String locationName = locationObject.getString("namaLokasi");
                            double locationRadius = locationObject.getDouble("radiusAbsen"); // Ambil radius dari JSON

                            // Hitung jarak menggunakan Haversine
                            double distance = haversine(userLatitude, userLongitude, locationLatitude, locationLongitude);

                            // Cek jarak untuk menentukan lokasi terdekat
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestLocationName = locationName;
                                nearestRadius = locationRadius; // Simpan radius lokasi terdekat
                            }
                        }

                        // Cek apakah lokasi terdekat berada di dalam radius yang ditentukan
                        isInRadius = minDistance <= nearestRadius;

                        // Tampilkan pesan atau dialog sesuai dengan status isInRadius
                        if (isInRadius) {

                            Toast.makeText(menu_location_longlat.this, "Dekat dengan: " + nearestLocationName, Toast.LENGTH_SHORT).show();
                            tv_near_location.setText(nearestLocationName);

                            if (getin.equals("1") && getout.equals("0")) {

                                if (img_background_foto_user.getDrawable() == null) {

//                                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {
//
//                                        cv = new ContentValues();
//                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
//                                        startActivityForResult(intent, 1);
//
//                                    } else {
//
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

//--------------------------------------------------------------------------------------------------------------------------------

//                                    if (checkPermissions(menu_location_longlat.this)) {
//                                        cv = new ContentValues();
//                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

                                    //-------------------------------- SUDAH BISA, TAPI ADNROID 13 KEATAS GABISA------------------------------

//                                    if (checkPermission(menu_location_longlat.this)) {
//                                        ContentValues cv = new ContentValues();
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                            // ✅ ANDROID 10+ (Tanpa WRITE_EXTERNAL_STORAGE)
//                                            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                                            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppFolder");
//
//                                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                        } else {
//                                            // ✅ ANDROID 9 KE BAWAH (Pakai WRITE_EXTERNAL_STORAGE + FileProvider)
//                                            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture.jpg");
////                                          imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.fileprovider", imageFile);
//                                            imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                        }
//
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

                                    //--------------------------------------------------------------------------------------------

                                    //dicomment dulu untuk infininx 24feb25
//                                    if (checkPermission(menu_location_longlat.this)) {
//                                        ContentValues cv = new ContentValues();
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                            // ✅ ANDROID 10+ (Tanpa WRITE_EXTERNAL_STORAGE)
//                                            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                                            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppFolder");
//
//                                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                            if (imageUri == null) {
//                                                // Gagal dapat URI, coba pakai penyimpanan Private
//                                                File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                                imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                            }
//
//                                        } else {
//                                            // ✅ ANDROID 9 KE BAWAH (Pakai WRITE_EXTERNAL_STORAGE + FileProvider)
//                                            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture.jpg");
//                                            imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                        }
//
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

                                    if (checkPermission(menu_location_longlat.this)) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture_" + System.currentTimeMillis() + ".jpg");
                                        imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                        startActivityForResult(intent, 1);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    post_get_in();
                                }

                            } else if (getin.equals("1") && getout.equals("1")){

                                if (img_background_foto_user.getDrawable() == null) {

//                                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {
//
//                                        cv = new ContentValues();
//                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        startActivityForResult(intent, 1);
//
//                                    } else {
//
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

                                    //-----------------------------------------------------------------

//                                    if (checkPermissions(menu_location_longlat.this)) {
//                                        cv = new ContentValues();
//                                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }



                                    //---------------------------------------- BISA, TAPI UNTUK ANDROID 13 KEATAS GABISA

//                                    if (checkPermission(menu_location_longlat.this)) {
//                                        ContentValues cv = new ContentValues();
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                            // ✅ ANDROID 10+ (Tanpa WRITE_EXTERNAL_STORAGE)
//                                            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                                            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppFolder");
//
//                                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//                                        } else {
//                                            // ✅ ANDROID 9 KE BAWAH (Pakai WRITE_EXTERNAL_STORAGE + FileProvider)
//                                            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture.jpg");
////                                            imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.fileprovider", imageFile);
//                                            imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                        }
//
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }

                                    //----------------------------------------------------------------------------------------------------------------

                                    //dicomment dulu untuk infininx 24feb25
//                                    if (checkPermission(menu_location_longlat.this)) {
//                                        ContentValues cv = new ContentValues();
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                            // ✅ ANDROID 10+ (Tanpa WRITE_EXTERNAL_STORAGE)
//                                            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                                            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppFolder");
//
//                                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                            if (imageUri == null) {
//                                                // Gagal dapat URI, coba pakai penyimpanan Private
//                                                File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture_" + System.currentTimeMillis() + ".jpg");
//                                                imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                            }
//
//                                        } else {
//                                            // ✅ ANDROID 9 KE BAWAH (Pakai WRITE_EXTERNAL_STORAGE + FileProvider)
//                                            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture.jpg");
//                                            imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);
//                                        }
//
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                        startActivityForResult(intent, 1);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                    }


                                    if (checkPermission(menu_location_longlat.this)) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture_" + System.currentTimeMillis() + ".jpg");
                                        imageUri = FileProvider.getUriForFile(menu_location_longlat.this, "com.project.bbt.provider", imageFile);

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                        startActivityForResult(intent, 1);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    post_get_out();
                                }
                            }

                        } else {
                            // JIKA DILUAR RADIUS
                            showOutOfRadiusDialog(nearestLocationName);
                        }

                        Log.d("NEAREST_LOCATION", "Lokasi terdekat: " + nearestLocationName + " dengan jarak: " + minDistance + " meter, radius: " + nearestRadius);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY_ERROR", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + android.util.Base64.encodeToString(creds.getBytes(), android.util.Base64.DEFAULT);
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

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        android.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void showOutOfRadiusDialog(String locationName) {
        new AlertDialog.Builder(this)
                .setTitle("Peringatan")
                .setMessage("Anda berada di luar radius lokasi " + locationName)

                .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (getin.equals("1") && getout.equals("0")) {
//
//                            if (img_background_foto_user.getDrawable() == null) {
//
//                                if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {
//
//                                    cv = new ContentValues();
//                                    cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                    cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                    startActivityForResult(intent, 1);
//
//                                } else {
//
//                                    Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                }
//
//                            } else {
//                                post_get_in();
//                            }
//
//                        } else if (getin.equals("1") && getout.equals("1")) {
//
//                            if (img_background_foto_user.getDrawable() == null) {
//
//                                if (checkPermissionREAD_EXTERNAL_STORAGE(menu_location_longlat.this)) {
//
//                                    cv = new ContentValues();
//                                    cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                                    cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                    startActivityForResult(intent, 1);
//
//                                } else {
//
//                                    Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                                }
//
//                            } else {
//                                post_get_out();
//                            }
//                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aksi batal
                        dialog.dismiss();
                    }
                })
                .show();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission diberikan, muat ulang peta
//                onMapReady(mMap);
//            } else {
//                Toast.makeText(this, "Izin lokasi diperlukan untuk menampilkan peta", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Handle izin lokasi
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk menampilkan peta", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == MY_PERMISSIONS_REQUEST) {
            // Handle izin Kamera & Storage
            boolean cameraAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean storageAccepted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (cameraAccepted && storageAccepted) {
                Toast.makeText(this, "Izin kamera & penyimpanan diberikan!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin kamera atau penyimpanan ditolak!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void get_lokasi_karyawan() {
        showCustomDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/Absen_manual2/index_lokasi_absen_karyawan?noUrut=" + string_no_urut_karyawan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideCustomDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            noUrut = jsonObject1.getString("noUrut");
                            status = jsonObject1.getString("status");
                            idlokasi_karyawan_absen = jsonObject1.getString("idlokasi");

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
                        hideCustomDialog();
                        Toast.makeText(menu_location_longlat.this, "Lokasi Tidak Ditemukan", Toast.LENGTH_SHORT).show();

//                        Toast.makeText(menu_getin_getout.this, "TEST", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void post_get_in() {
//        pDialog = new ProgressDialog(this);
//        pDialog.setContentView(R.layout.progress_dialog);
//        pDialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent
//        );

        showCustomDialog();

        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/api/absensi/insert_absen_get_in",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        str_getin_or_getout = "0";
                        upload_foto_user();
                        hideCustomDialog();

                        SweetAlertDialog Success = new SweetAlertDialog(menu_location_longlat.this, SweetAlertDialog.SUCCESS_TYPE);
                        Success.setContentText("Data Sudah Ditambahkan");
                        Success.setCancelable(false);
                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        Success.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideCustomDialog();

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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("nourut_karyawan", string_no_urut_karyawan);
                params.put("nip_karyawan", string_nip_karyawan);
                params.put("idlokasi", idlokasi_karyawan_absen);

                params.put("checktime", formattedDate);
                params.put("long", tv_longitude.getText().toString());
                params.put("lat", tv_latitude.getText().toString());
                params.put("userCreate", string_nip_karyawan);
                params.put("userDateCreate", formattedDate);
                params.put("url_foto", "https://ess.banktanah.id/bbt_api/rest_server/image/upload_absen_user/" + "IMG_"+ string_no_urut_karyawan + "_" + str_getin_or_getout + "-" + currentDateandTime2 + ".jpeg");

                return params;
            }
        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }

    private void post_get_out() {
//        pDialog = new ProgressDialog(this);
//        pDialog.setContentView(R.layout.progress_dialog);
//        pDialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent
//        );
//
//        showDialog();

        showCustomDialog();

        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/api/absensi/insert_absen_get_out",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        str_getin_or_getout = "1";
                        upload_foto_user();

//                      hideDialog();

                        hideCustomDialog();

                        SweetAlertDialog Success = new SweetAlertDialog(menu_location_longlat.this, SweetAlertDialog.SUCCESS_TYPE);
                        Success.setContentText("Data Sudah Ditambahkan");
                        Success.setCancelable(false);
                        Success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                // Set result agar menu_getin_getout tahu untuk refresh data
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        Success.show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideCustomDialog();

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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime2 = sdf2.format(new Date());

                params.put("nourut_karyawan", string_no_urut_karyawan);
                params.put("nip_karyawan", string_nip_karyawan);
                params.put("idlokasi", idlokasi_karyawan_absen);

                params.put("checktime", formattedDate);
                params.put("long", tv_longitude.getText().toString());
                params.put("lat", tv_latitude.getText().toString());
                params.put("userCreate", string_nip_karyawan);
                params.put("userDateCreate", formattedDate);
                params.put("url_foto", "https://ess.banktanah.id/bbt_api/rest_server/image/upload_absen_user/" + "IMG_"+ string_no_urut_karyawan + "_" + str_getin_or_getout + "-" + currentDateandTime2 + ".jpeg");

                return params;
            }
        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {


            if (resultCode == Activity.RESULT_OK) {
                try {

                    bitmap_user_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    // Mendapatkan dimensi asli dari bitmap
                    int originalWidth = bitmap_user_photo.getWidth();
                    int originalHeight = bitmap_user_photo.getHeight();

                    // Menghitung rasio aspek dari gambar
                    float aspectRatio = (float) originalWidth / (float) originalHeight;

                    // Menetapkan lebar gambar yang diinginkan dan menghitung tinggi berdasarkan rasio aspek
                    int desiredWidth = 1080; // Anda bisa mengubah ini sesuai kebutuhan
                    int desiredHeight = (int) (desiredWidth / aspectRatio);

                    // Menskalakan gambar dengan kualitas tinggi
                    bitmap_user_photo = Bitmap.createScaledBitmap(bitmap_user_photo, desiredWidth, desiredHeight, true);

                    // Menetapkan bitmap ke ImageView
                    img_background_foto_user.setImageBitmap(bitmap_user_photo);

                    // Mendapatkan layout parameters dari ImageView
                    ViewGroup.LayoutParams paramktp = img_background_foto_user.getLayoutParams();

                    // Mengonversi ukuran ke DP (misalnya, lebar 200dp)
                    float sizeInDPWidth = 200;
                    float sizeInDPHeight = sizeInDPWidth / aspectRatio;

                    // Mengonversi ukuran dari DP ke piksel
                    int widthInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, sizeInDPWidth, getResources().getDisplayMetrics());
                    int heightInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, sizeInDPHeight, getResources().getDisplayMetrics());

                    // Menetapkan ukuran baru ke layout parameters
                    paramktp.width = widthInDp;
                    paramktp.height = heightInDp;
                    img_background_foto_user.setLayoutParams(paramktp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void upload_foto_user() {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://ess.banktanah.id/bbt_api/rest_server/php/upload_image_absen_user.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(menu_location_longlat.this, "BERHASIL", Toast.LENGTH_SHORT).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error nya: " + error.getMessage());
                        System.out.println("Error nya: " + error.getMessage());

                        String errorMessage = "Upload failed";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMessage = new String(error.networkResponse.data);
                        }
//                        Log.e("Error", "FAIL MASUK DN: " + errorMessage);

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

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime2 = sdf2.format(new Date());
                String gambar = ImageToString(bitmap_user_photo);

                params.put("nama_foto", "IMG_"+ string_no_urut_karyawan + "_" + str_getin_or_getout + "-" + currentDateandTime2);
                params.put("nama_folder", "upload_absen_user");
                params.put("foto", gambar);

                return params;
            }
        };

        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.setCancelable(false);
//        pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void hideCustomDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //NEW CONFIG FOR CAMERA 7 FEB 25 -------------------------------------
    // Cek izin CAMERA dan READ_EXTERNAL_STORAGE OLD
    public boolean checkPermissions_old(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST
                );
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

//    public boolean checkPermission(final Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> permissions = new ArrayList<>();
//
//            // Permission buat akses kamera
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.CAMERA);
//            }
//
//            // READ_EXTERNAL_STORAGE (dibutuhkan untuk ambil gambar dari storage)
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//
//            // WRITE_EXTERNAL_STORAGE (Cuma buat Android 9 ke bawah, API 28)
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                }
//            }
//
//            // Untuk Android 14+ (API 34) gunakan READ_MEDIA_IMAGES
//            if (Build.VERSION.SDK_INT >= 34) { // Android 14+
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//                }
//            }
//
//            // Kalau ada permission yang belum granted, request ke user
//            if (!permissions.isEmpty()) {
//                ActivityCompat.requestPermissions((Activity) activity, permissions.toArray(new String[0]), 100);
//                return false;
//            }
//        }
//        return true;
//    }

    //di comment dulu untuk infinix
//    public boolean checkPermission(final Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> permissions = new ArrayList<>();
//
//            // Permission buat akses kamera
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.CAMERA);
//            }
//
//            if (Build.VERSION.SDK_INT >= 34) { // Android 14+ (API 34 ke atas)
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//                }
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33)
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//                }
//            } else { // Android 12 ke bawah (API 32 ke bawah)
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//                }
//            }
//
//            // WRITE_EXTERNAL_STORAGE (Cuma buat Android 9 ke bawah, API 28)
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                }
//            }
//
//            // Kalau ada permission yang belum granted, request ke user
//            if (!permissions.isEmpty()) {
//                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), 100);
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean checkPermission(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();

            // Cek izin kamera
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            // Cek izin akses media di Android 13+ (API 33 ke atas)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                // Android 12 ke bawah butuh izin READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }

            // Kalau ada permission yang belum granted, request ke user
            if (!permissions.isEmpty()) {
                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), 100);
                return false;
            }
        }
        return true;
    }

}

