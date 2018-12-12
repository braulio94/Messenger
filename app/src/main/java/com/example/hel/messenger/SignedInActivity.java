package com.example.hel.messenger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignedInActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    EditText editTextMessage;
    String username;
    String userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        editTextMessage = findViewById(R.id.edit_message);
        username = auth.getCurrentUser().getDisplayName();

        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Message message = new Message(editTextMessage.getText().toString(),username,userPhoto,null);
               databaseReference.child("messages").push().setValue(message);
               editTextMessage.setText("");

            }
        });
    }

}
