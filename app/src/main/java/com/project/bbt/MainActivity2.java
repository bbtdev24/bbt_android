package com.project.bbt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.project.bbt.R;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        NukeSSLCerts.nuke();

    }
}