package Storage;

import android.util.Log;

import com.example.outfitmakerfake.Entity.Capo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArmadioDAO {

    String idArmadio = "";
    String uid;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    public ArmadioDAO() {

    }

    public ArmadioDAO(FirebaseAuth mAuth, FirebaseFirestore db) {
        this.mAuth = mAuth;
        this.db = db;
    }

    public void creaArmadioFirestore(String idArmadio) {
        Map<String, Object> armadio = new HashMap<>();
        armadio.put("idArmadio", idArmadio);
        armadio.put("listaCapi", new ArrayList<>()); // Inizializza la lista dei capi come vuota

        db.collection("armadi")
                .document(idArmadio)
                .set(armadio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("CAZZOCULO", "Documento Armadio aggiunto con successo");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CAZZOCULO", "Errore durante l'aggiunta del documento Armadio + (" + e + ")");
                    }
                });
    }

    public Task<Boolean> aggiungiCapo(String nomeBrand, List<String> colori, String tipologia, String stagionalita, String occasione) {


        if (currentUser != null) {
            // L'utente è attualmente autenticato
            uid = currentUser.getUid();
            Log.d("INDUMENTO", "UID corrente: " + uid);

            // Riferimento al documento utente nella collezione "utenti" basato sull'UID
            DocumentReference utenteRef = FirebaseFirestore.getInstance().collection("utenti").document(uid);

            utenteRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Il documento utente esiste
                                idArmadio = documentSnapshot.getString("idArmadio");
                                if (idArmadio != null) {
                                    // Hai ottenuto con successo l'idArmadio associato all'utente
                                    Log.d("INDUMENTO", "idArmadio: " + idArmadio);
                                } else {
                                    // "idArmadio" non è presente nel documento o è null
                                    Log.d("INDUMENTO", "idArmadio non disponibile per l'utente");
                                }
                            } else {
                                // Il documento utente non esiste
                                Log.d("INDUMENTO", "Documento utente non trovato");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            // Gestisci l'errore durante la lettura del documento
                            Log.e("ERRORE", "Errore durante la lettura del documento utente", e);
                        }
                    });
        } else {
            // Nessun utente autenticato al momento
            Log.d("INDUMENTO", "Nessun utente attualmente autenticato");
        }

        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        // Genera un nuovo ID univoco per il capo
        String idCapo = generateUniqueIndumentoId();

        // Creare un nuovo documento per il capo nella raccolta "capi"
        Map<String, Object> capoMap = new HashMap<>();
        capoMap.put("id_indumento", idCapo);
        capoMap.put("nome_brand", nomeBrand);
        capoMap.put("colori", colori);
        capoMap.put("tipologia", tipologia);
        capoMap.put("stagionalita", stagionalita);
        capoMap.put("occasione", occasione);
        capoMap.put("uid", uid);

        // Nota: Non è possibile memorizzare direttamente un'immagine in Firestore, quindi potresti doverla salvare separatamente, ad esempio su Firebase Storage, e memorizzare solo il percorso o l'URL dell'immagine nel documento.

        db.collection("capi")
                .document(idCapo)
                .set(capoMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskCompletionSource.setResult(true);
                    } else {
                        Log.d("CAZZOCULO", "Errore durante l'aggiunta del capo al documento: " + task.getException());
                        taskCompletionSource.setResult(false);
                    }
                });
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> aggiungiCapoArmadio(String idArmadio, Capo nuovoCapo){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        db.collection("armadi")
                .document(idArmadio)
                .update("listaCapo", FieldValue.arrayUnion(nuovoCapo))
                .addOnSuccessListener(aVoid -> {
                    taskCompletionSource.setResult(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore durante l'aggiunta del capo alla listaCapo", e);
                    taskCompletionSource.setResult(false);
                });
        return taskCompletionSource.getTask();
    }

    public String generateUniqueArmadioId() {
        return UUID.randomUUID().toString();
    }

    public String generateUniqueIndumentoId() {
        return UUID.randomUUID().toString();
    }
}