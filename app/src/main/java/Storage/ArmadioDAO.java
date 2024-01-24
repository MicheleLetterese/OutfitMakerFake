package Storage;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.outfitmakerfake.Entity.Armadio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArmadioDAO {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public Task<Boolean> aggiungiCapo(String idIndumento, String nomeBrand, List<String> colori, String tipologia, String stagionalita, String occasione) {
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

    public String generateUniqueArmadioId() {
        return UUID.randomUUID().toString();
    }

    public String generateUniqueIndumentoId() {
        return UUID.randomUUID().toString();
    }
}