package com.example.outfitmakerfake.Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.outfitmakerfake.ArmadioController;
import com.example.outfitmakerfake.Entity.Capo;
import com.example.outfitmakerfake.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Storage.ArmadioDAO;
import Storage.ArmadioService;
import Storage.UtenteDAO;

import java.util.ArrayList;
import java.util.List;

public class FragmentInserisciCapo extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Ottieni l'utente corrente
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final int REQUEST_CODE = 14;
    Button buttonInserisciCapo;
    ImageView immagineCapo;
    Button buttonCreaIndumento;
    GridLayout gridLayout;
    RadioGroup radioGroupStag;
    RadioGroup radioGroupOccasione;
    RadioGroup radioGroupTipo;
    TextInputEditText nomeEditText;

    ArmadioService armadioService;
    UtenteDAO utenteDAO;
    Capo capo;

    String tipoSelezionato = "";
    String stagioneSelezionata = "";
    String occasioneSelezionata = "";
    Bitmap immagine = null;

    public FragmentInserisciCapo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frammeto_inserisci_capo, container, false);
        immagineCapo = v.findViewById(R.id.immagineCapo);
        buttonInserisciCapo = v.findViewById(R.id.btnImmagineCapo);
        buttonCreaIndumento = v.findViewById(R.id.btnCreaIndumento);

        gridLayout = v.findViewById(R.id.gridLayoutColore);
        radioGroupStag = v.findViewById(R.id.radioGroupStag);
        radioGroupOccasione = v.findViewById(R.id.radioGroupOccasione);
        radioGroupTipo = v.findViewById(R.id.radioGroup);
        nomeEditText = v.findViewById(R.id.editTextEmailLogin);

        utenteDAO = new UtenteDAO();

        armadioService = new ArmadioService(new ArmadioDAO());

        buttonInserisciCapo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            }
        });

        buttonCreaIndumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("INDUMENTO", "Entra in onClick");
                int childCount = gridLayout.getChildCount();
                List<String> coloriSelezionati = new ArrayList<>();

                for (int i = 0; i < childCount; i++) {
                    View childView = gridLayout.getChildAt(i);

                    if (childView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) childView;

                        if (checkBox.isChecked()) {
                            Log.d("INDUMENTO", "Entro nell'if per prendere i colori");

                            String color = checkBox.getText().toString();
                            coloriSelezionati.add(color);
                            Log.d("INDUMENTO", "Colore: " + color);
                        }
                    }
                }
                Log.d("INDUMENTO", "Esco dal for colori");


                if (coloriSelezionati.isEmpty()) {
                    Log.d("INDUMENTO", "Errore colore");
                    Toast.makeText(getContext(), "Inserire il colore", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("INDUMENTO", "Pre brandIndumento");
                String brandIndumento = nomeEditText.getText().toString();
                Log.d("INDUMENTO", "brandIndumento: " + brandIndumento);


                if (TextUtils.isEmpty(brandIndumento)) {
                    Log.d("INDUMENTO", "Errore nome");
                    Toast.makeText(getContext(), "Inserire il nome del Brand", Toast.LENGTH_SHORT).show();
                    return;
                } else if (brandIndumento.length() < 2 || brandIndumento.length() > 15) {
                    Log.d("INDUMENTO", "Errore nome");
                    Toast.makeText(getContext(), "Nome troppo lungo o troppo corto", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedRadioButtonIdStag = radioGroupStag.getCheckedRadioButtonId();

                if (selectedRadioButtonIdStag != -1) {
                    RadioButton selectedRadioButtonStag = getView().findViewById(selectedRadioButtonIdStag);
                    stagioneSelezionata = selectedRadioButtonStag.getText().toString();
                    Log.d("INDUMENTO", "Stagionalità: " + stagioneSelezionata);
                } else {
                    Toast.makeText(getContext(), "Inserire la Stagionalità", Toast.LENGTH_SHORT).show();
                    Log.d("INDUMENTO", "Errore stagione");
                    return;
                }

                int selectedRadioButtonIdOcc = radioGroupOccasione.getCheckedRadioButtonId();

                if (selectedRadioButtonIdOcc != -1) {
                    RadioButton selectedRadioButtonOcc = getView().findViewById(selectedRadioButtonIdOcc);
                    occasioneSelezionata = selectedRadioButtonOcc.getText().toString();
                    Log.d("INDUMENTO", "Occasione: " + occasioneSelezionata);

                } else {
                    Toast.makeText(getContext(), "Inserire l'Occasione", Toast.LENGTH_SHORT).show();
                    Log.d("INDUMENTO", "Errore occasione");
                    return;
                }

                int selectedRadioButtonIdTipo = radioGroupTipo.getCheckedRadioButtonId();

                if (selectedRadioButtonIdTipo != -1) {
                    RadioButton selectedRadioButtonTipo = getView().findViewById(selectedRadioButtonIdTipo);
                    tipoSelezionato = selectedRadioButtonTipo.getText().toString();
                    Log.d("INDUMENTO", "Tipologia: " + tipoSelezionato);

                } else {
                    Toast.makeText(getContext(), "Inserire la Tipologia", Toast.LENGTH_SHORT).show();
                    Log.d("INDUMENTO", "Errore tipologia");
                    return;
                }

                Drawable drawable = immagineCapo.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    immagine = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    immagine = BitmapFactory.decodeResource(getResources(), R.drawable.felpa);
                }

                armadioService.aggiungiCapo(brandIndumento, coloriSelezionati, tipoSelezionato, stagioneSelezionata, occasioneSelezionata)
                        .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                            @Override
                            public void onComplete(@NonNull Task<Boolean> task) {
                                Log.d("INDUMENTO", "Entra in onComplete di aggiungiCapo");
                                if(task.isSuccessful()) {
                                    boolean creazioneIndumento = task.getResult();
                                    Log.d("INDUMENTO", "creazioneIndumento = " + creazioneIndumento);


                                    if(creazioneIndumento) {
                                        capo = new Capo(brandIndumento, coloriSelezionati, tipoSelezionato, stagioneSelezionata, occasioneSelezionata);
                                        Log.d("INDUMENTO", "entra nell'if di creazioneIndumento");
                                        //armadioService.aggiungiCapoInArmadio(utenteDAO.getIdArmadio(), capo);
                                        Toast.makeText(getContext(), "Indumento creat con successo", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getContext(), ArmadioController.class);
                                        startActivity(i);
                                    } else {
                                        Log.d("INDUMENTO", "Entra in else di creazioneIndumento");
                                        Toast.makeText(getContext(), "Errore nella creazione dell'Indumento", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Exception exception = task.getException();
                                    Log.d("INDUMENTO", "else in onComplete exception: " + exception);
                                    Toast.makeText(getContext(), "Errore: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("INDUMENTO", "entro in onActivityResult");
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("INDUMENTO", "entra in nell'if di onActivityResult");
            Bitmap foto = (Bitmap) data.getExtras().get("data");
            immagineCapo.setImageBitmap(foto);
        } else {
            Log.d("INDUMENTO", "entra nell'else di onActivityResult");
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.d("INDUMENTO", "esco da if/else di onActivityResult");
    }

    private float calcolaDimensioneImmagine(Bitmap immagine) {
        return immagine.getByteCount() / (1024f * 1024f);
    }
}