package com.example.appandroidclient.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroidclient.MainActivity;
import com.example.appandroidclient.R;

import org.milaifontanals.model.Projecte;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProjecteAdapter extends RecyclerView.Adapter<ProjecteAdapter.ViewHolder> {

    private List<Projecte> mProjectesList;
    private int mPosSeleccionada = -1;
    private ThreadProjectes threadProjectes;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String ip = "192.168.1.103";
    private Integer port = 5056;
    private String loginTocken;

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public ProjecteAdapter(String loginTocken) {
        this.loginTocken = loginTocken;
        threadProjectes = new ThreadProjectes();
        new Thread(threadProjectes).start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fila = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_projecte, parent, false);
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
                    Log.d("APP", "M'han clicat POS: " + mPosSeleccionada);
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
        Projecte p = mProjectesList.get(position);
        holder.txvId.setText(p.getId().toString());
        holder.txvNom.setText(p.getNom());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rcyTasquesAssignades.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        layoutManager.setInitialPrefetchItemCount(p.getSizeTasques());

        TascaAssignadaAdapter tascaAssignadaAdapter = new TascaAssignadaAdapter(loginTocken, p.getId());

        holder.rcyTasquesAssignades.setLayoutManager(layoutManager);
        holder.rcyTasquesAssignades.setAdapter(tascaAssignadaAdapter);
        holder.rcyTasquesAssignades.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return mProjectesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txvId;
        public TextView txvNom;
        public RecyclerView rcyTasquesAssignades;

        public ViewHolder(@NonNull View fila) {
            super(fila);
            txvId =  fila.findViewById(R.id.txvId);
            txvNom =  fila.findViewById(R.id.txvNom);
            rcyTasquesAssignades = fila.findViewById(R.id.rcyTasquesAssignades);
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
                mProjectesList = (List<Projecte>)ois.readObject();

                Log.d("APP", "mProjectesList: " + mProjectesList);

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
