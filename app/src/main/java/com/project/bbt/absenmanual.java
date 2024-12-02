package com.project.bbt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.project.bbt.Item.LoginItem;
import com.project.bbt.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.project.bbt.menu.text_jabatan;
import static com.project.bbt.menu.txt_alpha;
import static com.project.bbt.menu_getin_getout.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static java.util.Calendar.DAY_OF_MONTH;

public class absenmanual extends AppCompatActivity {
    SearchableSpinner lokasi;
    ArrayList<String> Lokasi;
    EditText tanggal, waktu, keterangan;
    Button pengajuan;
    DrawerLayout dLayout;
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    RadioButton in, out;
    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan,
            string_no_urut_karyawan, string_id_lokasi, string_id_bagian,
            string_id_divisi, string_nama_bagian;

    Button uploadbutton;
    ImageView gambar1;

    ContentValues cv;
    Uri imageUri;
    ImageView img_background_foto_berita_acara;
    RelativeLayout relative_background_foto_berita_acara;
    Bitmap bitmap_user_photo;

    public static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absenmanual);
        NukeSSLCerts.nuke();
        lokasi = (SearchableSpinner) findViewById(R.id.lokasi);
        Lokasi = new ArrayList<>();
        in = (RadioButton) findViewById(R.id.in);
        out = (RadioButton) findViewById(R.id.out);
        uploadbutton = findViewById(R.id.uploadbutton);
        img_background_foto_berita_acara = findViewById(R.id.gambar1);
        relative_background_foto_berita_acara = findViewById(R.id.relative_upload);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);
        string_id_lokasi = sharedPreferences.getString("str_id_lokasi", null);
        string_id_bagian = sharedPreferences.getString("str_id_bagian", null);
        string_id_divisi = sharedPreferences.getString("str_id_divisi", null);
        string_nama_bagian = sharedPreferences.getString("str_nama_bagian", null);

        setNavigationDrawer();

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.id_toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        setTitle("Form Absen Manual");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });

//        uploadbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (checkPermissionREAD_EXTERNAL_STORAGE(absenmanual.this)) {
//
//                    cv = new ContentValues();
//                    cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                    cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//
//                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    startActivityForResult(intent, 1);
//
//                } else {
//
//                    Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });



//        uploadbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (checkPermissionREAD_EXTERNAL_STORAGE(absenmanual.this)) {
//
//                    // Menyimpan gambar ke MediaStore atau path yang sesuai
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        cv = new ContentValues();
//                        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
//                        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
//                        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
//                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//                    } else {
//                        String path = Environment.getExternalStorageDirectory() + "/MyPicture.jpg";
//                        File file = new File(path);
//                        imageUri = Uri.fromFile(file);
//                    }
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    startActivityForResult(intent, 1);
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(absenmanual.this)) {

//                    File photoFile = new File(Environment.getExternalStorageDirectory(), "MyPicture.jpg");
                    File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPicture.jpg");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(
                                absenmanual.this,
                                BuildConfig.APPLICATION_ID + ".provider",  // Pastikan sesuai dengan authorities di manifest
                                photoFile
                        );
                    } else {
                        imageUri = Uri.fromFile(photoFile);
                    }

                    Log.d("log PhotoFilePath", photoFile.getAbsolutePath());

                    if (photoFile.exists()) {
                        Log.d("PhotoFileCheck", "File exists, path: " + photoFile.getAbsolutePath());
                    } else {
                        Log.d("PhotoFileCheck", "File does not exist.");
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, 1);

                } else {
                    Toast.makeText(getApplicationContext(), "Tidak dapat mengakses kamera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        in.setChecked(true);

        tanggal = (EditText) findViewById(R.id.tanggal);
        waktu = (EditText) findViewById(R.id.waktu);
        keterangan = (EditText) findViewById(R.id.keterangan);
        pengajuan = (Button) findViewById(R.id.pengajuan);
        pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keterangan.getText().toString().length() == 0){
                    keterangan.setError("Masukkan Keterangan!");
                } else {
                    postabsenmanual();
                    upload_foto_absen_berita_acara();
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMMM-yyyy" );
        final Calendar myCalendar = Calendar.getInstance();
        tanggal.setText(sdf.format(myCalendar.getTime()));

        SimpleDateFormat sdf2 = new SimpleDateFormat( "HH:mm:00" );
        final Calendar myCalendar2 = Calendar.getInstance();
        myCalendar2.set(Calendar.DAY_OF_MONTH, myCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        waktu.setText(sdf2.format(myCalendar.getTime()));

        waktu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(absenmanual.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        waktu.setText(String.format("%02d:%02d:00", selectedHour, selectedMinute));

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            private void updateLabel() {
                String myFormat = "dd-MMMM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                tanggal.setText(sdf.format(myCalendar.getTime()));
            }
        };
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(absenmanual.this, datePickerDialog, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(DAY_OF_MONTH)).show();
            }
        });

        getLokasi();
    }

    private void setNavigationDrawer() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
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
                            absenmanual.this);
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
                                    Intent intent = new Intent(absenmanual.this, MainActivity.class);
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

    private void postabsenmanual() {
        pDialog = new ProgressDialog(this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://36.88.110.134:27/bbt_api/rest_server/pengajuan/Absen_manual2/index",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "data sudah dimasukkan", Toast.LENGTH_LONG).show();
                        absenmanual.this.finish();
                    }
                }, new Response.ErrorListener() {
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
                Toast.makeText(getApplicationContext(), "Maaf ada kesalahan", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            };

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK ,null);
                String jabatan = text_jabatan.getText().toString();
