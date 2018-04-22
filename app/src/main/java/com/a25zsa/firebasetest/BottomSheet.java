package com.a25zsa.firebasetest;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 25zsa on 4/17/2018.
 */

public class BottomSheet extends BottomSheetDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    FirebaseStorageTransfer imageTransfer;
    FirebaseDataTransfer textTransfer;

    ImageView imageView;
    Button upload;
    Button descriptionButton;
    Button reviewButton;
    TextView descriptionBox;
    RatingBar ratingBar;
    String markerHashLocation;
    String userName;
    //something is not working.
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        imageView = view.findViewById(R.id.imageView);
        upload = view.findViewById(R.id.uploadButton);
        descriptionButton = view.findViewById(R.id.descriptionButton);
        reviewButton = view.findViewById(R.id.reviewButton);
        descriptionBox = view.findViewById(R.id.descriptionBox);
        ratingBar = view.findViewById(R.id.ratingBar);
        imageTransfer = new FirebaseStorageTransfer();
        textTransfer = new FirebaseDataTransfer();
        markerHashLocation = getArguments().getString("latLng");
        userName = getArguments().getString("userName");
        imageTransfer.storageImageToView(markerHashLocation, imageView);
        textTransfer.databaseToTextDescription(markerHashLocation, descriptionBox);
        textTransfer.databaseToRatingBar(markerHashLocation, ratingBar);

        reviewButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getContext(), ReviewActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("latLng", markerHashLocation);
                startActivity(intent);
            }
        });

        upload.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view1){
                if(userName.equals("")){
                    Toast.makeText(getActivity(), "Must sign in to use this feature.", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    openFileChooser();
                }
            }
        });

        descriptionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view1){
                if(userName.equals("")){
                    Toast.makeText(getActivity(), "Must sign in to use this feature.", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(getContext(), DescriptionActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("latLng", markerHashLocation);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    public void testing(){
        descriptionBox.setText("Testing. this is hard. it is taking me forever, but i think i got something working.");
    }

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri pickImage = data.getData();
            imageTransfer.uploadImage(pickImage, markerHashLocation);
            imageView.setImageURI(pickImage);

        }
    }
}
