package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.appandroidclient.adapters.ProjecteAdapter;
import com.example.appandroidclient.databinding.ActivityLlistaTasquesBinding;

import org.milaifontanals.model.Estat;
import org.milaifontanals.model.Projecte;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LlistaTasquesActivity extends AppCompatActivity {

    private ActivityLlistaTasquesBinding binding;
    private String loginTocken;
    private ProjecteAdapter mProjecteAdapter;
    private List<Projecte> projectes = new ArrayList<>();
    private List<String> spinnerProjectes = new ArrayList<>();
    private List<Estat> estats = new ArrayList<>();
    private List<String> spinnerEstat = new ArrayList<>();
    private ThreadProjectes threadProjectes;
    private ThreadEstats threadEstats;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "10.175.0.3";
    private Integer port = 5056;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLlistaTasquesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginTocken = this.getIntent().getExtras().getString("token");

        spinnerProjectes.add("Tots els projectes");
        for (Projecte proj: projectes) {
            spinnerProjectes.add(proj.getNom());
        }

        binding.spnFiltreProjecte.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerProjectes));

        threadEstats = new ThreadEstats();
        new Thread(threadEstats).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        spinnerEstat.add("Totes les tasques");
        for (Estat stat: estats) {
            String nom = stat.getNom().toLowerCase().replace("_", " ");
            String primeraLletra = nom.substring(0,1).toUpperCase();
            spinnerEstat.add(primeraLletra + nom.substring(1, nom.length()));
        }

        binding.spnFiltreEstats.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerEstat));
        binding.spnFiltreEstats.setSelection(5);

        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: fer el filtre
            }
        });

        binding.rcyProjectes.setLayoutManager(new LinearLayoutManager(this));

        mProjecteAdapter = new ProjecteAdapter(loginTocken);
        binding.rcyProjectes.setAdapter(mProjecteAdapter);

        threadProjectes = new ThreadProjectes();
        new Thread(threadProjectes).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class ThreadProjectes implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(2);
                oos.writeObject(loginTocken);
                projectes = (List<Projecte>)ois.readObject();

                Log.d("APP", "projectes: " + projectes);

                oos.writeObject(-1);
                Log.d("APP", "Tancant aquesta connexió : " + socket);
                socket.close();
                Log.d("APP", "Connexió tancada");

            } catch(Exception e){

                Log.d("APP", "SOCKET");
            }
        }
    }

    private class ThreadEstats implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(4);
                oos.writeObject(loginTocken);
                estats = (List<Estat>)ois.readObject();

                Log.d("APP", "estats: " + estats);

                oos.writeObject(-1);
                Log.d("APP", "Tancant aquesta connexió : " + socket);
                socket.close();
                Log.d("APP", "Connexió tancada");

            } catch(Exception e){

                Log.d("APP", "SOCKET");
            }
        }
    }



}