package com.project.bbt;

import static android.view.View.GONE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.Item.LoginItem;
import com.project.bbt.Item.pelanggaranmodel;
import com.project.bbt.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class menu_pembinaan extends AppCompatActivity {
    ListView list_pembinaan;
    private List<pelanggaranmodel> movieItemList2 = new ArrayList<>();
    SharedPreferences sharedPreferences;

    MaterialCardView detail;

    TextView noForm, suratSP, efektif, berakhir, status, jenispelanggaran, keteranganpelanggaran;

    ListViewAdapterPelanggaran adapter;

    DrawerLayout dLayout;
    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pembinaan);
        NukeSSLCerts.nuke();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        list_pembinaan = findViewById(R.id.list_pembinaan);
        detail = findViewById(R.id.detail);

        noForm = findViewById(R.id.noForm);
        suratSP = findViewById(R.id.suratSP);

        efektif = findViewById(R.id.efektif);
        berakhir = findViewById(R.id.berakhir);
        status = findViewById(R.id.status);
        jenispelanggaran = findViewById(R.id.jenispelanggaran);
        keteranganpelanggaran = findViewById(R.id.keteranganpelanggaran);


        getPunishment();

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
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
        setNavigationDrawer();

        toolbar.setTitle("Pembinaan Pegawai");

        list_pembinaan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String old_date =  adapter.getItem(position).getEnd_date();
//                int ok = getDateDiffFromNow(old_date);
//                String tanggal2 = Integer.toString(ok);

                list_pembinaan.setVisibility(GONE);
                detail.setVisibility(View.VISIBLE);
                noForm.setText("Nomor Form : " + adapter.getItem(position).getNo_surat());
                suratSP.setText(adapter.getItem(position).getPunishment_historical_violance());

                efektif.setText(tanggal(adapter.getItem(position).getWarning_start_historical_violance()));
                berakhir.setText(tanggal(adapter.getItem(position).getWarning_end_historical_violance()));
                status.setText(adapter.getItem(position).getStatus_dokumen());
                jenispelanggaran.setText(adapter.getItem(position).getPelanggaran_historical_violance());
                keteranganpelanggaran.setText(adapter.getItem(position).getWarning_note_historical_violance());
//                tgl_kontrak.setText(tanggal(adapter.getItem(position).getStart_date()));
//                tgl_berakhir.setText(tanggal(adapter.getItem(position).getEnd_date()));
//                kontrak.setText(adapter.getItem(position).getKontrak());

            }
        });

    }

    private void setNavigationDrawer() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        dLayout = (DrawerLayout) findViewById(R.id.dl_pembinaan); // initiate a DrawerLayout
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
                            menu_pembinaan.this);
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
                                    Intent intent = new Intent(menu_pembinaan.this, MainActivity.class);
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

    private void getPunishment() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/punishment/index?nik_baru=" + nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject movieObject = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                movieObject = movieArray.getJSONObject(i);

                                final pelanggaranmodel movieItem = new pelanggaranmodel(
                                        movieObject.getString("no_surat"),
                                        movieObject.getString("punishment_historical_violance"),
                                        movieObject.getString("pelanggaran_historical_violance"),
                                        movieObject.getString("warning_note_historical_violance"),
                                        movieObject.getString("warning_start_historical_violance"),
                                        movieObject.getString("warning_end_historical_violance"),
                                        movieObject.getString("status_dokumen"));


                                movieItemList2.add(movieItem);

                                adapter = new ListViewAdapterPelanggaran(movieItemList2, getApplicationContext());
                                list_pembinaan.setAdapter(adapter);



                                Collections.sort(movieItemList2, new Comparator<pelanggaranmodel>() {
                                    public int compare(pelanggaranmodel o1, pelanggaranmodel o2) {
                                        if (o2.getWarning_start_historical_violance() == null || o1.getWarning_start_historical_violance() == null)
                                            return 0;
                                        return o2.getNo_surat().compareTo(o1.getNo_surat());
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

    public class ListViewAdapterPelanggaran extends ArrayAdapter<pelanggaranmodel> {

        List<pelanggaranmodel> movieItemList2;

        private Context context;

        public ListViewAdapterPelanggaran(List<pelanggaranmodel> movieItemList2, Context context) {
            super(context, R.layout.list_item_pelanggaran, movieItemList2);
            this.movieItemList2 = movieItemList2;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_item_pelanggaran, null, true);

            TextView noForm = listViewItem.findViewById(R.id.noForm);
            TextView surat = listViewItem.findViewById(R.id.suratSP);

            pelanggaranmodel movieItem = getItem(position);

            noForm.setText("Nomor Form : " + movieItem.getNo_surat());
            surat.setText(movieItem.getPunishment_historical_violance());

//            tanggal_2.setText(convertFormat(movieItem.getWarning_start_historical_violance()));
//            no.setText(movieItem.getNo_surat());
//            sanksi.setText(movieItem.getPunishment_historical_violance());
//            pelanggaran.setText(movieItem.getPelanggaran_historical_violance());
//            keterangan.setText(movieItem.getWarning_note_historical_violance());
//
//            efektif.setText(tanggal(movieItem.getWarning_start_historical_violance()));
//            berakhir.setText(tanggal(movieItem.getWarning_end_historical_violance()));
//            sanksistatus.setText(movieItem.getStatus_dokumen());

            return listViewItem;
        }
    }

    public static String convertFormat(String inputDate) {
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

    public static String tanggal(String inputDate) {
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
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return convetDateFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        if(detail.getVisibility() == View.VISIBLE){
            detail.setVisibility(GONE);
            list_pembinaan.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }
}