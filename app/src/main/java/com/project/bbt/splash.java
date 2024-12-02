package com.project.bbt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.project.bbt.R;
//import com.sanojpunchihewa.updatemanager.UpdateManager;
//import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

public class splash extends AppCompatActivity {
    private int waktu_loading = 4500;
    SharedPreferences sharedPreferences;
//    UpdateManager mUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NukeSSLCerts.nuke();

        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                    String nik_baru = sharedPreferences.getString("str_nip_karyawan", null);
//                    Toast.makeText(splash.this, "NIP = " + nik_baru, Toast.LENGTH_SHORT).show();

                    if (nik_baru == null) {
                        Intent home = new Intent(splash.this, MainActivity.class);
                        startActivity(home);
                        finish(); // Tambahkan ini
                    } else {
                        Intent intent = new Intent(splash.this, menu.class);
                        startActivity(intent);
                        finish(); // Tambahkan ini juga
                    }

                }
            }, waktu_loading);
        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle("Maaf, anda belum terhubung ke internet");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
//        String nik_baru = sharedPreferences.getString("str_nip_karyawan", null);
//
//        if (nik_baru != null) {
//            Intent intent = new Intent(splash.this, menu.class);
//            startActivity(intent);
//            finish(); // Supaya tidak kembali ke splash lagi
//        }
//    }
}