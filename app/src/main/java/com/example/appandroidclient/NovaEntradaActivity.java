package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.appandroidclient.databinding.ActivityMainBinding;
import com.example.appandroidclient.databinding.ActivityNovaEntradaBinding;

import org.milaifontanals.model.Entrada;
import org.milaifontanals.model.Estat;
import org.milaifontanals.model.Projecte;
import org.milaifontanals.model.Usuari;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NovaEntradaActivity extends AppCompatActivity {

    private ActivityNovaEntradaBinding binding;
    private String loginTocken;
    private String estat;
    private Integer idTasca;
    private Integer idEntrada;
    private List<Usuari> usuaris = new ArrayList<>();
    private List<String> spinnerUsuari = new ArrayList<>();
    private List<Estat> estats = new ArrayList<>();
    private List<String> spinnerEstat = new ArrayList<>();
    private Usuari usuariLoginat;
    private Entrada entradaSeleccionada;
    private ThreadUsuaris threadUsuaris;
    private ThreadUsuariLoginat threadUsuariLoginat;
    private ThreadEstats threadEstats;
    private ThreadEntrada threadEntrada;
    private ThreadNovaEntrada threadNovaEntrada;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "192.168.1.103";
    private Integer port = 5056;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNovaEntradaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginTocken = this.getIntent().getExtras().getString("token");
        estat = this.getIntent().getExtras().getString("estat");
        idTasca = Integer.parseInt(this.getIntent().getExtras().getString("idTasca"));
        idEntrada = Integer.parseInt(this.getIntent().getExtras().getString("idEntrada"));

        threadEntrada = new ThreadEntrada();
        new Thread(threadEntrada).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadUsuaris = new ThreadUsuaris();
        new Thread(threadUsuaris).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadEstats = new ThreadEstats();
        new Thread(threadEstats).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadUsuariLoginat = new ThreadUsuariLoginat();
        new Thread(threadUsuariLoginat).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        spinnerUsuari.add("");
        for (Usuari usu: usuaris) {
            spinnerUsuari.add(usu.getNom());
        }

        binding.spnEscritaPer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerUsuari));
        binding.spnNovaAssignacio.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerUsuari));

        spinnerEstat.add("");
        for (Estat stat: estats) {
            String nom = stat.getNom().toLowerCase().replace("_", " ");
            String primeraLletra = nom.substring(0,1).toUpperCase();
            spinnerEstat.add(primeraLletra + nom.substring(1, nom.length()));
        }

        binding.spnEstat.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerEstat));


        if (estat.equals("nou")) {
            Date avui = new Date();
            binding.tvxDataEntrada.setText(sdf.format(avui));
            binding.spnEscritaPer.setSelection(usuariLoginat.getId());
            binding.spnEscritaPer.setEnabled(false);

        } else if (estat.equals("editar")) {
            binding.tvxDataEntrada.setText(entradaSeleccionada.getDataFormatada());
            binding.edtEntradaEntrada.setText(entradaSeleccionada.getEntrada());
            binding.spnEscritaPer.setSelection(entradaSeleccionada.getEscriptor().getId());
            binding.spnEscritaPer.setEnabled(false);
            if (entradaSeleccionada.getNovaAssignacio() != null) {
                binding.spnNovaAssignacio.setSelection(entradaSeleccionada.getNovaAssignacio().getId());
            }
            if (entradaSeleccionada.getNouEstat() != null) {
                binding.spnEstat.setSelection(entradaSeleccionada.getNouEstat().getId());
            }
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estat.equals("nou")) {
                    threadNovaEntrada = new ThreadNovaEntrada();
                    new Thread(threadNovaEntrada).start();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (estat.equals("editar")) {

                }
                finish();
            }
        });

    }

    private class ThreadUsuaris implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(7);
                oos.writeObject(loginTocken);
                usuaris = (List<Usuari>)ois.readObject();

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

    private class ThreadEntrada implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(8);
                oos.writeObject(idTasca);
                oos.writeObject(idEntrada);
                entradaSeleccionada = (Entrada)ois.readObject();

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

    private class ThreadUsuariLoginat implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(9);
                oos.writeObject(loginTocken);
                usuariLoginat = (Usuari)ois.readObject();

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

    private class ThreadNovaEntrada implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(10);
                oos.writeObject(idTasca);
                Integer novaEntrada = (Integer)ois.readObject();

                Entrada entrada = new Entrada(novaEntrada, new Date(), binding.edtEntradaEntrada.getText().toString(),
                        usuariLoginat,
                        binding.spnNovaAssignacio.getSelectedItem() != null?
                                usuaris.get(binding.spnNovaAssignacio.getSelectedItemPosition()) : null,
                        binding.spnEstat.getSelectedItem() != null?
                                estats.get(binding.spnEstat.getSelectedItemPosition()) : null);

                oos.writeObject(11);
                oos.writeObject(idTasca);
                oos.writeObject(entrada);

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