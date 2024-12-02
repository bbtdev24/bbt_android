package com.project.bbt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.bbt.R;

public class list_teamt_revisi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teamt_revisi);
        NukeSSLCerts.nuke();

    }
}