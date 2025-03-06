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
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.project.bbt.Item.LoginItem;
import com.project.bbt.Item.approvalcutikhusus;
import com.project.bbt.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class approval_cuti_khusus extends AppCompatActivity {
    TextView nomor;
    ProgressDialog pDialog;
    SearchableSpinner spinner;
    Button lihat, approvalall;
    private List<approvalcutikhusus> movieItemList;
    ListView list;
    DrawerLayout dLayout;
    SharedPreferences sharedPreferences;

    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_id_bagian, string_id_divisi;
    TextView kondisi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_cuti_khusus);
        NukeSSLCerts.nuke();

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_id_bagian = sharedPreferences.getString("str_id_bagian", null);
        string_id_divisi = sharedPreferences.getString("str_id_divisi", null);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.id_toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        setTitle("Approval Cuti Khusus");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });
        spinner = (SearchableSpinner) findViewById(R.id.jenisizin);
        lihat = (Button) findViewById(R.id.lihat);
        approvalall = (Button) findViewById(R.id.approvalall);
        setNavigationDrawer();


        list = findViewById(R.id.list);

        String[] jenisizin = {"Open", "Approved", "Not Approved", "Hangus", "Semua"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, jenisizin);
        spinner.setAdapter(adapter);

        lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalkhusus();
            }
        });
        movieItemList = new ArrayList<>();
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
                            approval_cuti_khusus.this);
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
                                    Intent intent = new Intent(approval_cuti_khusus.this, MainActivity.class);
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
    protected void onResume() {
        super.onResume();
        movieItemList = new ArrayList<>();
//        approvalkhusus();
    }

    private void approvalkhusus() {
        movieItemList.clear();
        pDialog = new ProgressDialog(approval_cuti_khusus.this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/cuti_khusus/index_get_cuti_khusus_approval_atasan?id_divisi="+ string_id_divisi + "&id_bagian="+ string_id_bagian + "&id_jabatan=" + string_id_jabatan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject movieObject = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                movieObject = movieArray.getJSONObject(i);

                                final approvalcutikhusus movieItem = new approvalcutikhusus(
                                        movieObject.getString("id_cuti_khusus"),
                                        movieObject.getString("nik_cuti_khusus"),
                                        movieObject.getString("tanggal_pengajuan"),
                                        movieObject.getString("nama_karyawan_struktur"),
                                        movieObject.getString("start_cuti_khusus"),
                                        movieObject.getString("lokasi_struktur"),
                                        movieObject.getString("ket_tambahan_khusus"),
                                        movieObject.getString("jenis_cuti_khusus"),
                                        movieObject.getString("kondisi"),
                                        movieObject.getString("status_cuti_khusus"),
                                        movieObject.getString("feedback_cuti_khusus"));

                                movieItemList.add(movieItem);

                                hideDialog();

                                if (spinner.getSelectedItem().toString().equals("Open")) {
                                    approvalall.setVisibility(View.VISIBLE);
                                    if (!movieObject.getString("status_cuti_khusus").equals("0")) {
                                        movieItemList.remove(movieItem);
                                    }
                                }
                                if (spinner.getSelectedItem().toString().equals("Approved")) {
                                    approvalall.setVisibility(View.INVISIBLE);
                                    if (!movieObject.getString("status_cuti_khusus").equals("1")) {
                                        movieItemList.remove(movieItem);
                                    }
                                }
                                if (spinner.getSelectedItem().toString().equals("Not Approved")) {
                                    approvalall.setVisibility(View.INVISIBLE);
                                    if (!movieObject.getString("status_cuti_khusus").equals("2")) {
                                        movieItemList.remove(movieItem);
                                    }
                                }
                                if (spinner.getSelectedItem().toString().equals("Hangus")) {
                                    approvalall.setVisibility(View.INVISIBLE);
                                    if (!movieObject.getString("status_cuti_khusus").equals("3")) {
                                        movieItemList.remove(movieItem);
                                    }
                                }
                                if (spinner.getSelectedItem().toString().equals("Semua")) {
                                    approvalall.setVisibility(View.INVISIBLE);
                                    if (!movieItemList.contains(movieItem)) {
                                        movieItemList.add(movieItem);
                                    }
                                }

                                ListViewAdapterCutiKhususApproval adapter = new ListViewAdapterCutiKhususApproval(movieItemList, getApplicationContext());

                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

//                                if(!menu.txt_lokasi.getText().toString().equals("Pusat")) {
//                                    if (!movieObject.getString("lokasi_struktur").equalsIgnoreCase(menu.txt_lokasi.getText().toString())) {
//                                        movieItemList.remove(movieItem);
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                }

//                                Collections.sort(movieItemList, new Comparator<approvalcutikhusus>() {
//                                    public int compare(approvalcutikhusus o1, approvalcutikhusus o2) {
//                                        if (o2.getStart_cuti_khusus() == null || o1.getStart_cuti_khusus() == null)
//                                            return 0;
//                                        return o2.getStart_cuti_khusus().compareTo(o1.getStart_cuti_khusus());
//                                    }
//                                });
                                if(spinner.getSelectedItem().toString().equals("Open")) {
                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                            Intent i = new Intent(approval_cuti_khusus.this, form_cuti_khusus.class);
                                            String nik_baru = ((approvalcutikhusus) parent.getItemAtPosition(position)).getId_cuti_khusus();
                                            String nama_karyawan = ((approvalcutikhusus) parent.getItemAtPosition(position)).getNama_karyawan_struktur();

                                            // Menambahkan data ke dalam intent
                                            i.putExtra(LoginItem.KEY_NIK, nik_baru);
                                            i.putExtra("nama_karyawan", nama_karyawan);

                                            // Memulai activity
                                            startActivity(i);

                                            // Menampilkan data di log untuk keperluan debugging
                                            System.out.println("Test NIK: " + nik_baru);
                                            System.out.println("Test Nama Karyawan: " + nama_karyawan);
                                        }
                                    });
                                } else {
                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                        }
                                    });
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
            };
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        7200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class ListViewAdapterCutiKhususApproval extends ArrayAdapter<approvalcutikhusus> {

        private class ViewHolder {
            TextView tanggalid;
            TextView id;
            TextView nik;
            TextView name;
            TextView date3;
            TextView jenisCT;

            TextView condition;
            TextView keterangan;
            TextView feedback1;
            ImageView approval1;

            CheckBox checkBox1;

        }
        List<approvalcutikhusus> movieItemList;
        private Context context;
        boolean[] itemChecked;

        public ListViewAdapterCutiKhususApproval(List<approvalcutikhusus> movieItemList, Context context) {
            super(context, R.layout.list_view_approval_khusus, movieItemList);
            this.movieItemList = movieItemList;
            this.context = context;
            approvalall = (Button) findViewById(R.id.approvalall);
            nomor = (TextView) findViewById(R.id.nomor);
            itemChecked = new boolean[movieItemList.size()];
        }

        public int getCount() {
            return movieItemList.size();
        }

        public approvalcutikhusus getItem(int position) {
            return movieItemList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            approvalcutikhusus movieItem = getItem(position);
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_view_approval_khusus, parent, false);

                viewHolder.tanggalid = (TextView) convertView.findViewById(R.id.tanggalid);

                viewHolder.id = (TextView) convertView.findViewById(R.id.id);
                viewHolder.nik = (TextView) convertView.findViewById(R.id.nik);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.date3 = (TextView) convertView.findViewById(R.id.date3);
                viewHolder.jenisCT = (TextView) convertView.findViewById(R.id.jenisCT);

                viewHolder.condition = (TextView) convertView.findViewById(R.id.condition);
                viewHolder.keterangan = (TextView) convertView.findViewById(R.id.keterangan);
                viewHolder.feedback1 = (TextView) convertView.findViewById(R.id.feedback1);
                viewHolder.approval1 = (ImageView) convertView.findViewById(R.id.approval1);
                viewHolder.jenisCT = (TextView) convertView.findViewById(R.id.jenisCT);
                viewHolder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tanggalid.setText(convertFormat(movieItem.getStart_cuti_khusus()));
            viewHolder.id.setText(movieItem.getId_cuti_khusus());
            viewHolder.nik.setText(movieItem.getNik_baru());
            viewHolder.name.setText(movieItem.getNama_karyawan_struktur());

            viewHolder.date3.setText(tanggal(movieItem.getStart_cuti_khusus()));
            viewHolder.jenisCT.setText(movieItem.getJenis_cuti_khusus());
            viewHolder.condition.setText(movieItem.getKondisi());

            viewHolder.keterangan.setText(movieItem.getKet_tambahan_khusus());
            viewHolder.feedback1.setText(movieItem.getFeedback_cuti_khusus());

            viewHolder.checkBox1.setChecked(true);
            viewHolder.checkBox1.setTag(position);

            if (itemChecked[position])
                viewHolder.checkBox1.setChecked(true);
            else
                viewHolder.checkBox1.setChecked(false);

            viewHolder.checkBox1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (viewHolder.checkBox1.isChecked())
                        itemChecked[position] = true;
                    else
                        itemChecked[position] = false;
                }
            });

            if (movieItem.getStatus_cuti_khusus().equals("0")) {
                viewHolder.approval1.setImageResource(R.drawable.btn_open);
            } else if (movieItem.getStatus_cuti_khusus().equals("1")) {
                viewHolder.approval1.setImageResource(R.drawable.btn_aprv);
            } else if (movieItem.getStatus_cuti_khusus().equals("2")) {
                viewHolder.approval1.setImageResource(R.drawable.btn_notaprv);
            } else if (movieItem.getStatus_cuti_khusus().equals("3")) {
                viewHolder.approval1.setImageResource(R.drawable.btn_hangus);
            }

            int a = getPosition(getItem(getCount() -1));
            nomor.setText(String.valueOf(a));

            approvalall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(approval_cuti_khusus.this);
                    alertDialogBuilder.setTitle("Anda yakin untuk approve semua ?");
                    alertDialogBuilder.setMessage("Klik Ya untuk approve all  !");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, final int id) {
                            approve();

//                            final int y = Integer.valueOf(nomor.getText().toString());
//
//                                if (!viewHolder.checkBox1.isChecked()) {
//                                    movieItemList.remove(position);
//                                    notifyDataSetChanged();
//                            }

//                            new Timer().schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                }
//                            }, 1000);


                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            return convertView;
        }

        private void approve() {
            pDialog = new ProgressDialog(approval_cuti_khusus.this);
            showDialog();
            pDialog.setContentView(R.layout.progress_dialog);
            pDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                }
            };
            handler.postDelayed(r, 200);

            final int x = Integer.valueOf(nomor.getText().toString());
            for (int i = 0; i <= x; i++) {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int finalI = i;
                System.out.println("angka akhir = " + x);

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/cuti_khusus/index",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (finalI == x) {
                                    hideDialog();
                                    Toast.makeText(getApplicationContext(), "Semuanya sudah di update", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "maaf ada kesalahan", Toast.LENGTH_LONG).show();
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
                        System.out.println("angkahasil = " + finalI);

                        String nik_baru = getItem(finalI).getNik_baru();
                        String tanggal2 = getItem(finalI).getStart_cuti_khusus();
                        String jenis = getItem(finalI).getJenis_cuti_khusus();

                        String id = getItem(finalI).getId_cuti_khusus();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        String tanggal = sdf2.format(new Date());
                        System.out.println("angkahasil = " + nik_baru + "," + tanggal2 + "," + jenis + "," + id + "," + tanggal);

                        // Pengecekan apakah ada data yang kosong
                        if (nik_baru == null || nik_baru.isEmpty() ||
                                tanggal2 == null || tanggal2.isEmpty() ||
                                jenis == null || jenis.isEmpty() ||
                                id == null || id.isEmpty()) {

                            System.out.println("Data tidak lengkap: ada yang kosong.");
                            Toast.makeText(getApplicationContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            params.put("nik_baru", nik_baru);
                            params.put("tanggal_absen", tanggalconvert(tanggal2));
                            params.put("jenis_cuti_khusus", jenis);
                            params.put("id_cuti_khusus", id);
                            params.put("status_cuti_khusus", "1");
                            params.put("feedback_cuti_khusus", "OK ACC ALL");
                            params.put("tanggal_approval_cuti_khusus", tanggal);
                        }

                        return params;
                    }

                };
                stringRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                RequestQueue requestQueue = Volley.newRequestQueue(approval_cuti_khusus.this);
                requestQueue.add(stringRequest);
            }
        }
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
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        return convetDateFormat.format(date);
    }

    public static String tanggalconvert(String inputDate) {
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
        int beta = Integer.parseInt(menu.txt_alpha.getText().toString());

        
        super.onDestroy();
    }
}

