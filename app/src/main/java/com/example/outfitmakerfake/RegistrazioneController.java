package com.example.outfitmakerfake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import Storage.RegistrazioneService;
import Storage.UtenteDAO;
import Utility.NetworkUtil;

public class RegistrazioneController extends AppCompatActivity {

    EditText nomeET;
    EditText cognomeET;
    EditText emailET;
    EditText passwordET;
    EditText telefonoET;
    ProgressBar progressBar;

    String uid;
    String nome;
    String cognome;
    String email;
    String password;
    String telefono;

    Boolean isUser;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private RegistrazioneService registrazioneService;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrazione);

        progressBar = findViewById(R.id.progressBarR);
        nomeET = findViewById(R.id.editTextNome);
        cognomeET = findViewById(R.id.editTextCognome);
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        telefonoET = findViewById(R.id.editTextTelefono);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        registrazioneService = new RegistrazioneService(new UtenteDAO());
    }

    public void inserisciDati(View v) {
        Log.d("UTENTE", "Entra in inserisciDati");

        if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Connessione Internet assente", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        nome = nomeET.getText().toString();
        cognome = cognomeET.getText().toString();
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        telefono = telefonoET.getText().toString();


        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (nome.length() < 3 || nome.length() > 15 || cognome.length() < 3 || cognome.length() > 15) {
            Toast.makeText(getApplicationContext(), "Nome e cognome devono avere da 3 a 15 caratteri", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Formato email non valido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            Toast.makeText(getApplicationContext(), "La password deve avere almeno 6 caratteri, un numero e un simbolo", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (telefono.length() != 10) {
            Toast.makeText(getApplicationContext(), "Il telefono deve avere 10 caratteri", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        registrazioneService.creaUtente(nome, cognome, email, password, telefono)
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean registrazioneSuccesso = task.getResult();

                            if (registrazioneSuccesso) {
                                Toast.makeText(getApplicationContext(), "Registrazione avvenuta con successo", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), Home.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Errore nella registrazione", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Gestione dell'errore nella registrazione
                            Exception exception = task.getException();
                            Toast.makeText(getApplicationContext(), "Errore: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void vaiLogin(View v) {
        Intent i = new Intent(getApplicationContext(), LoginController.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }
}