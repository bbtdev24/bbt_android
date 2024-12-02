package com.project.bbt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ListView;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.project.bbt.Item.LoginItem;
import com.project.bbt.Item.approvalcutikhusus;
import com.project.bbt.Item.model_pemberitahuan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class menu_bell_training extends AppCompatActivity {

    DrawerLayout dLayout;
    SharedPreferences sharedPreferences;
    ListView listview_pemberitahuan;
    List<model_pemberitahuan> modelpemberitahuan = new ArrayList<>();
    ProgressDialog pDialog;

    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bell_training);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        listview_pemberitahuan = findViewById(R.id.listview_pemberitahuan);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        setTitle("Pemberitahuan");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });

        setNavigationDrawer();

        get_listview_pemberithauan();
    }

    private void get_listview_pemberithauan() {
        pDialog = new ProgressDialog(menu_bell_training.this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://203.100.57.36/project/ess-api-android-bt/rest_server/api/training/index_get_training?nama_pegawai=" + string_nama_karyawan + "&no_urut_pegawai=" + string_no_urut_karyawan + "&nip_pegawai=" + string_nip_karyawan + "&id_jabatan_pegawai=" + string_id_jabatan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject movieObject = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                movieObject = movieArray.getJSONObject(i);

                                final model_pemberitahuan movieItem = new model_pemberitahuan(
                                        movieObject.getString("title_training"),
                                        movieObject.getString("keterangan_training"),
                                        movieObject.getString("file_path"),
                                        movieObject.getString("tanggal_pengajuan"),
                                        movieObject.getString("kode_training"));

                                modelpemberitahuan.add(movieItem);

                                hideDialog();
                                ListviewAdapterPemberitahuan adapter = new ListviewAdapterPemberitahuan(modelpemberitahuan, getApplicationContext());
                                listview_pemberitahuan.setAdapter(adapter);


                                listview_pemberitahuan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                        Intent i = new Intent(menu_bell_training.this, menu_detail_bell_training.class);
                                        String str_intent_training_title = ((model_pemberitahuan) parent.getItemAtPosition(position)).getTitle_training();
                                        String str_intent_training_date = ((model_pemberitahuan) parent.getItemAtPosition(position)).getTanggal_pengajuan();
                                        String str_intent_training_description = ((model_pemberitahuan) parent.getItemAtPosition(position)).getKeterangan_training();
                                        String str_intent_training_file_path = ((model_pemberitahuan) parent.getItemAtPosition(position)).getFile_path();

                                        // Menambahkan data ke dalam intent
                                        i.putExtra("training_title", str_intent_training_title);
                                        i.putExtra("training_date", str_intent_training_date);
                                        i.putExtra("training_description", str_intent_training_description);
                                        i.putExtra("training_file_path", str_intent_training_file_path);

                                        // Memulai activity
                                        startActivity(i);

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
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "Belum ada Pengajuan", Toast.LENGTH_SHORT).show();
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
                )
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    public class ListviewAdapterPemberitahuan extends ArrayAdapter<model_pemberitahuan> {
//
//        List<model_pemberitahuan> movieItemList;
//
//        private Context context;
//
//        public ListviewAdapterPemberitahuan(List<model_pemberitahuan> movieItemList, Context context) {
//            super(context, R.layout.list_item_pemberitahuan, movieItemList);
//            this.movieItemList = movieItemList;
//            this.context = context;
//        }
//
//        @SuppressLint("NewApi")
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            LayoutInflater inflater = LayoutInflater.from(context);
//
//            final View listViewItem = inflater.inflate(R.layout.list_item_pemberitahuan, null, true);
//
//            TextView title_pemberitahuan = listViewItem.findViewById(R.id.title_pemberitahuan);
//            TextView description_pemberitahuan = listViewItem.findViewById(R.id.description_pemberitahuan);
//            TextView date_pemberitahuan = listViewItem.findViewById(R.id.date_pemberitahuan);
//
//            model_pemberitahuan movieItem = getItem(position);
//
//            title_pemberitahuan.setText(movieItem.getTitle_training());
//            description_pemberitahuan.setText(movieItem.getKeterangan_training());
////            date_pemberitahuan.setText(movieItem.getTanggal_pengajuan());
//
//            // Format tanggal input (dari API)
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            // Format tanggal yang diinginkan
//            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
//
//            try {
//                // Ambil tanggal dari movieItem
//                Date date = inputFormat.parse(movieItem.getTanggal_pengajuan());
//                // Ubah ke format yang diinginkan
//                String formattedDate = outputFormat.format(date);
//                // Set teks yang sudah diformat ke TextView
//                date_pemberitahuan.setText(formattedDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//                // Jika ada error parsing, tampilkan tanggal aslinya
//                date_pemberitahuan.setText(movieItem.getTanggal_pengajuan());
//            }
//
//
//            return listViewItem;
//        }
//    }

    public class ListviewAdapterPemberitahuan extends ArrayAdapter<model_pemberitahuan> {

        private List<model_pemberitahuan> movieItemList;
        private Context context;
        private SharedPreferences sharedPreferences;

        public ListviewAdapterPemberitahuan(List<model_pemberitahuan> movieItemList, Context context) {
            super(context, R.layout.list_item_pemberitahuan, movieItemList);
            this.movieItemList = movieItemList;
            this.context = context;
            this.sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View listViewItem = inflater.inflate(R.layout.list_item_pemberitahuan, null, true);

            TextView title_pemberitahuan = listViewItem.findViewById(R.id.title_pemberitahuan);
            TextView description_pemberitahuan = listViewItem.findViewById(R.id.description_pemberitahuan);
            TextView date_pemberitahuan = listViewItem.findViewById(R.id.date_pemberitahuan);

            model_pemberitahuan movieItem = getItem(position);

            title_pemberitahuan.setText(movieItem.getTitle_training());
            description_pemberitahuan.setText(movieItem.getKeterangan_training());

            // Format tanggal
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());

            try {
                Date date = inputFormat.parse(movieItem.getTanggal_pengajuan());
                String formattedDate = outputFormat.format(date);
                date_pemberitahuan.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                date_pemberitahuan.setText(movieItem.getTanggal_pengajuan());
            }

            // Cek status baca di SharedPreferences
            boolean isRead = sharedPreferences.getBoolean("notification_" + movieItem.getKode_training(), false);
            if (isRead) {
                title_pemberitahuan.setTextColor(Color.GRAY);
                description_pemberitahuan.setTextColor(Color.GRAY);
                date_pemberitahuan.setTextColor(Color.GRAY);
            } else {
                title_pemberitahuan.setTextColor(Color.BLACK);
                description_pemberitahuan.setTextColor(Color.BLACK);
                date_pemberitahuan.setTextColor(Color.BLACK);
            }

            // Set OnClickListener untuk membuka detail dan menandai sebagai sudah dibaca
            listViewItem.setOnClickListener(v -> {
                if (!isRead) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notification_" + movieItem.getKode_training(), true);
                    editor.apply();

                    title_pemberitahuan.setTextColor(Color.GRAY);
                    description_pemberitahuan.setTextColor(Color.GRAY);
                    date_pemberitahuan.setTextColor(Color.GRAY);
                }

                Intent intent = new Intent(context, menu_detail_bell_training.class);
                intent.putExtra("training_title", movieItem.getTitle_training());
                intent.putExtra("training_date", movieItem.getTanggal_pengajuan());
                intent.putExtra("training_description", movieItem.getKeterangan_training());
                intent.putExtra("training_file_path", movieItem.getFile_path());

                // Tambahkan FLAG_ACTIVITY_NEW_TASK jika context bukan Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            return listViewItem;
        }
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
                            menu_bell_training.this);
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
                                    Intent intent = new Intent(menu_bell_training.this, MainActivity.class);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}