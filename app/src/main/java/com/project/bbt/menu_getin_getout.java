package com.project.bbt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_getin_getout extends AppCompatActivity {

    private TextView tvTime, tvDate, tv_nama_karyawan, tv_nip_karyawan;
    Button bt_getin;
    TextView tv_date_getin, tv_date_getout;
    private Handler handler = new Handler();
    private Runnable runnable;
    ProgressDialog pDialog;

    String string_nip_karyawan, string_lokasi_karyawan,
            string_jabatan_karyawan, string_nama_karyawan,
            string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;
    SharedPreferences sharedPreferences;
    String str_nip, str_checktime, str_checktype, str_long, str_lat, str_foto;
    String str_nip_out, str_checktime_out, str_checktype_out, str_long_out, str_lat_out, str_foto_out;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView tv_longlat, tv_time_getin, tv_time_getout;
    String formattedDate;
    String str_latitude, str_longitude, str_only_date;

    ContentValues cv;
    Uri imageUri;
    ImageView img_background_foto_user;
    RelativeLayout relative_background_foto_user;
    Bitmap bitmap_user_photo;
    Button bt_submit_dokumen;
    String str_getin_or_getout;

    Button btn_lihat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_getin_getout);
        NukeSSLCerts.nuke();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        relative_background_foto_user = findViewById(R.id.relative_background_foto_user);
        img_background_foto_user = findViewById(R.id.img_background_foto_user);

        str_getin_or_getout = null;

        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        bt_getin = findViewById(R.id.bt_getin);
        tv_date_getin = findViewById(R.id.tv_date_getin);
        tv_date_getout = findViewById(R.id.tv_date_getout);
        tv_nama_karyawan = findViewById(R.id.tv_name);
        tv_nip_karyawan = findViewById(R.id.tv_id);

        tv_time_getin = findViewById(R.id.tv_time_getin);
        tv_time_getout = findViewById(R.id.tv_time_getout);

        // Inisialisasi TextView untuk longlat
        tv_longlat = findViewById(R.id.tv_longlat);
        tv_nama_karyawan.setText(string_nama_karyawan);
        tv_nip_karyawan.setText(string_nip_karyawan);

        btn_lihat = findViewById(R.id.btn_lihat);

        // Simpan longlat yang diambil dari TextView

        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Date date = new Date();
        // Membuat SimpleDateFormat dengan pola yang diinginkan
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat only_date = new SimpleDateFormat("yyyy-MM-dd");

        // Memformat tanggal saat ini
        formattedDate = sdf.format(date);
        str_only_date = only_date.format(date);

        // Update time every second
        runnable = new Runnable() {
            @Override
            public void run() {
                // Get current time and date
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
                SimpleDateFormat dateFormatGetNew = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());  // New date format

                Date now = new Date();

                // Set time and date to TextViews
                tvTime.setText(timeFormat.format(now));
                tvDate.setText(dateFormat.format(now));
                tv_date_getin.setText(dateFormatGetNew.format(now));
                tv_date_getout.setText(dateFormatGetNew.format(now));

                handler.postDelayed(this, 1000);  // Update every second
            }
        };
        handler.post(runnable);  // Start the runnable

        // Cek permission sebelum ambil lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Dapatkan lokasi terakhir yang diketahui
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Update TextView dengan longlat terbaru
                            updateLongLatTextView(location);
                        }
                    }
                });

        // Buat request untuk update lokasi secara periodik
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update tiap 10 detik
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update TextView tiap kali ada perubahan lokasi
                    updateLongLatTextView(location);
                }
            }
        };

        // Mulai update lokasi secara periodik
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        get_absen_karyawan_in();

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            // Refresh fungsi Volley atau panggil onCreate untuk refresh
//            get_absen_karyawan_in(); // Ganti dengan fungsi Volley lu
//            // atau
//            // recreate(); // Untuk memanggil ulang onCreate
//        }
//    }

    // Fungsi untuk update TextView dengan latitude dan longitude
    private void updateLongLatTextView(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        tv_longlat.setText(latitude + ", " + longitude);
        str_latitude = String.valueOf(latitude);
        str_longitude = String.valueOf(longitude);

        // Klik tombol Lihat untuk membuka maps
        btn_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(menu_getin_getout.this, "OKE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(menu_getin_getout.this, menu_location_longlat.class);

                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

        bt_getin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Panggil metode checkLocationAndShowDialog
//                checkLocationAndShowDialog(latitude, longitude);

                if (tv_time_getin.getText().toString().equals("-")) {

                    Intent intent = new Intent(menu_getin_getout.this, menu_location_longlat.class);
                    intent.putExtra("getin", "1");
                    intent.putExtra("getout", "0");
                    startActivity(intent);
//                    if (img_background_foto_user.getDrawable() == null) {
//
//                        if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {
//
//                            cv = new ContentValues();
//                            cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                            cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                            startActivityForResult(intent, 1);
//
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        post_get_in();
//                    }

                } else {
                    Intent intent = new Intent(menu_getin_getout.this, menu_location_longlat.class);
                    intent.putExtra("getin", "1");
                    intent.putExtra("getout", "1");
                    startActivity(intent);
//                    if (img_background_foto_user.getDrawable() == null) {
//
//                        if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {
//
//                            cv = new ContentValues();
//                            cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                            cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                            startActivityForResult(intent, 1);
//
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        post_get_out();
//                    }
                }

            }
        });

    }

    private void post_get_in() {
        pDialog = new ProgressDialog(this);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        showDialog();

        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://36.88.110.134:27/bbt_api/rest_server/api/absensi/insert_absen_get_in",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        str_getin_or_getout = "0";
                        upload_foto_user();

                        hideDialog();
                        SweetAlertDialog Success = new SweetAlertDialog(menu_getin_getout.this, SweetAlertDialog.SUCCESS_TYPE);
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
                params.put("checktime", formattedDate);
                params.put("long", str_longitude);
                params.put("lat", str_latitude);
                params.put("userCreate", string_nip_karyawan);
                params.put("userDateCreate", formattedDate);
                params.put("url_foto", "http://36.88.110.134:27/bbt_api/rest_server/image/upload_absen_user/" + "IMG_"+ string_no_urut_karyawan + "_" + str_getin_or_getout + "-" + currentDateandTime2 + ".jpeg");

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
        pDialog = new ProgressDialog(this);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        showDialog();

        final StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://36.88.110.134:27/bbt_api/rest_server/api/absensi/insert_absen_get_out",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        str_getin_or_getout = "1";
                        upload_foto_user();

                        hideDialog();
                        SweetAlertDialog Success = new SweetAlertDialog(menu_getin_getout.this, SweetAlertDialog.SUCCESS_TYPE);
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
                params.put("checktime", formattedDate);
                params.put("long", str_longitude);
                params.put("lat", str_latitude);
                params.put("userCreate", string_nip_karyawan);
                params.put("userDateCreate", formattedDate);
                params.put("url_foto", "http://36.88.110.134:27/bbt_api/rest_server/image/upload_absen_user/" + "IMG_"+ string_no_urut_karyawan + "_" + str_getin_or_getout + "-" + currentDateandTime2 + ".jpeg");

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

    private void get_absen_karyawan_in() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/api/absensi/index_get_absen_getin?nip=" + string_nip_karyawan + "&checktime=" + str_only_date + "&typeInsert=" + "MOBILE", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            str_nip = jsonObject1.getString("nip");
                            str_checktime = jsonObject1.getString("checktime");
                            str_checktype = jsonObject1.getString("checktype");
                            str_long = jsonObject1.getString("long");
                            str_lat = jsonObject1.getString("lat");
                            str_foto = jsonObject1.getString("foto");

                            tv_time_getin.setText(str_checktime);

                        }

                        get_absen_karyawan_out();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        get_absen_karyawan_out();
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

    private void get_absen_karyawan_out() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/api/absensi/index_get_absen_getout?nip=" + string_nip_karyawan + "&checktime=" + str_only_date + "&typeInsert=" + "MOBILE", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            str_nip_out = jsonObject1.getString("nip");
                            str_checktime_out = jsonObject1.getString("checktime");
                            str_checktype_out = jsonObject1.getString("checktype");
                            str_long_out = jsonObject1.getString("long");
                            str_lat_out = jsonObject1.getString("lat");
                            str_foto_out = jsonObject1.getString("foto");

                            tv_time_getout.setText(str_checktime_out);

                        }

                        if (tv_time_getin.getText().toString().equals("-") && tv_time_getout.getText().toString().equals("-")) {
                            bt_getin.setText("CHECK IN");
                        } else if (!tv_time_getin.getText().toString().equals("-") && tv_time_getout.getText().toString().equals("-")) {
                            bt_getin.setText("CHECK OUT");
                        } else if (!tv_time_getin.getText().toString().equals("-") && !tv_time_getout.getText().toString().equals("-")) {

                            bt_getin.setText("CHECK OUT");
                            bt_getin.setEnabled(false);
                            bt_getin.setBackgroundColor(Color.GRAY);  // Ubah warna menjadi abu-abu
                            bt_getin.setTextColor(Color.WHITE);
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
                        if (tv_time_getin.getText().toString().equals("-") && tv_time_getout.getText().toString().equals("-")) {
                            bt_getin.setText("CHECK IN");
                        } else if (!tv_time_getin.getText().toString().equals("-") && tv_time_getout.getText().toString().equals("-")) {
                            bt_getin.setText("CHECK OUT");
                        } else if (!tv_time_getin.getText().toString().equals("-") && !tv_time_getout.getText().toString().equals("-")) {

                            bt_getin.setText("CHECK OUT");
                            bt_getin.setEnabled(false);
                            bt_getin.setBackgroundColor(Color.GRAY);  // Ubah warna menjadi abu-abu
                            bt_getin.setTextColor(Color.WHITE);
                        }

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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    // Ketika aplikasi ditutup, berhenti mendapatkan update lokasi
    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cek apakah data perlu diperbarui
        get_absen_karyawan_in();
        get_absen_karyawan_out();
//        recreate();
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
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
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
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(menu_getin_getout.this, "GET_ACCOUNTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Pastikan ini dipanggil di awal
        Log.d("ActivityResult", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);

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

        } else if (requestCode == 2) {
            // Logika untuk refresh data setelah kembali dari menu_location_longlat
            if (resultCode == Activity.RESULT_OK) {

                Log.d("ActivityResult", "Refreshing data after returning from menu_location_longlat");
                get_absen_karyawan_in();
                get_absen_karyawan_out();
                // atau
                // recreate(); // Untuk memanggil ulang onCreate
            }
        }
    }

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void upload_foto_user() {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://36.88.110.134:27/bbt_api/rest_server/php/upload_image_absen_user.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(menu_getin_getout.this, "BERHASIL", Toast.LENGTH_SHORT).show();
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

    private void checkLocationAndShowDialog(double userLatitude, double userLongitude) {
        // Patokan lokasi
        double referenceLatitude = -6.187684998474075;
        double referenceLongitude = 106.82502839555477;

        // Buat objek Location untuk pengguna
        Location userLocation = new Location("userLocation");
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);

        // Buat objek Location untuk patokan
        Location referenceLocation = new Location("referenceLocation");
        referenceLocation.setLatitude(referenceLatitude);
        referenceLocation.setLongitude(referenceLongitude);

        // Hitung jarak dalam meter
        float distance = userLocation.distanceTo(referenceLocation);

        // Periksa apakah jarak lebih dari 50 meter
        if (distance > 50) {
            showDialog_longlat_radius();

        } else {
            // Lanjutkan ke proses berikutnya
            // ...

            if (tv_time_getin.getText().toString().equals("-")) {

                if (img_background_foto_user.getDrawable() == null) {

                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {

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
                    post_get_in();
                }

            } else {

                if (img_background_foto_user.getDrawable() == null) {

                    if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {

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
        }
    }

    private void showDialog_longlat_radius() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan")
                .setMessage("Anda berada di luar radius 50 meter dari lokasi yang ditentukan.")
                .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aksi jika pengguna memilih untuk lanjutkan
                        dialog.dismiss();

                        if (tv_time_getin.getText().toString().equals("-")) {

                            if (img_background_foto_user.getDrawable() == null) {

                                if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {

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
                                post_get_in();
                            }

                        } else {

                            if (img_background_foto_user.getDrawable() == null) {

                                if (checkPermissionREAD_EXTERNAL_STORAGE(menu_getin_getout.this)) {

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
                        // ...
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aksi jika pengguna memilih batal
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);  // Stop the handler when activity is destroyed
    }

}
