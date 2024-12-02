package com.project.bbt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.bbt.R;

public class alasanexit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alasanexit);
        NukeSSLCerts.nuke();

        NukeSSLCerts.nuke();
    }
}