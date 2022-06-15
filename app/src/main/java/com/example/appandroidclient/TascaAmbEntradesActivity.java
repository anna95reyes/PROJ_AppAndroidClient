package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.appandroidclient.databinding.ActivityMainBinding;
import com.example.appandroidclient.databinding.ActivityTascaAmbEntradesBinding;

public class TascaAmbEntradesActivity extends AppCompatActivity {

    private ActivityTascaAmbEntradesBinding binding;
    private String loginTocken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTascaAmbEntradesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}