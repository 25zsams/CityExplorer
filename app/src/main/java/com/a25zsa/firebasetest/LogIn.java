package com.a25zsa.firebasetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {


    EditText user;
    EditText pass;
    Button signIn;
    Button backButton;
    DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        user = (EditText)findViewById(R.id.logInUserName);
        pass = (EditText)findViewById(R.id.logInPassword);
        signIn = (Button)findViewById(R.id.signInButton);
        backButton = (Button)findViewById(R.id.backButton);
        firebase = FirebaseDatabase.getInstance().getReference("Accounts");

        signIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                verifyLogIn();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent back = new Intent(LogIn.this, MainActivity.class);
                startActivity(back);
            }
        });


    }


    public void verifyLogIn(){
        Log.d("Firebase", "start verifyLogIn");
        final String checkUser = user.getText().toString().trim();
        final String checkPass = pass.getText().toString().trim();
        final DatabaseReference temp = firebase;

        if(TextUtils.isEmpty(checkUser) || TextUtils.isEmpty(checkPass)){
            Toast.makeText(this, "Missing Field", Toast.LENGTH_SHORT).show();
            return;
        }

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(checkUser)) {
                    Log.d("Firebase", "true");
                    Object value = dataSnapshot.child(checkUser).child("password").getValue();
                    String t = value.toString();

                    if(t.equals(checkPass)){
                        //Toast.makeText(getBaseContext(), "Logging in, need next page", Toast.LENGTH_LONG).show();
                        Intent createMarker = new Intent(LogIn.this, CreateMapMarker.class);
                        startActivity(createMarker);
                    }
                    else{
                        Toast.makeText(getBaseContext(), "Incorrect name or pass", Toast.LENGTH_LONG).show();
                    }


                } else {

                    Toast.makeText(getBaseContext(), "Incorrect name or pass", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener eventListener = valueEventListener;
        temp.addListenerForSingleValueEvent(eventListener);
        Log.d("Firebase", "start LogInEnd");
    }
}
