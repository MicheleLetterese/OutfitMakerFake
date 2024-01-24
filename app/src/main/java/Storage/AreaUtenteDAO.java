package Storage;

import static java.security.AccessController.getContext;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class AreaUtenteDAO {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;

    public AreaUtenteDAO() {
    }

    public AreaUtenteDAO(FirebaseAuth mAuth, FirebaseFirestore db) {
        this.mAuth = mAuth;
        this.db = db;
    }

    public Task<Boolean> ottieniDatiUtente(String uid, String nome, String cognome, String email, String telefono) {
        Log.d("AREAUTENTELOG", "Entra in ottieniDatiUtente");

        // Crea una nuova istanza di TaskCompletionSource
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

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

                                // Esegui l'operazione desiderata con i dati
                                // ...

                                // Completa il Task con successo
                                taskCompletionSource.setResult(true);
                            } else {
                                Log.w("AREAUTENTELOG", "Documento dell'utente corrente non trovato");
                                // Completa il Task con fallimento
                                taskCompletionSource.setResult(false);
                            }
                        } else {
                            Log.w("AREAUTENTELOG", "Errore durante il recupero del documento dell'utente corrente" + task.getException());
                            // Completa il Task con fallimento
                            taskCompletionSource.setResult(false);
                        }
                    }
                });

        return taskCompletionSource.getTask();
    }

    public Task<Boolean> modificaDati(String nome_nuovo, String cognome_nuovo, String telefono_nuovo) {
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Log.d("MODIFICA", "userId: " + uid);

            FirebaseFirestore.getInstance().collection("utenti")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    // Documento trovato, ottieni il riferimento al documento
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    DocumentReference userRef = document.getReference();

                                    // Dati da aggiornare
                                    Map<String, Object> nuoviDati = new HashMap<>();
                                    nuoviDati.put("nome", nome_nuovo);
                                    nuoviDati.put("cognome", cognome_nuovo);
                                    nuoviDati.put("telefono", telefono_nuovo);

                                    // Aggiorna i dati nel documento Firestore
                                    userRef.update(nuoviDati)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    taskCompletionSource.setResult(true);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("MODIFICA", "Errore durante l'aggiornamento dei dati utente", e);
                                                    taskCompletionSource.setResult(false);
                                                }
                                            });
                                } else {
                                    Log.e("MODIFICA", "Nessun documento corrispondente all'UID trovato");
                                    taskCompletionSource.setResult(false);
                                }
                            } else {
                                Log.e("MODIFICA", "Errore durante la query per l'UID", task.getException());
                                taskCompletionSource.setResult(false);
                            }
                        }
                    });
        } else {
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();
    }
}