package com.project.bbt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.project.bbt.R;
import com.google.android.material.card.MaterialCardView;

public class menu_absen_mobile extends AppCompatActivity {

    MaterialCardView bt_getin_getout, bt_absenmanual;
    String string_nip_karyawan, string_lokasi_karyawan,
            string_jabatan_karyawan, string_nama_karyawan,
            string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;
    SharedPreferences sharedPreferences;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_absen_mobile);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        bt_getin_getout = findViewById(R.id.bt_getin_getout);
        bt_absenmanual = findViewById(R.id.bt_absenmanual);

//        bt_getin_getout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(menu_absen_mobile.this, menu_getin_getout.class);
//                startActivity(i);
//            }
//        });

        bt_getin_getout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(menu_absen_mobile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(menu_absen_mobile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // Minta izin lokasi
                    ActivityCompat.requestPermissions(menu_absen_mobile.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    // Izin sudah ada, langsung pindah activity
                    Intent i = new Intent(menu_absen_mobile.this, menu_getin_getout.class);
                    startActivity(i);
                }
            }
        });


        bt_absenmanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menu_absen_mobile.this, menu_absen_manual.class);
                startActivity(i);
            }
        });



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Kalau izin lokasi dikasih, langsung pindah
                Intent i = new Intent(menu_absen_mobile.this, menu_getin_getout.class);
                startActivity(i);
            } else {
                // Izin ditolak, kasih warning
                Toast.makeText(this, "Izin lokasi ditolak, fitur ini mungkin tidak bisa digunakan", Toast.LENGTH_LONG).show();
            }
        }
    }

}