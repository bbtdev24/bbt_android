package com.project.bbt;

import static com.project.bbt.menu.txt_alpha;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.Item.sisacutimodel;
import com.project.bbt.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class form_cuti_tahunan_hr extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    TextView no, tgl, tahun_cuti, sisa_cuti;
    EditText pengajuan, nikbaru, namakaryawan, tidakhadir, kondisi, keterangan2;
    ImageButton cekdetail, ceklihat;
    ImageButton approval;
    DrawerLayout dLayout;
    SharedPreferences sharedPreferences;

    RadioButton diterima, tidakterima;
    RadioGroup opsiapproval;
    EditText feedback;
    private List<sisacutimodel> movieItemList3;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cuti_tahunan_hr);
        NukeSSLCerts.nuke();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });

        no = (TextView) findViewById(R.id.no);
        tgl = (TextView) findViewById(R.id.tgl);
        setNavigationDrawer();
        pengajuan = (EditText) findViewById(R.id.pengajuan);
        nikbaru = (EditText) findViewById(R.id.nikbaru);
        namakaryawan = (EditText) findViewById(R.id.namakaryawan);
        tidakhadir = (EditText) findViewById(R.id.tidakhadir);
        kondisi = (EditText) findViewById(R.id.kondisi);
        keterangan2 = (EditText) findViewById(R.id.keterangan2);
        cekdetail = (ImageButton) findViewById(R.id.cekdetail);
        ceklihat = (ImageButton) findViewById(R.id.ceklihat);

        tahun_cuti = (TextView) findViewById(R.id.tahun_cuti);
        sisa_cuti = (TextView) findViewById(R.id.sisa_cuti);

        feedback = (EditText) findViewById(R.id.keterangantambahan);

        getform();

        opsiapproval = (RadioGroup) findViewById(R.id.option);
        opsiapproval.setOnCheckedChangeListener(this);
        diterima = (RadioButton) findViewById(R.id.diterima);
        tidakterima = (RadioButton) findViewById(R.id.tidakterima);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        tgl.setText(currentDateandTime);

        approval = (ImageButton) findViewById(R.id.approve);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedback.getText().toString().length() == 0){
                    feedback.setError("Masukkan Keterangan");
                } else {
                    approve();
                }
            }
        });
        movieItemList3 = new ArrayList<>();
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
                            form_cuti_tahunan_hr.this);
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
                                    Intent intent = new Intent(form_cuti_tahunan_hr.this, MainActivity.class);
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == R.id.diterima) {
            no.setText("1");
        }
        if (i == R.id.tidakterima) {
            no.setText("2");

        }
    }

    private void getform() {
        pDialog = new ProgressDialog(form_cuti_tahunan_hr.this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        final String idkaryawan = getIntent().getStringExtra("id_cutitahunan");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/pengajuan/cuti_tahunan/index?id_sisa_cuti=" + idkaryawan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {

                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final String gambar = movieObject.getString("dok_cuti_tahunan");
                                final String lat = movieObject.getString("lat");
                                final String lon = movieObject.getString("lon");

                                pengajuan.setText(movieObject.getString("id_sisa_cuti"));
                                nikbaru.setText(movieObject.getString("nik_baru"));
                                namakaryawan.setText(movieObject.getString("nama_karyawan_struktur"));
                                tidakhadir.setText(movieObject.getString("tanggal_absen"));
                                kondisi.setText(movieObject.getString("opsi_cuti_tahunan"));
                                keterangan2.setText(movieObject.getString("ket_tambahan_tahunan"));

                                if(lat.equals("null") && (lon.equals("null"))){
                                    ceklihat.setVisibility(View.INVISIBLE);
                                }

                                if ("".equals(gambar)) {
                                    cekdetail.setVisibility(View.INVISIBLE);
                                }
                                cekdetail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://203.100.57.36/project/ess-bt/uploads/cuti_tahunan/" + gambar));
                                        startActivity(browserIntent);
                                    }

                                });

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/api/cuti/index?nik_baru=" + movieObject.getString("nik_baru"),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                    JSONArray movieArray = obj.getJSONArray("data");

                                                    for (int i = 0; i < movieArray.length(); i++) {

                                                        JSONObject movieObject = movieArray.getJSONObject(movieArray.length()-1);

                                                        tahun_cuti.setText(String.valueOf(movieObject.getString("tahun")));
                                                        sisa_cuti.setText(String.valueOf(movieObject.getInt("hak_cuti_utuh")));

                                                        Toast.makeText(form_cuti_tahunan_hr.this, "Tahun Cuti = " + tahun_cuti.getText().toString() + " ,Sisa Cuti = " + sisa_cuti.getText().toString(), Toast.LENGTH_SHORT).show();

                                                        System.out.println(sisa_cuti.getText().toString() + " - " + tahun_cuti.getText().toString());

                                                        hideDialog();
                                                    }



                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), "Anda belum mempunyai hak cuti", Toast.LENGTH_SHORT).show();
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
                                    }
                                };
                                stringRequest.setRetryPolicy(
                                        new DefaultRetryPolicy(
                                                500000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                        ));

                                RequestQueue requestQueue = Volley.newRequestQueue(form_cuti_tahunan_hr.this);
                                requestQueue.add(stringRequest);



                                ceklihat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + lat + " " + lon));
                                        System.out.println("https://www.google.com/search?q=" + lat + " " + lon);
                                        startActivity(browserIntent);
                                    }

                                });
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
                })
        {
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

    private void approve() {
        pDialog = new ProgressDialog(form_cuti_tahunan_hr.this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://36.88.110.134:27/bbt_api/rest_server/pengajuan/cuti_tahunan/index_HrManager",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hakcutiupdate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (tidakterima.isChecked()) {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "sudah di update", Toast.LENGTH_LONG).show();
                            form_cuti_tahunan_hr.this.finish();
                        } else if (diterima.isChecked()) {
                            hideDialog();
                            hakcutiupdate();
                            Toast.makeText(getApplicationContext(), "sudah di update", Toast.LENGTH_LONG).show();
                            form_cuti_tahunan_hr.this.finish();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "maaf ada kesalahan", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String nik = nikbaru.getText().toString();
                String tanggal2 = tidakhadir.getText().toString();
                String jenis = kondisi.getText().toString();

                String idkaryawan = pengajuan.getText().toString();
                String status = no.getText().toString();
                String feedback1 = feedback.getText().toString();
                String tanggal = tgl.getText().toString();

                params.put("nik_baru", nik);
                params.put("tanggal_absen", tanggal2);
                params.put("opsi_cuti_tahunan", jenis);

                params.put("id_sisa_cuti", idkaryawan);
                params.put("status_cuti_manager", status);
                params.put("feedback_cuti_manager", feedback1);
                params.put("tanggal_cuti_manager", tanggal);

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

    private void hakcutiupdate() {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://36.88.110.134:27/bbt_api/rest_server/pengajuan/Cuti_tahunan/index_hakcuti",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "sudah di update", Toast.LENGTH_LONG).show();
                        form_cuti_tahunan_hr.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "maaf ada kesalahan", Toast.LENGTH_LONG).show();
                    }
                }
        ) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("nik_sisa_cuti", nikbaru.getText().toString());
                params.put("tahun", tahun_cuti.getText().toString());
                params.put("hak_cuti_utuh", String.valueOf(Integer.parseInt(sisa_cuti.getText().toString()) - 1));

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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());


        super.onDestroy();
    }
}