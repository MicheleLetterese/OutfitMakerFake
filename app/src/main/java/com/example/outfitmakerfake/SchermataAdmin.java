package com.example.outfitmakerfake;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SchermataAdmin extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView nome_cognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paginaamministratore);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        nome_cognome = findViewById(R.id.nome_cognome);

        if(user == null){
            Intent i = new Intent(getApplicationContext(), LoginController.class);
            startActivity(i);
            finish();
        } else {
            nome_cognome.setText(user.getEmail());
        }
    }

    public void armadioClicked(View v){
        Intent i = new Intent(getApplicationContext(), ArmadioController.class);
        startActivity(i);
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
            setContentView(R.layout.paginaamministratore);
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
