package com.example.sspdim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;
public class welcome_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_SHORT).show();
    }
}