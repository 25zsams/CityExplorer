package com.a25zsa.firebasetest.BottomSheetComponent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a25zsa.firebasetest.MainMap.CreateMapMarker;
import com.a25zsa.firebasetest.Firebase.FirebaseDataTransfer;
import com.a25zsa.firebasetest.R;

public class DescriptionActivity extends AppCompatActivity {

    EditText descriptionEditBox;
    Button confirmButton;
    Button backButton;
    String userName;
    String markerHashLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        descriptionEditBox = (EditText)findViewById(R.id.descriptionEditBox);
        confirmButton = (Button)findViewById(R.id.confirmButton);
        backButton = (Button) findViewById(R.id.backButton);
        userName = getIntent().getExtras().getString("userName");
        markerHashLocation = getIntent().getExtras().getString("latLng");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMapPage();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setConfirmDescription();
                goBackToMapPage();
            }
        });

    }

    public void goBackToMapPage(){
        Intent createMarker = new Intent(DescriptionActivity.this, CreateMapMarker.class);
        createMarker.putExtra("userName", userName);
        startActivity(createMarker);
    }

    public void setConfirmDescription(){
        FirebaseDataTransfer pushData = new FirebaseDataTransfer();
        pushData.pushDescriptionToFirebase(markerHashLocation, descriptionEditBox.getText().toString());
        Toast.makeText(this, "Your description is now display in the front marker page!", Toast.LENGTH_LONG).show();
    }

}
