package com.a25zsa.firebasetest.Firebase;

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

/**
 *this class takes care of writing data into the database, and reading data from the database
 */
public class FirebaseDataTransfer extends AppCompatActivity {

    /**
     * The Firebase database reference.
     */
    DatabaseReference firebase;
    /**
     * The Image number.
     */
    int imageNumber = 0;

    /**
     * Instantiates a new Firebase data transfer.
     */
    public FirebaseDataTransfer(){
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Push description to firebase database
     *
     * @param latLng      the latitude and longitude
     * @param description the description
     */
    public void pushDescriptionToFirebase(String latLng, String description){
        DatabaseReference descriptionDir = firebase.child("Description").child(latLng);
        descriptionDir.setValue(description);
    }


    /**
     * Database to comment list.
     *
     * @param latLng      the latitude and longitude
     * @param commentList the comment list
     * @param context     Interface to global information about an application environment
     */
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

    /**
     * Database to rating bar.
     *
     * @param latLng    the latitude and longitude
     * @param ratingBar the rating bar
     */
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

    /**
     * Database to text description.
     *
     * @param latLng   the latitude and longitude
     * @param textView the text view
     */
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

    /**
     * Push rating to firebase database
     *
     * @param latLng   the latitude and longitude
     * @param userName the user name
     * @param rating   the rating
     */
    public void pushRatingToFirebase(String latLng, String userName, float rating){
        DatabaseReference ratingDirectory = firebase.child("Rating").child(latLng).child(userName);
        ratingDirectory.setValue(rating);
    }

    /**
     * Push comment to firebase database
     *
     * @param latLng  the latitude and longitutde
     * @param comment the comment
     */
    public void pushCommentToFirebase(String latLng, String comment){
        DatabaseReference commentDirectory = firebase.child("Comments").child(latLng);
        commentDirectory.push().setValue(comment);
    }

}
