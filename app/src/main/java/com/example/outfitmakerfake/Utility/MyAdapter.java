package com.example.outfitmakerfake.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outfitmakerfake.Entity.Capo;
import com.example.outfitmakerfake.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Capo> capoArrayList;
    String idCapo;
    FirebaseFirestore db;
    FirebaseAuth auth;

    public MyAdapter(Context context, ArrayList<Capo> capoArrayList) {
        this.context = context;
        this.capoArrayList = capoArrayList;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.singolo_elemento_armadio, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Log.d("TAG", "Entra in onBindViewHolder");

        Capo capo = capoArrayList.get(position);
        idCapo = capoArrayList.get(position).getId_indumento();
        Log.d("TAG", "idCapo: " + idCapo);

        holder.nome.setText(capo.getNome_brand());
        holder.tipologia.setText(capo.getTipologia());
        holder.stagionalita.setText(capo.getStagionalita());
        holder.occasione.setText(capo.getOccasione());

        // Visualizza la lista di colori come una singola stringa separata da virgole
        String coloriString = TextUtils.join(", ", capo.getColori());
        holder.listaColori.setText(coloriString + " ");

        if(capo.getTipologia().equals("Felpa")){
            holder.immagine.setImageResource(R.drawable.felpa);
        } else if(capo.getTipologia().equals("T-shirt") && capo.getOccasione().equals("Elegante")){
            holder.immagine.setImageResource(R.drawable.polo);
        } else if(capo.getTipologia().equals("T-shirt")){
            holder.immagine.setImageResource(R.drawable.t_shirt);
        } else if(capo.getTipologia().equals("Maglia lunga") && capo.getOccasione().equals("Elegante")){
            holder.immagine.setImageResource(R.drawable.maglia_lunga_elegante);
        } else if(capo.getTipologia().equals("Maglia lunga")){
            holder.immagine.setImageResource(R.drawable.maglia_lunga);
        } else if(capo.getTipologia().equals("Giacca") && capo.getOccasione().equals("Elegante")){
            holder.immagine.setImageResource(R.drawable.giacca_elegante);
        } else if(capo.getTipologia().equals("Giacca")){
            holder.immagine.setImageResource(R.drawable.giacca);
        } else if(capo.getTipologia().equals("Camicia")){
            holder.immagine.setImageResource(R.drawable.camicia);
        } else if(capo.getTipologia().equals("Cappotto")){
            holder.immagine.setImageResource(R.drawable.cappotto);
        } else if(capo.getTipologia().equals("Jeans") || capo.getTipologia().equals("Pantalone")){
            holder.immagine.setImageResource(R.drawable.jeans);
        } else if(capo.getTipologia().equals("Gonna")){
            holder.immagine.setImageResource(R.drawable.gonna);
        } else if(capo.getTipologia().equals("Vestito corto")){
            holder.immagine.setImageResource(R.drawable.vestito_corto);
        } else if(capo.getTipologia().equals("Vestito lungo")){
            holder.immagine.setImageResource(R.drawable.vestito_lungo);
        } else if(capo.getTipologia().equals("Scarpe")){
            holder.immagine.setImageResource(R.drawable.scarpa_casual);
        }

        holder.immagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "id: " + capoArrayList.get(position).getId_indumento(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btn_elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Conferma eliminazione");
                builder.setMessage("Sei sicuro di voler eliminare questo indumento?");

                builder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            db.collection("capi")
                                    .document(capoArrayList.get(currentPosition).getId_indumento())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Rimuove l'elemento dalla lista utilizzando la posizione corrente
                                                capoArrayList.remove(currentPosition);
                                                // Notifica all'Adapter che i dati sono stati modificati
                                                notifyItemRemoved(currentPosition);
                                                Toast.makeText(context, "Indumento cancellato", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Errore: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Chiudi il dialog senza eseguire l'eliminazione
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return capoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome, listaColori, tipologia, stagionalita, occasione;
        ImageView immagine, btn_elimina;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            immagine = itemView.findViewById(R.id.immagineV);
            nome = itemView.findViewById(R.id.nomeTVL);
            listaColori = itemView.findViewById(R.id.coloriTVL);
            tipologia = itemView.findViewById(R.id.tipologiaTVL);
            stagionalita = itemView.findViewById(R.id.stagionalitàTVL);
            occasione = itemView.findViewById(R.id.occasioneTVL);

            btn_elimina = itemView.findViewById(R.id.rimuovi_btn);
        }
    }

}
