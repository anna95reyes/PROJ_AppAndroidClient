package com.example.appandroidclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroidclient.NovaEntradaActivity;
import com.example.appandroidclient.R;

import org.milaifontanals.model.Entrada;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class EntradaAdapter extends RecyclerView.Adapter<EntradaAdapter.ViewHolder> {

    private List<Entrada> mEntrades;
    private int mPosSeleccionada = -1;
    private ThreadEntrades threadEntrades;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "192.168.1.103";
    private Integer port = 5056;
    private String loginTocken;
    private Integer idTasca;

    public EntradaAdapter(String loginTocken, Integer idTasca){
        this.loginTocken = loginTocken;
        this.idTasca = idTasca;
        threadEntrades = new ThreadEntrades();
        new Thread(threadEntrades).start();
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_entrades, parent, false);
        ViewHolder vh = new ViewHolder(fila);
        //Tinc acces a la fila i puc programar esdeveniment
        fila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = vh.getAdapterPosition();
                if (pos != mPosSeleccionada){
                    if (mPosSeleccionada != -1) {
                        notifyItemChanged(mPosSeleccionada);
                    }
                    mPosSeleccionada = pos;
                    notifyItemChanged(mPosSeleccionada);
                } else {
                    mPosSeleccionada = -1;
                    notifyItemChanged(pos);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrada entrada = mEntrades.get(position);
        holder.txvNum.setText(entrada.getNumero().toString());
        holder.txvData.setText(entrada.getDataFormatada());
        if (entrada.getNouEstat() != null) {
            holder.txvEstat.setText(entrada.getNouEstat().getNom());
        }
        holder.txvEntrada.setText(entrada.getEntrada());

        if (mPosSeleccionada != -1){
            holder.setOnClickListeners(loginTocken, idTasca, entrada.getNumero());
        }
    }

    @Override
    public int getItemCount() {
        return mEntrades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txvNum;
        public TextView txvData;
        public TextView txvEstat;
        public TextView txvEntrada;
        private Context context;

        public ViewHolder(@NonNull View fila) {
            super(fila);
            txvNum = fila.findViewById(R.id.txvNum);
            txvData = fila.findViewById(R.id.txvData);
            txvEstat = fila.findViewById(R.id.txvEstat);
            txvEntrada = fila.findViewById(R.id.txvEntrada);
            context = fila.getContext();
        }

        public void setOnClickListeners(String loginTocken, Integer idTasca, Integer idEntrada) {
            Bundle parametres = new Bundle();

            Intent intent = new Intent(context, NovaEntradaActivity.class);
            intent.putExtra("token", loginTocken);
            intent.putExtra("estat", "editar");
            intent.putExtra("idTasca", idTasca.toString());
            intent.putExtra("idEntrada", idEntrada.toString());
            context.startActivity(intent);
        }
    }

    private class ThreadEntrades implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(6);
                oos.writeObject(loginTocken);
                oos.writeObject(idTasca);
                mEntrades = (List<Entrada>)ois.readObject();

                Log.d("APP", "mEntrades: " + mEntrades);

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
