package com.project.bbt;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.project.bbt.Item.LoginItem;
import com.project.bbt.Item.kontrakmodel;
import com.project.bbt.R;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.project.bbt.menu.txt_alpha;

public class kontrakkaryawan extends AppCompatActivity {
    public static ListView list;
    private List<kontrakmodel> movieItemList;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    DrawerLayout dLayout;
    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontrakkaryawan);
        NukeSSLCerts.nuke();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });
        list = findViewById(R.id.list);
        movieItemList = new ArrayList<>();
        setNavigationDrawer();

//        getKontrak();
    }

//    private void getKontrak() {
//        pDialog = new ProgressDialog(kontrakkaryawan.this);
//        showDialog();
//        pDialog.setContentView(R.layout.progress_dialog);
//        pDialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent
//        );
//        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
//        String nik_baru = sharedPreferences.getString(KEY_NIK, null);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/pengajuan/Surat_Kontrak/index?nik_baru=" + nik_baru,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            final JSONArray movieArray = obj.getJSONArray("data");
//
//                            JSONObject movieObject = null;
//                            for (int i = 0; i < movieArray.length(); i++) {
//
//                                movieObject = movieArray.getJSONObject(i);
//
//                                final kontrakmodel movieItem = new kontrakmodel(
//                                        movieObject.getString("submit_date"),
//                                        movieObject.getString("no_urut"),
//                                        movieObject.getString("nik_baru"),
//                                        movieObject.getString("nama_karyawan_struktur"),
//                                        movieObject.getString("kontrak"),
//                                        movieObject.getString("tanggal_kontrak"),
//                                        movieObject.getString("start_date"),
//                                        movieObject.getString("end_date"));
//
//
//                                movieItemList.add(movieItem);
//
//                                hideDialog();
//
//                                ListViewAdapterSKontrak adapter = new ListViewAdapterSKontrak(movieItemList, getApplicationContext());
//
//                                Collections.sort(movieItemList, new Comparator<kontrakmodel>() {
//                                    public int compare(kontrakmodel o1, kontrakmodel o2) {
//                                        if (o2.getSubmit_date() == null || o1.getSubmit_date() == null)
//                                            return 0;
//                                        return o2.getSubmit_date().compareTo(o1.getSubmit_date());
//                                    }
//                                });
//
//                                list.setAdapter(adapter);
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), "Belum ada Pengajuan", Toast.LENGTH_SHORT).show();
//                    }
//                })
//        {
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
//                        500000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                ));
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//
//    public class ListViewAdapterSKontrak extends ArrayAdapter<kontrakmodel> {
//
//        List<kontrakmodel> movieItemList;
//
//        private Context context;
//
//        public ListViewAdapterSKontrak(List<kontrakmodel> movieItemList, Context context) {
//            super(context, R.layout.list_item_kontrak, movieItemList);
//            this.movieItemList = movieItemList;
//            this.context = context;
//        }
//
//        @SuppressLint("NewApi")
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            LayoutInflater inflater = LayoutInflater.from(context);
//
//            View listViewItem = inflater.inflate(R.layout.list_item_kontrak, null, true);
//
//            TextView tanggal_2 = listViewItem.findViewById(R.id.tanggal_2);
//            TextView nik = listViewItem.findViewById(R.id.nik);
//            TextView nama = listViewItem.findViewById(R.id.nama);
//            TextView tglawal1 = listViewItem.findViewById(R.id.tglawal1);
//
//            TextView tglakhir1 = listViewItem.findViewById(R.id.tglakhir1);
//            TextView waktu = listViewItem.findViewById(R.id.waktu);
//            TextView status = listViewItem.findViewById(R.id.status);
//
//            kontrakmodel movieItem = getItem(position);
//
//            String old_date =  movieItem.getEnd_date();
//            int ok = getDateDiffFromNow(old_date);
//            if (60 >= ok && ok >= 46) {
//                waktu.setTextColor(Color.parseColor("#008000"));
//            } else  if (45 >= ok && ok >= 31) {
//                waktu.setTextColor(Color.parseColor("#FFFF00"));
//            } else  if (30 >= ok && ok >= 1) {
//                waktu.setTextColor(Color.parseColor("#FF0000"));
//            }
//
//            if (ok < 0) {
//                waktu.setTextColor(Color.parseColor("#FF0000"));
//                ok = 0;
//            }
//
//            String tanggal = Integer.toString(ok);
//
//            tanggal_2.setText(convertFormat(movieItem.getStart_date()));
//            nik.setText(movieItem.getNik_baru());
//            nama.setText(movieItem.getNama_karyawan_struktur());
//            tglawal1.setText(movieItem.getStart_date());
//            waktu.setText(tanggal + " Hari");
//
//            tglakhir1.setText(movieItem.getEnd_date());
//            status.setText(movieItem.getKontrak());
//
//
//            return listViewItem;
//        }
//    }

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
                            kontrakkaryawan.this);
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
                                    Intent intent = new Intent(kontrakkaryawan.this, MainActivity.class);
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

    public int getDateDiffFromNow(String date){
        int days = 0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long diff = sdf.parse(date).getTime() - new Date().getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            days = ((int) (long) hours / 24);
        }catch (Exception e){
            e.printStackTrace();
        }
        return days;
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
    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());


        super.onDestroy();
    }
}