package com.project.bbt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.bbt.Item.LoginItem;
import com.project.bbt.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.project.bbt.menu.txt_alpha;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {

    public static EditText editTextnik_baru;
    EditText editTextpassword;
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    Button qr, login;
//    UpdateManager mUpdateManager;
    
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NukeSSLCerts.nuke();
        createLoadingDialog();
        editTextnik_baru = (EditText) findViewById(R.id.editTextnik_baru);
        editTextpassword = (EditText) findViewById(R.id.editTextpassword);
        login = (Button) findViewById(R.id.login);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        sharedPreferences.contains(LoginItem.KEY_NIK);

        if (sharedPreferences.contains(LoginItem.KEY_NIK)) {
            pDialog = new ProgressDialog(MainActivity.this);
            showLoadingDialog();
            pDialog.setContentView(R.layout.progress_dialog);
            pDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
            sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
            String nik_baru = sharedPreferences.getString(LoginItem.KEY_NIK, null);

            //JIKA BERHASIL LOGIN
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + nik_baru,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getString("status").equals("true")) {
                                    hideLoadingDialog();
                                    Intent intent = new Intent(MainActivity.this, menu.class);
                                    startActivity(intent);
                                } else {
                                    hideLoadingDialog();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoadingDialog();
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

        //KLIK BUTTON LOGIN
        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (editTextnik_baru.getText().toString().length() == 0 && editTextpassword.getText().toString().length() == 0) {
                    editTextnik_baru.setError("NIK diperlukan!");
                    editTextpassword.setError("Password diperlukan!");
                } else if (editTextnik_baru.getText().toString().length() == 0) {
                    editTextnik_baru.setError("NIK diperlukan!");
                } else if (editTextpassword.getText().toString().length() == 0) {
                    editTextpassword.setError("Password diperlukan!");
                } else {
                    sendLogin();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    JSONObject object = new JSONObject(result.getContents());
                    System.out.println(object.getString("nik"));
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();

                    final String nik = result.getContents();
                    pDialog = new ProgressDialog(MainActivity.this);
                    showLoadingDialog();
                    pDialog.setContentView(R.layout.progress_dialog);
                    pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent
                    );
                    StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + result.getContents(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideLoadingDialog();
                                    Intent intent = new Intent(MainActivity.this, qrpassword.class);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(LoginItem.KEY_NIK, nik);
                                    editor.apply();
                                    startActivity(intent);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideLoadingDialog();
                                    Toast.makeText(getApplicationContext(), "Hasil tidak ditemukan atau jaringan error", Toast.LENGTH_SHORT).show();
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
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendLogin2() {

        showLoadingDialog();
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + editTextnik_baru.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");

                                for (int i = 0; i < movieArray.length(); i++) {

                                    JSONObject movieObject = movieArray.getJSONObject(i);

                                    if (movieObject.getString("password").equals(md5(editTextpassword.getText().toString()))) {
                                        Toast.makeText(getApplicationContext(), "Selamat Datang", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this, menu.class);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(LoginItem.KEY_NIK, editTextnik_baru.getText().toString());
                                        editor.apply();

                                        hideLoadingDialog();
                                        startActivity(intent);
                                    }
                                    else if (!movieObject.getString("password").equals(md5(editTextpassword.getText().toString()))) {
                                        hideLoadingDialog();
                                        Toast.makeText(getApplicationContext(), "password tidak sama", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                hideLoadingDialog();
                                Toast.makeText(MainActivity.this, "NIK Tidak sesuai", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            hideLoadingDialog();
                            System.out.println("Alasan " + error);
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

    private void sendLogin() {

        showLoadingDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://ess.banktanah.id/bbt_api/rest_server/api/login/index_login_absensi?nip=" + editTextnik_baru.getText().toString(),                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {

                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    hideLoadingDialog();

                                    if (movieObject.getString("password").equals(md5(editTextpassword.getText().toString()))) {
                                        final String lokasi = movieObject.getString("namaLokasi"); //namaLokasi PUSAT
                                        final String jabatan = movieObject.getString("jabatan_karyawan"); //jabatan_karyawan staff
                                        final String nama = movieObject.getString("nama_karyawan_struktur"); //nama_karyawan_struktur
                                        final String companyId = movieObject.getString("namaDivisi"); //namaDivisi DIVISI UMUM DAN ADMINISTRASI
                                        final String idJabatan = movieObject.getString("idJabatan"); //idJabatan 1
                                        final String nourut	 = movieObject.getString("noUrut"); //noUrut 000036
                                        final String idLokasi = movieObject.getString("idLokasi"); //idLokasi	1
                                        final String idBagian = movieObject.getString("idBagian"); //idBagian	25
                                        final String idDivisi = movieObject.getString("idDivisi"); //idDivisi	86
                                        final String namaBagian	 = movieObject.getString("namaBagian"); //namaBagian BAGIAN TEKNOLOGI INFORMASI

                                        Intent intent = new Intent(MainActivity.this, menu.class);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("str_nip_karyawan", editTextnik_baru.getText().toString());
                                        editor.putString("str_lokasi_karyawan", lokasi);
                                        editor.putString("str_jabatan_karyawan", jabatan);
                                        editor.putString("str_nama_karyawan", nama);
                                        editor.putString("str_nama_divisi", companyId);
                                        editor.putString("str_id_jabatan", idJabatan);
                                        editor.putString("str_nourut_karyawan", nourut);
                                        editor.putString("str_id_lokasi", idLokasi);
                                        editor.putString("str_id_bagian", idBagian);
                                        editor.putString("str_id_divisi", idDivisi);
                                        editor.putString("str_nama_bagian", namaBagian);

                                        editor.apply();
                                        startActivity(intent);

                                    } else if (!movieObject.getString("password").equals(md5(editTextpassword.getText().toString()))) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setContentText("Oops... NIK / Password Salah")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    }
                                }

                            } else {
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Oops.. NIK / Password Salah")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();

                                hideLoadingDialog();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError) {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Nik anda salah!")
                                    .show();
                        } else {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Jaringan sedang bermasalah!")
                                    .show();
                        }

                        hideLoadingDialog();

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
                        8000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Anda yakin untuk keluar dari aplikasi ini ?");
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String md5(String s) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash)
            {
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(setting.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(setting.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }
    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());
        super.onDestroy();
    }

    private void createLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false); // Dialog tidak bisa di-cancel dengan menekan di luar
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Menghilangkan background default dialog
    }

    private void showLoadingDialog() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

}
