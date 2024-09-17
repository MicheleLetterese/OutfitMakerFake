package com.example.outfitmakerfake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.outfitmakerfake.Archivio.Archivio;
import com.example.outfitmakerfake.AreaUtente.AreaUtenteController;
import com.example.outfitmakerfake.Armadio.ArmadioController;
import com.example.outfitmakerfake.CreazioneOutfit.CreazioneOutfitController;
import com.example.outfitmakerfake.GenerazioneOutfit.GenerazioneOutfit;
import com.example.outfitmakerfake.Utente.LoginController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import Storage.Utente.UtenteDAO;

public class Home extends AppCompatActivity {

    boolean appDisattiva;
    FirebaseAuth auth;
    UtenteDAO utenteDAO;
    FirebaseUser user;
    TextView nome_cognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        nome_cognome = findViewById(R.id.nome_cognome);



        if(user == null){
            Intent i = new Intent(getApplicationContext(), LoginController.class);
            startActivity(i);
            finish();
        } else {
            nome_cognome.setText(user.getEmail());
        }



    }

    public void generazioneCliked(View v) {
        utenteDAO = new UtenteDAO(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
        utenteDAO.getAppDisattivaFromFirestore()
                .addOnSuccessListener(appDisattiva -> {
                    if (appDisattiva) {
                        Log.d("Homegenerazionedisab", "l'app è disattiva " + appDisattiva);
                        Toast.makeText(this, "L'app è stata momentaneamente disabilitata", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Homegenerazionedisab", "l'app non è disattiva " + appDisattiva);
                        Intent i = new Intent(getApplicationContext(), GenerazioneOutfit.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i);
                    }
                });
    }

    public void creazioneClicked(View v) {
        utenteDAO = new UtenteDAO(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
        utenteDAO.getAppDisattivaFromFirestore()
                .addOnSuccessListener(appDisattiva -> {
                    if (appDisattiva) {
                        Log.d("Homecreazionedisab", "l'app è disattiva " + appDisattiva);
                        Toast.makeText(this, "L'app è stata momentaneamente disabilitata", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Homecreazionedisab", "l'app non è disattiva " + appDisattiva);
                        Intent i = new Intent(getApplicationContext(), CreazioneOutfitController.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i);
                    }
                });
    }

    public void armadioClicked(View v) {
        utenteDAO = new UtenteDAO(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
        utenteDAO.getAppDisattivaFromFirestore()
                .addOnSuccessListener(appDisattiva -> {
                    if (appDisattiva) {
                        Log.d("Homearmadiodisab", "l'app è disattiva " + appDisattiva);
                        Toast.makeText(this, "L'app è stata momentaneamente disabilitata", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Homegenerazionedisab", "l'app non è disattiva " + appDisattiva);
                        Intent i = new Intent(getApplicationContext(), ArmadioController.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i);
                    }
                });
    }

    public void archivioClicked(View v){
        utenteDAO = new UtenteDAO(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
        utenteDAO.getAppDisattivaFromFirestore()
                .addOnSuccessListener(appDisattiva -> {
                    if (appDisattiva) {
                        Log.d("Homearchiviodisab", "l'app è disattiva " + appDisattiva);
                        Toast.makeText(this, "L'app è stata momentaneamente disabilitata", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(getApplicationContext(), Archivio.class);
                        startActivity(i);
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
