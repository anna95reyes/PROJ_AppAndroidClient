package com.example.appandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;

import com.example.appandroidclient.databinding.ActivityMainBinding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    private Integer port = 5056;
    private String login = null;

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

                socketLogin();

            }
        });


    }

    private void socketLogin(){
        try {

            Log.d("APP", "PASO 1");

            InetAddress ip = InetAddress.getByName("localhost");
            Log.d("APP", "PASO 2");
            socket = new Socket(ip, port);
            Log.d("APP", "PASO 3");
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Log.d("APP", "PASO 4");
            dos.writeInt(1);
            dos.writeUTF(binding.edtLogin.getText().toString());
            dos.writeUTF(hashMD5(binding.edtPassword.getText().toString()));

            login = dis.readUTF();
            if (!login.equals("")) {
                Log.d("APP", "LOGIN: " + login);
                binding.edtLogin.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                binding.edtPassword.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            } else {
                binding.edtLogin.setBackgroundColor(Color.parseColor("#FFAFAF"));
                binding.edtPassword.setBackgroundColor(Color.parseColor("#FFAFAF"));
            }




            dos.writeInt(-1);

            Log.d("APP", "Tancant aquesta connexió : " + socket);
            socket.close();
            Log.d("APP", "Connexió tancada");


        } catch(Exception e){

            Log.d("APP", "SOCKET");
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