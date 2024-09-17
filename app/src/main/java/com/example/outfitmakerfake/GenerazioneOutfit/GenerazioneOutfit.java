package com.example.outfitmakerfake.GenerazioneOutfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.outfitmakerfake.AreaUtente.AreaUtenteController;
import com.example.outfitmakerfake.Entity.Capo;
import com.example.outfitmakerfake.Home;
import com.example.outfitmakerfake.R;
import com.example.outfitmakerfake.Utility.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Storage.Armadio.ArmadioDAO;
import Storage.Armadio.ArmadioService;

public class GenerazioneOutfit extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String uid;
    ArmadioService armadioService;
    RadioGroup radioGroup_colore;
    RadioGroup radioGroup_stagionalita;
    RadioGroup radioGroup_occasione;
    RadioGroup radioGroup_tipologia;
    ProgressBar progressBar;
    Button btn_genera_outfit;

    ArrayList<Capo> lista_capi;

    String colore = "";
    String stagionalita = "";
    String occasione = "";
    String tipologia = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generazione_outfit);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        uid = currentUser.getUid();

        armadioService = new ArmadioService(new ArmadioDAO());

        radioGroup_colore = findViewById(R.id.radioGroupColore_Gen);
        radioGroup_stagionalita = findViewById(R.id.radioGroupStagionalita_Gen);
        radioGroup_occasione = findViewById(R.id.radioGroupOccasione_Gen);
        radioGroup_tipologia = findViewById(R.id.radioGroupTipologia_Gen);
        progressBar = findViewById(R.id.progressBar_generazione_outfit);

        btn_genera_outfit = findViewById(R.id.btn_genera_outfit);

        btn_genera_outfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Connessione Internet assente", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //COLORE
                int selectedRadioButtonIdColore = radioGroup_colore.getCheckedRadioButtonId();
                if (selectedRadioButtonIdColore != -1) {
                    RadioButton selectedRadioButtonColore = findViewById(selectedRadioButtonIdColore);
                    colore = selectedRadioButtonColore.getText().toString();
                    Log.d("GENNN", "Colore: " + colore);
                } else {
                    Toast.makeText(getApplicationContext(), "Inserire il colore", Toast.LENGTH_SHORT).show();
                    return;
                }

                //STAGIONALITA
                int selectedRadioButtonIdStag = radioGroup_stagionalita.getCheckedRadioButtonId();
                if (selectedRadioButtonIdStag != -1) {
                    RadioButton selectedRadioButtonStag = findViewById(selectedRadioButtonIdStag);
                    stagionalita = selectedRadioButtonStag.getText().toString();
                    Log.d("GENNN", "Staginalità: " + stagionalita);
                } else {
                    Toast.makeText(getApplicationContext(), "Inserire la Stagionalità", Toast.LENGTH_SHORT).show();
                    return;
                }

                //OCCASIONE
                int selectedRadioButtonIdOcc = radioGroup_occasione.getCheckedRadioButtonId();
                if (selectedRadioButtonIdOcc != -1) {
                    RadioButton selectedRadioButtonOcc = findViewById(selectedRadioButtonIdOcc);
                    occasione = selectedRadioButtonOcc.getText().toString();
                    Log.d("GENNN", "Occasione: " + occasione);
                } else {
                    Toast.makeText(getApplicationContext(), "Inserire l'Occasione", Toast.LENGTH_SHORT).show();
                    return;
                }

                //TIPOLOGIA
               int selectedRadioButtonIdTip = radioGroup_tipologia.getCheckedRadioButtonId();
                if (selectedRadioButtonIdTip != -1) {
                    RadioButton selectedRadioButtonTip = findViewById(selectedRadioButtonIdTip);
                    tipologia = selectedRadioButtonTip.getText().toString();
                    Log.d("GENNN", "Tipologia: " + tipologia);
                }

                try {
                    armadioService.generaOutfit(stagionalita).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            lista_capi = task.getResult();

                            if (lista_capi.size() != 3) {
                                Toast.makeText(getApplicationContext(), "Capi non presenti per creare l'outfit", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Outfit creato con successo", Toast.LENGTH_SHORT).show();
                                Log.d("GENNN", "Tipologia: " + lista_capi.get(0).getTipologia() + " Stagionalita: " + lista_capi.get(0).getStagionalita() + "Nome: " + lista_capi.get(0).getNome_brand());
                                Log.d("GENNN", "Tipologia: " + lista_capi.get(1).getTipologia() + " Stagionalita: " + lista_capi.get(1).getStagionalita() + "Nome: " + lista_capi.get(1).getNome_brand());
                                Log.d("GENNN", "Tipologia: " + lista_capi.get(2).getTipologia() + " Stagionalita: " + lista_capi.get(2).getStagionalita() + "Nome: " + lista_capi.get(2).getNome_brand());
                                Intent i = new Intent(getApplicationContext(), OutfitGenerato.class);
                                i.putParcelableArrayListExtra("lISTA_CAPI", lista_capi);
                                startActivity(i);
                            }
                        } else {
                            Log.d("GENNN", "Error in generaOutfit: " + task.getException());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                } catch (Exception e) {
                    Log.d("GENNN", "Exception: " + e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menu1){
            setContentView(R.layout.home);
            return true;
        } else if(id == R.id.menu2){
            Intent i = new Intent(getApplicationContext(), AreaUtenteController.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            return true;
        } else if(id == R.id.menu3){
            setContentView(R.layout.assistenza);
            return true;
        } else if(id == R.id.menu4){
            setContentView(R.layout.istruzioni);
        }
        return super.onOptionsItemSelected(item);
    }
}