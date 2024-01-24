package com.example.outfitmakerfake;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outfitmakerfake.Entity.Capo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.example.outfitmakerfake.Utility.FragmentInserisciCapo;
import com.example.outfitmakerfake.Utility.MyAdapter;

public class ArmadioController extends AppCompatActivity {
    FragmentManager fm;
    FrameLayout contenitoreFrammento;
    private Boolean attivatoFrammento = false;

    RecyclerView recyclerView;
    ArrayList<Capo> capoArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armadio);

        contenitoreFrammento = findViewById(R.id.contenitoreFrammento);
        fm = getSupportFragmentManager();

        recyclerView = findViewById(R.id.recycleList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        capoArrayList = new ArrayList<Capo>();
        myAdapter = new MyAdapter(ArmadioController.this, capoArrayList);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

    }

    private void EventChangeListener() {
        String uid = currentUser.getUid();;

        db.collection("capi")
                .whereEqualTo("uid", uid)
                .orderBy("tipologia", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.d("LISTA", "Firestore error: " + error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                capoArrayList.add(dc.getDocument().toObject(Capo.class));
                            }

                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void inserisciCapoClicked(View v) {
        attivatoFrammento = true;
        contenitoreFrammento.setVisibility(View.VISIBLE);
        FragmentInserisciCapo ic = new FragmentInserisciCapo();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.contenitoreFrammento, ic, "InserisciCapo");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void rimuoviFrammentoClicked(View v){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment ic = fm.findFragmentByTag("InserisciCapo");
        if(ic != null)
            ft.remove(ic);
        else
            Toast.makeText(this, "Inserire prima un FragmentA", Toast.LENGTH_SHORT).show();
        contenitoreFrammento.setVisibility(View.GONE);
        ft.commit();
    }
}