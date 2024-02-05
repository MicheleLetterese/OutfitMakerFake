package com.example.outfitmakerfake.AreaUtente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.outfitmakerfake.Home;
import com.example.outfitmakerfake.Utente.LoginController;
import com.example.outfitmakerfake.R;
import com.example.outfitmakerfake.Utility.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import Storage.Utente.UtenteDAO;

public class AreaUtenteController extends AppCompatActivity {
    FragmentManager fm;

    FrameLayout frammento_modifica_utente, frammento_areautente_admin;

    //FrameLayout frammento_modifica_utente;

    public String nome, cognome, email, telefono;
    TextView nomeTV, cognomeTV, emailTV, telefonoTV;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AREAUTENTELOG", "onCreate AreaUtente");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_utente);

        UtenteDAO utenteDAO=new UtenteDAO(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());

        if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Connessione Internet assente", Toast.LENGTH_SHORT).show();
            return;
        }

        utenteDAO.isUtenteAdmin().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Boolean isAdmin=task.getResult();
                Log.d("isAdmin1" ,"isAdmin:" + isAdmin);
                if(isAdmin){
                    Log.d("isAdmin1" ,"isAdmin:" + isAdmin);
                    frammento_areautente_admin=findViewById(R.id.contenitoreAreaAdmin);
                    if(frammento_areautente_admin!=null){
                        frammento_areautente_admin.setVisibility(View.VISIBLE);
                        FragmentAreaAdmin fragmentAreaAdmin=new FragmentAreaAdmin();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.contenitoreAreaAdmin, fragmentAreaAdmin, "AreaAdmin");
                        ft.addToBackStack(null);
                        ft.commit();
                        Log.d("isAdmin1" ,"isAdmin:" + isAdmin);
                    } else {
                        Log.d("isAdmin1", "Frammento_modifica_utente è null");
                    }
                }
            } else {
                Log.d("isAdmin1", "Errore durante la verifica dell'admin");
            }
        });


        frammento_modifica_utente = findViewById(R.id.contenitoreModificaDati);

        fm = getSupportFragmentManager();
        nomeTV = findViewById(R.id.nomeAU);
        cognomeTV = findViewById(R.id.cognomeAU);
        emailTV = findViewById(R.id.emailAU);
        telefonoTV = findViewById(R.id.telefonoAU);
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            uid = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            Log.d("AREAUTENTELOG", uid);

            ottieniDatiUtente();
        }
    }

    public void ottieniDatiUtente(){
        Log.d("AREAUTENTELOG", "Entra in ottieniDatiUtente");
        // Utilizza il campo uid per cercare l'utente
        db.collection("utenti")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("AREAUTENTELOG", "entrato in onComplete");
                        if (task.isSuccessful()) {
                            Log.d("AREAUTENTELOG", "entrato in isSuccessful");
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Dovrebbe esserci un solo documento, ma è comunque possibile gestire più risultati
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                Log.d("AREAUTENTELOG", "Il documento esiste");
                                nome = document.getString("nome");
                                cognome = document.getString("cognome");
                                email = document.getString("email");
                                telefono = document.getString("telefono");

                                Log.d("AREAUTENTELOG", "Nome: " + nome);
                                // Ora che ho i dati, posso chiamare settaDatiUtente per aggiornare le TextView
                                settaDatiUtente();
                            } else {
                                Log.w("AREAUTENTELOG", "Documento dell'utente corrente non trovato");
                            }
                        } else {
                            Log.w("AREAUTENTELOG", "Errore durante il recupero del documento dell'utente corrente" + task.getException());
                        }
                    }
                });
    }

    public void settaDatiUtente(){
        Log.d("AREAUTENTELOG", "Entra in settaDatiUtente");
        nomeTV.setText("Nome: " + nome);
        cognomeTV.setText("Cognome: " + cognome);
        emailTV.setText("Email: " + email);
        telefonoTV.setText("Telefono: " + telefono);
    }

    public void inserimentoModificaDati(View v){
        frammento_modifica_utente.setVisibility(View.VISIBLE);
        FragmentModificaUtente modificaCapo = new FragmentModificaUtente();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.contenitoreModificaDati, modificaCapo, "ModificaDati");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void rimuoviFrammentoModifica(View v){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment modificaCapo = fm.findFragmentByTag("ModificaDati");
        if(modificaCapo != null)
            ft.remove(modificaCapo);
        else
            Toast.makeText(this, "Inserire prima un Fragment", Toast.LENGTH_SHORT).show();
            frammento_modifica_utente.setVisibility(View.GONE);
            ft.commit();
    }

    public void disconnessioneClicked(View v){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), LoginController.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
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
            Intent i = new Intent(getApplicationContext(), Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
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