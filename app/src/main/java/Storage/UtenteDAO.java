package Storage;

import android.util.Log;

import com.example.outfitmakerfake.Entity.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UtenteDAO {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Utente utente;
    String idArmadio;
    Boolean isAdmin=false;
    public ArmadioDAO armadioDAO = new ArmadioDAO(mAuth, db);
    public UtenteDAO() {
    }


    public UtenteDAO(FirebaseAuth mAuth, FirebaseFirestore db) {
        this.mAuth = mAuth;
        this.db = db;
    }

    public Task<Boolean> creaUtente(String nome, String cognome, String email, String password, String telefono) {
        Log.d("UTENTE", "Entra in creaUtente");

        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("UTENTE", "Entro in onComplete()");
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();

                                idArmadio = armadioDAO.generateUniqueArmadioId();

                                Log.d("UTENTE", "UID in creaUtenteFirestore: " + uid);
                                creaUtenteFirestore(uid, nome, cognome, email, password, telefono, idArmadio, isAdmin);
                                utente = new Utente(uid, nome, cognome, email, password, telefono, idArmadio);
                                taskCompletionSource.setResult(true);
                            } else {
                                Log.d("UTENTE", "Errore: currentUser è null");
                                taskCompletionSource.setResult(false);
                            }
                        } else {
                            Log.d("CREAUTENTE", "Errore durante la creazione dell'utente " + email + " " + password);
                            taskCompletionSource.setResult(false);
                        }
                        Exception exception = task.getException();
                        Log.d("UTENTE", "Errore in onComplete() " + exception);
                    }
                });

        return taskCompletionSource.getTask();
    }

    public void creaUtenteFirestore(String uid, String nome, String cognome, String email, String password, String telefono, String idArmadio, Boolean isAdmin) {

        //nel caso in cui non trovi la collezione di utenti la va a creare, va a creare il documento in base agli id degli utenti
        // una volta eseguito il metodo firebase va a creare il nuovo utente che ha il nuovo id
        //DocumentReference dR=db.collection("Utenti").document(utente.getId());
        //armadioDAO=new ArmadioDAO();
        Log.d("UTENTE", "Entra in creaUtenteFirestore");
        Map<String, Object> utente = new HashMap<>();
        utente.put("uid", uid);
        utente.put("nome", nome);
        utente.put("cognome", cognome);
        utente.put("email", email);
        utente.put("password", password);
        utente.put("telefono", telefono);
        utente.put("idArmadio", idArmadio);// Aggiorna con il nuovo campo
        utente.put("isAdmin", isAdmin);
        Log.d("UTENTE", "Legge i dati");

        db.collection("utenti")
                .add(utente)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UTENTE", "Armadio creato con ID: " + documentReference.getId());
                        armadioDAO.creaArmadioFirestore(idArmadio);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UTENTE", "Errore durante l'aggiunta del documento + (" + e + ")");
                    }
                });
    }

    public Task<Boolean> effettuaLogin(String email, String password) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=mAuth.getCurrentUser();
                            if(user!=null){
                                verificaRuoloUtente(user, taskCompletionSource);
                            }else {
                                taskCompletionSource.setResult(false);
                            }
                        } else {

                            taskCompletionSource.setResult(false);
                        }
                        }
                });

        return taskCompletionSource.getTask();
    }


    public void verificaRuoloUtente(FirebaseUser user, TaskCompletionSource<Boolean> taskCompletionSource){
        String uid=user.getUid();
        db.collection("utenti").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document= task.getResult();
                    if(document!=null){
                        boolean isAdmin= document.getBoolean("isAdmin");
                        taskCompletionSource.setResult(isAdmin);
                    }else{
                        taskCompletionSource.setResult(false);
                    }
                }else{
                    taskCompletionSource.setResult(false);
                }
            }
        });
    }
    public String getIdArmadio(){
        return this.idArmadio;
    }

}