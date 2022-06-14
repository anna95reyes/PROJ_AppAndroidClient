package com.example.appandroidclient.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroidclient.R;

import org.milaifontanals.model.Projecte;
import org.milaifontanals.model.Tasca;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TascaAssignadaAdapter extends RecyclerView.Adapter<TascaAssignadaAdapter.ViewHolder> {
    private List<Tasca> mTasquesAssignadesList;
    private int mPosSeleccionada = -1;
    private ThreadTasquesAssignades threadTasquesAssignades;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "192.168.1.103";
    private Integer port = 5056;
    private String loginTocken;
    private Integer projecteId;

    public TascaAssignadaAdapter (String loginTocken, Integer projecteId){
        this.loginTocken = loginTocken;
        this.projecteId = projecteId;
        threadTasquesAssignades = new ThreadTasquesAssignades();
        new Thread(threadTasquesAssignades).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_tasques, parent, false);
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
        Tasca t = mTasquesAssignadesList.get(position);
        holder.txvNom.setText(t.getNom());
        if (t.getDescripcio() != null) {
            holder.txvSeparador.setText(" - ");
        }
        holder.txvDesc.setText(t.getDescripcio());
    }

    @Override
    public int getItemCount() {
        return mTasquesAssignadesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txvNom;
        public TextView txvSeparador;
        public TextView txvDesc;

        public ViewHolder(@NonNull View fila) {
            super(fila);
            txvNom =  fila.findViewById(R.id.txvNom);
            txvSeparador = fila.findViewById(R.id.txvSeparador);
            txvDesc =  fila.findViewById(R.id.txvDesc);
        }
    }

    private class ThreadTasquesAssignades implements Runnable {

        @Override
        public void run() {

            try {
                //InetAddress ip = InetAddress.getByName("localhost");
                socket = new Socket(ip, port);

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                oos.writeObject(3);
                oos.writeObject(loginTocken);
                oos.writeObject(projecteId);
                mTasquesAssignadesList = (List<Tasca>)ois.readObject();

                Log.d("APP", "mTasquesAssignadesList: " + mTasquesAssignadesList);

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
