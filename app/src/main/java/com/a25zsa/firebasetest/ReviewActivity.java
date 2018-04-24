package com.a25zsa.firebasetest;

import android.content.Context;
import android.drm.DrmStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    String userName;
    String markerHashLocation;
    Button commentButton;
    Button rateButton;
    EditText commentBox;
    RatingBar ratingBar;
    ListView commentList;
    FirebaseDataTransfer firebaseDataTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        firebaseDataTransfer = new FirebaseDataTransfer();
        userName = getIntent().getExtras().getString("userName");
        markerHashLocation = getIntent().getExtras().getString("latLng");
        commentButton = (Button)findViewById(R.id.commentButton);
        rateButton = (Button)findViewById(R.id.rateButton);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        commentBox = (EditText)findViewById(R.id.commentBox);
        commentList = (ListView)findViewById(R.id.commentList);


        getSupportActionBar().setTitle(" Review");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(userName.equals("")){
                    Toast.makeText(getBaseContext(), "Must sign in to submit rating!", Toast.LENGTH_LONG).show();
                }
                else{
                    submitRatings();
                }
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(userName.equals("")){
                    Toast.makeText(getBaseContext(), "Must sign in to post comment!", Toast.LENGTH_LONG).show();
                }
                else{
                    hideTypePad();
                    postComment();
                    firebaseDataTransfer.databaseToCommentList(markerHashLocation, commentList, getBaseContext());
                }
            }
        });

        loadCommentList();
        hideTypePad();

    }

    public void loadCommentList(){
        firebaseDataTransfer.databaseToCommentList(markerHashLocation, commentList, getBaseContext());
    }

    public void hideTypePad(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String createCommentFormat(){
        String timeStamp = new SimpleDateFormat("[MM/dd/yyyy]").format(Calendar.getInstance().getTime());
        return timeStamp + " " + userName + ": " + commentBox.getText().toString();
    }

    public void postComment(){
        firebaseDataTransfer.pushCommentToFirebase(markerHashLocation, createCommentFormat());
        Toast.makeText(getBaseContext(), "Your comment has been posted", Toast.LENGTH_LONG).show();
    }

    public void submitRatings(){
        firebaseDataTransfer.pushRatingToFirebase(markerHashLocation, userName, ratingBar.getRating());
        Toast.makeText(getBaseContext(), "Your rating has been updated", Toast.LENGTH_LONG).show();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        System.out.println("testing in onOptionItemSelected");
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
