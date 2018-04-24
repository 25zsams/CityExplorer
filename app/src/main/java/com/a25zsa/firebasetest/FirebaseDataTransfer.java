package com.a25zsa.firebasetest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by 25zsa on 4/21/2018.
 */

public class FirebaseDataTransfer extends AppCompatActivity {

    DatabaseReference firebase;
    public FirebaseDataTransfer(){
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    public void pushDescriptionToFirebase(String latLng, String description){
        DatabaseReference descriptionDir = firebase.child("Description").child(latLng);
        descriptionDir.setValue(description);
    }


    public void databaseToCommentList(final String latLng, final ListView commentList, final Context context){
        final DatabaseReference directory = firebase.child("Comments");
        final String targetLocation = latLng;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(targetLocation)){
                    System.out.println("found target");
                    ArrayList<String> firebaseComments = new ArrayList<String>();
                    for(DataSnapshot comments: dataSnapshot.child(targetLocation).getChildren()){
                        System.out.println(comments.getValue().toString());
                        firebaseComments.add(comments.getValue().toString());
                    }
                    ArrayAdapter<String> list = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, firebaseComments);
                    commentList.setAdapter(list);
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        directory.addListenerForSingleValueEvent(eventListener);
    }

    public void databaseToRatingBar(String latLng, final RatingBar ratingBar){
        final DatabaseReference directory = firebase.child("Rating");
        final String targetLocation = latLng;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(targetLocation)){
                    float ratings = 0;
                    for(DataSnapshot people: dataSnapshot.child(targetLocation).getChildren()){
                        ratings += Double.parseDouble(people.getValue().toString().trim());
                    }
                    ratingBar.setRating(ratings/dataSnapshot.child(targetLocation).getChildrenCount());
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        directory.addListenerForSingleValueEvent(eventListener);
    }

    public void databaseToTextDescription(String latLng, final TextView textView){
        final DatabaseReference directory = firebase.child("Description");
        final String targetLocation = latLng;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(targetLocation)){
                    textView.setText(dataSnapshot.child(targetLocation).getValue().toString());
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        directory.addListenerForSingleValueEvent(eventListener);
    }

    public void pushRatingToFirebase(String latLng, String userName, float rating){
        DatabaseReference ratingDirectory = firebase.child("Rating").child(latLng).child(userName);
        ratingDirectory.setValue(rating);
    }

    public void pushCommentToFirebase(String latLng, String comment){
        DatabaseReference commentDirectory = firebase.child("Comments").child(latLng);
        commentDirectory.push().setValue(comment);
    }


}
