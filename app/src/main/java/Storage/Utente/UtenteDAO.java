package Storage.Utente;

import android.util.Log;

import com.example.outfitmakerfake.Entity.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Storage.Archivio.ArchivioDAO;
import Storage.Archivio.ArchivioService;
import Storage.Armadio.ArmadioDAO;

public class UtenteDAO {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Utente utente;
    String idArmadio;
    String idArchivio;
    public ArmadioDAO armadioDAO = new ArmadioDAO(mAuth, db);
    public ArchivioService archivioService = new ArchivioService(new ArchivioDAO());

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
                                idArchivio = archivioService.generateUniqueArchivioId();

                                creaUtenteFirestore(uid, nome, cognome, email, password, telefono, idArmadio, idArchivio);
                                utente = new Utente(uid, nome, cognome, email, password, telefono, idArmadio, idArchivio);
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

    public void creaUtenteFirestore(String uid, String nome, String cognome, String email, String password, String telefono, String idArmadio, String idArchivio) {

        //armadioDAO=new ArmadioDAO();
        Log.d("UTENTE", "Entra in creaUtenteFirestore");
        Map<String, Object> utente = new HashMap<>();
        utente.put("uid", uid);
        utente.put("nome", nome);
        utente.put("cognome", cognome);
        utente.put("email", email);
        utente.put("password", password);
        utente.put("telefono", telefono);
        utente.put("idArmadio", idArmadio);
        utente.put("idArchivio", idArchivio);
        utente.put("isAdmin", false);
        utente.put("appDisattiva", false);
        // Aggiorna con il nuovo campo

        db.collection("utenti")
                .add(utente)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UTENTE", "Armadio creato con ID: " + documentReference.getId());
                        armadioDAO.creaArmadioFirestore(idArmadio);
                        archivioService.creaArchivioFirestore(idArchivio, uid);
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
                            taskCompletionSource.setResult(true);
                        } else {
                            taskCompletionSource.setResult(false);
                        }
                    }
                });

        return taskCompletionSource.getTask();
    }

    public String getIdArmadioUtente() {
        if (utente != null) {
            return utente.getIdArmadio();
        }
        return null; // o gestisci questo caso in modo appropriato per il tuo scenario
    }

    public Task<Boolean> isUtenteAdmin() {
        final TaskCompletionSource taskCompletionSource = new TaskCompletionSource<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("utenti")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                // Dovrebbe esserci un solo documento, ma puoi gestire più risultati se necessario
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                // Ottieni il valore booleano dal campo "isAdmin"
                                Boolean isAdmin = document.getBoolean("isAdmin");

                                // Verifica se il valore è true o false
                                if (isAdmin != null) {
                                    Log.d("utenteadmin", "l'utente è un " + isAdmin);
                                    taskCompletionSource.setResult(isAdmin);
                                } else {
                                    // Il campo "isAdmin" non è presente o è null, consideralo come false
                                    Log.d("utenteadmin", "// Il campo \"isAdmin\" non è presente o è null, consideralo come false");
                                    taskCompletionSource.setResult(false);
                                }
                            } else {
                                Log.d("utenteadmin", "Nessun documento trovato");
                                // Nessun documento trovato, considera come false
                                taskCompletionSource.setResult(false);
                            }
                        } else {
                            Log.d("utenteadmin", "altri errori a cazzo");
                            // Gestisci l'errore durante il recupero del documento utente
                            taskCompletionSource.setResult(false);
                        }
                    });
        } else {
            // L'utente non è autenticato, considera come false
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();
    }

    public Task<Void> setBooleanChanges(boolean appDisattiva) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource<>();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d("utentenullo", "l'utente è nullo :(");

            FirebaseFirestore.getInstance()
                    .collection("utenti")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Task<Void>> updateTasks = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                // Aggiorna il campo appDisattiva per tutti gli utenti
                                updateTasks.add(document.getReference().update("appDisattiva", appDisattiva));
                            }

                            // Combina tutte le attività di aggiornamento in una singola attività
                            Tasks.whenAll(updateTasks)
                                    .addOnCompleteListener(combinedTask -> {
                                        if (combinedTask.isSuccessful()) {
                                            taskCompletionSource.setResult(null);
                                        } else {
                                            taskCompletionSource.setException(combinedTask.getException());
                                        }
                                    });
                        } else {
                            taskCompletionSource.setException(task.getException());
                        }
                    });
        }

            return taskCompletionSource.getTask();
        }


    public Task<Boolean> getAppDisattivaFromFirestore() {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("utenti")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Dovrebbe esserci un solo documento, ma puoi gestire più risultati se necessario
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Ottieni il valore booleano dal campo "appDisattiva"
                            Boolean appDisattiva = document.getBoolean("appDisattiva");

                            // Verifica se il valore è true o false
                            if (appDisattiva != null) {
                                Log.d("appDisattiva", "Il valore di appDisattiva è " + appDisattiva);
                                taskCompletionSource.setResult(appDisattiva);
                            } else {
                                // Il campo "appDisattiva" non è presente o è null, considera come false
                                Log.d("appDisattiva", "Il campo \"appDisattiva\" non è presente o è null, considera come false");
                                taskCompletionSource.setResult(false);
                            }
                        } else {
                            Log.d("appDisattiva", "Nessun documento trovato");
                            // Nessun documento trovato, considera come false
                            taskCompletionSource.setResult(false);
                        }
                    } else {
                        Log.d("appDisattiva", "Altri errori durante il recupero del documento utente");
                        // Gestisci l'errore durante il recupero del documento utente
                        taskCompletionSource.setResult(false);
                    }
                });

        return taskCompletionSource.getTask();
    }




}