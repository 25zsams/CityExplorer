package com.a25zsa.firebasetest.GateKeeping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a25zsa.firebasetest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * this class takes care of new user sign up
 */
public class SignUp extends AppCompatActivity {


    /**
     * The User.
     */
    EditText user;
    /**
     * The Password
     */
    EditText pass;
    /**
     * confirm password field
     */
    EditText confirmPass;
    /**
     * The Sign up button
     */
    Button signUp;
    /**
     * The Back button.
     */
    Button backButton;
    /**
     * The Firebase database reference
     */
    DatabaseReference firebase;

    /**
     * Called when the activity is starting
     * @param savedInstanceState contains the data it most recently supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebase = FirebaseDatabase.getInstance().getReference("Accounts");
        backButton = (Button)findViewById(R.id.backButton);
        signUp = (Button) findViewById(R.id.signUp);
        user = (EditText) findViewById(R.id.userName);
        pass = (EditText) findViewById(R.id.password);
        confirmPass = (EditText) findViewById(R.id.confirmPassword);

        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent back = new Intent(SignUp.this, MainActivity.class);
                startActivity(back);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                verifiyNewUser();
            }
        });

    }

    /**
     * verifies the new user
     */
    private void verifiyNewUser(){
        final String checkUser = user.getText().toString().trim();
        final String checkPass = pass.getText().toString().trim();
        final String checkConfirmPass = confirmPass.getText().toString().trim();

        if(TextUtils.isEmpty(checkUser) || TextUtils.isEmpty(checkPass)){
            Toast.makeText(this, "Missing Field", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkPass.equals(checkConfirmPass)){
            Toast.makeText(this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
            return;
        }

        final DatabaseReference temp = firebase.child(checkUser);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //Log.d("FirebaseTest", "onDataChangeIf" );
                    UserAccount newUser = new UserAccount(checkUser, checkPass);
                    temp.setValue(newUser);
                    Toast.makeText(getBaseContext(), "Sign Up Success", Toast.LENGTH_SHORT).show();
                    Intent back = new Intent(SignUp.this, MainActivity.class);
                    startActivity(back);
                }
                else{
                    Log.d("FirebaseTest", "onDataChangeElse" );
                    Toast.makeText(getBaseContext(), "Name Taken", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        temp.addListenerForSingleValueEvent(eventListener);
    }
}
