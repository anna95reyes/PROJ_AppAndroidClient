package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;

import com.example.appandroidclient.databinding.ActivityMainBinding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "192.168.1.103";
    private Integer port = 5056;
    private String loginTocken;
    private ThreadLogin threadLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.edtLogin.setText("annareyes");
        binding.edtPassword.setText("annareyes");

        binding.btnIniciarSessio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                threadLogin = new ThreadLogin();
                new Thread(threadLogin).start();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (loginTocken != null){
                    Intent intent = new Intent(MainActivity.this, LlistaTasquesActivity.class);
                    intent.putExtra("token", loginTocken);
                    startActivity(intent);
                }
            }
        });


    }


    private class ThreadLogin implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(1);
                oos.writeObject(binding.edtLogin.getText().toString());
                oos.writeObject(hashMD5(binding.edtPassword.getText().toString()));

                String login = (String)ois.readObject();
                if (!login.equals("")) {
                    Log.d("APP", "LOGIN: " + login);
                    loginTocken = login;
                    Log.d("APP", "loginTocken: " + loginTocken);

                } else {
                    Log.d("APP", "Usuari o contrasenya incorrectes");
                    loginTocken = null;
                    Log.d("APP", "loginTocken: " + loginTocken);
                    //binding.edtLogin.setBackgroundColor(Color.RED);
                    //binding.edtPassword.setBackgroundColor(Color.RED);
                }
                oos.writeObject(-1);
                Log.d("APP", "Tancant aquesta connexió : " + socket);
                socket.close();
                Log.d("APP", "Connexió tancada");

            } catch(Exception e){

                Log.d("APP", "SOCKET");
            }
        }
    }

    public String hashMD5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}