//                String Lokasi = lokasi.getSelectedItem().toString();
//                String[] splited_text = Lokasi.split(" \\(");
//                Lokasi = splited_text[1];
//                Lokasi = Lokasi.replace(")", "");
                String date = convertFormat(tanggal.getText().toString());
                String jam = waktu.getText().toString();
                String Keterangan = keterangan.getText().toString();

                params.put("nik_absen", string_nip_karyawan);
                params.put("jabatan_absen", string_id_jabatan);
                params.put("lokasi_absen", string_id_lokasi);

                if(in.isChecked()){
                    params.put("jenis_absen", "IN");
                } else if (out.isChecked()){
                    params.put("jenis_absen", "OUT");
                }

                params.put("tanggal_absen", date);
                params.put("waktu_absen", jam);
                params.put("ket_absen", Keterangan);

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

    private void getLokasi() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/master/lokasi/index",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Lokasi.add("PILIH LOKASI");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String depo = null;
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    depo = jsonObject1.getString("depo_nama");
                                    String sn = jsonObject1.getString("SN");
                                    Lokasi.add(depo + " (" + sn + ") ");

                                }
                            }
                            lokasi.setTitle("Pilih Pegawai");
                            lokasi.setAdapter(new ArrayAdapter<String>(absenmanual.this, android.R.layout.simple_spinner_dropdown_item, Lokasi));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Maaf ada kesalahan", Toast.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            };
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin diberikan, bisa lanjutkan proses kamera
                    uploadbutton.performClick();  // Panggil kembali proses kamera
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
                    img_background_foto_berita_acara.setImageBitmap(bitmap_user_photo);

                    // Mendapatkan layout parameters dari ImageView
                    ViewGroup.LayoutParams paramktp = img_background_foto_berita_acara.getLayoutParams();

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
                    img_background_foto_berita_acara.setLayoutParams(paramktp);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("log Bitmap", "Berhasil mengambil gambar");
            } else {
                Log.d("log Bitmap", "Gagal mengambil gambar");
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

    public void upload_foto_absen_berita_acara() {

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://36.88.110.134:27/bbt_api/rest_server/php/upload_image_absen_berita_acara.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(absenmanual.this, "BERHASIL UPLOAD BA", Toast.LENGTH_SHORT).show();
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

                String str_absen_in_or_out = null;

                if(in.isChecked()){
                    str_absen_in_or_out = "IN";

                } else if (out.isChecked()){
                    str_absen_in_or_out = "OUT";
                }

                params.put("nama_foto", "IMG_"+ string_no_urut_karyawan + "_" + str_absen_in_or_out + "-" + currentDateandTime2);
                params.put("nama_folder", "upload_absen_berita_acara");
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



    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());

        
        super.onDestroy();
    }
}