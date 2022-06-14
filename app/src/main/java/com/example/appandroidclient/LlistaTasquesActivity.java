package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.appandroidclient.adapters.ProjecteAdapter;
import com.example.appandroidclient.databinding.ActivityLlistaTasquesBinding;

public class LlistaTasquesActivity extends AppCompatActivity {

    private ActivityLlistaTasquesBinding binding;
    private String loginTocken;
    private ProjecteAdapter mProjecteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLlistaTasquesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // setContentView(R.layout.activity_llista_tasques);

        loginTocken = this.getIntent().getExtras().getString("token");

        binding.rcyProjectes.setLayoutManager(new LinearLayoutManager(this));

        mProjecteAdapter = new ProjecteAdapter(loginTocken);
        binding.rcyProjectes.setAdapter(mProjecteAdapter);

    }
}