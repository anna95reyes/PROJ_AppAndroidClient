package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appandroidclient.adapters.EntradaAdapter;
import com.example.appandroidclient.adapters.ProjecteAdapter;
import com.example.appandroidclient.databinding.ActivityMainBinding;
import com.example.appandroidclient.databinding.ActivityTascaAmbEntradesBinding;

import org.milaifontanals.model.Projecte;
import org.milaifontanals.model.Tasca;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TascaAmbEntradesActivity extends AppCompatActivity {

    private ActivityTascaAmbEntradesBinding binding;
    private String loginTocken;
    private Integer idTasca;
    private Tasca tasca;
    private EntradaAdapter mEntradaAdapter;
    private ThreadTasca threadTasca;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "10.175.0.3";
    private Integer port = 5056;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTascaAmbEntradesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginTocken = this.getIntent().getExtras().getString("token");
        idTasca = Integer.parseInt(this.getIntent().getExtras().getString("tasca"));

        binding.btnPullBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        threadTasca = new ThreadTasca();
        new Thread(threadTasca).start();
        try {
            Thread.sleep(1400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        binding.tvxIdTasca.setText(tasca.getId().toString());
        binding.tvxDataCreacioTasca.setText(tasca.getDataCreacioFormatada());
        binding.tvxNomTasca.setText(tasca.getNom());
        binding.tvxDataLimitTasca.setText(tasca.getDataLimitFormatada());
        binding.tvxDescTasca.setText(tasca.getDescripcio());

        binding.rcyEntrades.setLayoutManager(new LinearLayoutManager(this));

        mEntradaAdapter = new EntradaAdapter(loginTocken, idTasca);
        binding.rcyEntrades.setAdapter(mEntradaAdapter);

        binding.btnNovaEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TascaAmbEntradesActivity.this, NovaEntradaActivity.class);
                intent.putExtra("token", loginTocken);
                intent.putExtra("estat", "nou");
                intent.putExtra("idTasca", idTasca.toString());
                intent.putExtra("idEntrada", "-1");
                startActivity(intent);
            }
        });

    }

    private class ThreadTasca implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(5);
                oos.writeObject(idTasca);
                tasca = (Tasca)ois.readObject();

                Log.d("APP", "tasca: " + tasca);

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