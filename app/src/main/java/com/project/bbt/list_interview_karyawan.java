package com.project.bbt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.project.bbt.Item.resignteammodel;
import com.project.bbt.R;
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

import static com.project.bbt.menu.txt_alpha;

public class list_interview_karyawan extends AppCompatActivity {
    SearchView simpleSearchView;
    ListView list;
    List<resignteammodel> resignteammodelList;
    DrawerLayout dLayout;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_interview_karyawan);
        NukeSSLCerts.nuke();
        list = findViewById(R.id.list);
        simpleSearchView = findViewById(R.id.simpleSearchView);
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
        setNavigationDrawer();

    }

    private void getResignExitInterView() {
        resignteammodelList.clear();
        pDialog = new ProgressDialog(list_interview_karyawan.this);
        showDialog();
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://36.88.110.134:27/bbt_api/rest_server/pengajuan/resign/index_jabatan?jabatan=" + menu.text_jabatan.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            JSONObject movieObject = null;
                            for (int i = 0; i < movieArray.length(); i++) {

                                movieObject = movieArray.getJSONObject(i);

                                final resignteammodel movieItem = new resignteammodel(
                                        movieObject.getString("id"),
                                        movieObject.getString("nik_baru"),
                                        movieObject.getString("alasan_resign"),
                                        movieObject.getString("klarifikasi_resign"),
                                        movieObject.getString("nama_karyawan_struktur"),
                                        movieObject.getString("jabatan_karyawan"),
                                        movieObject.getString("submit_date"),
                                        movieObject.getString("tanggal_pengajuan"),
                                        movieObject.getString("tanggal_efektif_resign"),
                                        movieObject.getString("status_atasan"),
                                        movieObject.getString("status_atasan_2"));


                                resignteammodelList.add(movieItem);

                                if(movieObject.getString("status_exit").equals("1")) {
                                    resignteammodelList.remove(movieItem);
                                }


                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

//                                        Intent i = new Intent(list_interview_karyawan.this, menu_exitinterview.class);
//                                        String nik_baru = ((resignteammodel) parent.getItemAtPosition(position)).getNik_baru();
//                                        i.putExtra(LoginItem.KEY_NIK, nik_baru);
//                                        startActivity(i);
                                    }
                                });

                                hideDialog();

                                final ListViewAdapterResign adapter = new ListViewAdapterResign(resignteammodelList, getApplicationContext());

                                Collections.sort(resignteammodelList, new Comparator<resignteammodel>() {
                                    public int compare(resignteammodel o1, resignteammodel o2) {
                                        if (o2.getSubmit_date() == null || o1.getSubmit_date() == null)
                                            return 0;
                                        return o2.getSubmit_date().compareTo(o1.getSubmit_date());
                                    }
                                });



                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextChange(String nextText) {
                                        adapter.getFilter().filter(nextText);
                                        return false;
                                    }
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
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
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static class ListViewAdapterResign extends ArrayAdapter<resignteammodel> {

        List<resignteammodel> movieItemList;

        private Context context;

        public ListViewAdapterResign(List<resignteammodel> movieItemList, Context context) {
            super(context, R.layout.list_item_resign_approval, movieItemList);
            this.movieItemList = movieItemList;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.list_item_resign_approval, null, true);

            TextView tanggalid = listViewItem.findViewById(R.id.tanggalid);
            TextView nik = listViewItem.findViewById(R.id.nik);
            TextView name = listViewItem.findViewById(R.id.name);
            TextView jabatan = listViewItem.findViewById(R.id.jabatan);
            TextView tanggalpengajuan = listViewItem.findViewById(R.id.tanggalpengajuan);
            LinearLayout efektiflinear = listViewItem.findViewById(R.id.efektiflinear);
            TextView tanggalefektif = listViewItem.findViewById(R.id.tanggalefektif);

            TextView reason = listViewItem.findViewById(R.id.reason);
            TextView klarifikasi = listViewItem.findViewById(R.id.klarifikasi);
            ImageView approval = listViewItem.findViewById(R.id.approval);
            ImageView approval2 = listViewItem.findViewById(R.id.approval2);

            resignteammodel movieItem = getItem(position);

            tanggalid.setText(convertFormat(movieItem.getSubmit_date()));
            nik.setText(movieItem.getNik_baru());
            name.setText(movieItem.getNama_karyawan_struktur());
            jabatan.setText(movieItem.getJabatan_karyawan());
            tanggalpengajuan.setText(tanggal(movieItem.getTanggal_pengajuan()));
            if(movieItem.getTanggal_efektif_resign().equals("0000-00-00") || movieItem.getTanggal_efektif_resign().equals("null")){
                efektiflinear.setVisibility(View.GONE);
            }
            tanggalefektif.setText(tanggal(movieItem.getTanggal_efektif_resign()));

            reason.setText(movieItem.getAlasan_resign());
            klarifikasi.setText(movieItem.getKlarifikasi_resign());

            if (movieItem.getStatus_atasan().equals("0")) {
                approval.setImageResource(R.drawable.btn_open);
            } else if (movieItem.getStatus_atasan().equals("1")) {
                approval.setImageResource(R.drawable.btn_aprv);
            } else if (movieItem.getStatus_atasan().equals("2")) {
                approval.setImageResource(R.drawable.btn_notaprv);
            } else if (movieItem.getStatus_atasan().equals("3")) {
                approval.setImageResource(R.drawable.btn_hangus);
            }

            if (movieItem.getStatus_atasan_2().equals("0")) {
                approval2.setImageResource(R.drawable.btn_open);
            } else if (movieItem.getStatus_atasan_2().equals("1")) {
                approval2.setImageResource(R.drawable.btn_aprv);
            } else if (movieItem.getStatus_atasan_2().equals("2")) {
                approval2.setImageResource(R.drawable.btn_notaprv);
            } else if (movieItem.getStatus_atasan_2().equals("3")) {
                approval2.setImageResource(R.drawable.btn_hangus);
            }

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
                            list_interview_karyawan.this);
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
                                    Intent intent = new Intent(list_interview_karyawan.this, MainActivity.class);
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

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "Tidak Ada Tanggal";
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
            return "Tidak Ada Tanggal";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        return convetDateFormat.format(date);
    }
    @Override
    protected void onResume() {
        super.onResume();
        resignteammodelList = new ArrayList<>();
        getResignExitInterView();
    }
    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());

        
        super.onDestroy();
    }
}
