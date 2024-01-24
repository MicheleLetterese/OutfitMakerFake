package com.example.outfitmakerfake.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.outfitmakerfake.Home;
import com.example.outfitmakerfake.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import Storage.AreaUtenteDAO;
import Storage.AreaUtenteService;

public class FragmentModificaCapo extends Fragment {

    AreaUtenteService areaUtenteService;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText nome_nuovo;
    EditText cognome_nuovo;
    EditText telefono_nuovo;
    Button btn_modifica_dati;
    ProgressBar progressBarModifica;

    public FragmentModificaCapo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frammento_modifica_utente, container, false);
        nome_nuovo = v.findViewById(R.id.editTextNomeNuovo);
        cognome_nuovo = v.findViewById(R.id.editTextCognomeNuovo);
        telefono_nuovo = v.findViewById(R.id.editTextTelefonoNuovo);
        btn_modifica_dati = v.findViewById(R.id.btn_modifica_dati);
        progressBarModifica = v.findViewById(R.id.progressBarModifica);

        areaUtenteService = new AreaUtenteService(new AreaUtenteDAO());

        btn_modifica_dati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarModifica.setVisibility(View.VISIBLE);

                String nome = nome_nuovo.getText().toString().trim();
                String cognome = cognome_nuovo.getText().toString().trim();
                String telefono = telefono_nuovo.getText().toString().trim();

                if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(telefono)) {
                    Toast.makeText(getContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nome.length() < 2 || nome.length() > 15) {
                    Toast.makeText(getContext(), "Nome troppo lungo o troppo corto", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cognome.length() < 2 || cognome.length() > 15) {
                    Toast.makeText(getContext(), "Cognome troppo lungo o troppo corto", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!telefono.matches("\\d{10}")) {
                    Toast.makeText(getContext(), "Telefono deve contenere 10 cifre numeriche", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("MODIFICA", "Nome: " + nome + " Cognome: " + cognome + " Telefono: " + telefono);
                areaUtenteService.modificaDatiUtente(nome, cognome, telefono).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        Log.d("MODIFICA", "entra in onClick");
                        if (task.isSuccessful()) {
                            boolean modificaRiuscita = task.getResult();
                            Log.d("MODIFICA", "task = " + modificaRiuscita);
                            if (modificaRiuscita) {
                                Log.d("MODIFICA", "entra in modificaRiuscita");
                                progressBarModifica.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Dati modificati con successo", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getContext(), Home.class);
                                startActivity(i);
                            } else {
                                progressBarModifica.setVisibility(View.INVISIBLE);
                                Log.d("MODIFICA", "entra in modificaErrore");
                                Toast.makeText(getContext(), "Errore durante la modifica dei dati", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBarModifica.setVisibility(View.INVISIBLE);
                            Exception exception = task.getException();
                            Toast.makeText(getContext(), "Errore: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return v;
    }
}