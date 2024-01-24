package com.example.outfitmakerfake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import Storage.LoginService;
import Storage.UtenteDAO;

public class LoginController extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText emailET;
    EditText passwordET;
    String email;
    String password;
    ProgressBar progressBar;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        emailET = findViewById(R.id.editTextEmailLogin);
        passwordET = findViewById(R.id.editTextPasswordLogin);
        progressBar = findViewById(R.id.progressBarL);

        loginService = new LoginService(new UtenteDAO());


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LOGIN", "entro in onStart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("LOGIN", "entro nell'if di onStart");
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }
        Log.d("LOGIN", "esco dall'if di onStart");
    }

    public void inserisciDatiLog(View v){
        progressBar.setVisibility(View.VISIBLE);
        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Email non valida", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (password.length() < 6 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            Toast.makeText(getApplicationContext(), "Password errata", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        loginService.effettuaLogin(email, password)
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()) {
                            Toast.makeText(getApplicationContext(), "Login avvenuto con successo", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), Home.class);
                            boolean isAdmin=loginService.isUtenteAdmin();
                            gestisciAccesso(isAdmin);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Errore durante il login", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                });
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void gestisciAccesso(boolean isAdmin){
        if(isAdmin){
            //l'utente è amministratore
            Intent i=new Intent(getApplicationContext(), SchermataAdmin.class);
            startActivity(i);
            finish();
        }else{
            //utente è un utente normale
            Intent i=new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }
    }
    public void vaiRegistrazione(View v){
        Intent i = new Intent(getApplicationContext(), RegistrazioneController.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }
}