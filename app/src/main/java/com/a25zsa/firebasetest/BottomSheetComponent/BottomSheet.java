package com.a25zsa.firebasetest.BottomSheetComponent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.a25zsa.firebasetest.Firebase.FirebaseDataTransfer;
import com.a25zsa.firebasetest.Firebase.FirebaseStorageTransfer;
import com.a25zsa.firebasetest.R;
import com.a25zsa.firebasetest.UserReviews.ReviewActivity;

import static android.app.Activity.RESULT_OK;


/**
 * Created by 25zsa on 4/17/2018.
 */

/**
 * A bottom sheet is a component that slides up from bottom of the screen to reveal more content
 */

public class BottomSheet extends BottomSheetDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * The Image transfer.
     */
    FirebaseStorageTransfer imageTransfer;
    /**
     * The Text transfer.
     */
    FirebaseDataTransfer textTransfer;

    /**
     * The Image view.
     */
    ImageView imageView;
    /**
     * The View pager.
     */
    ViewPager viewPager;
    /**
     * The Upload.
     */
    Button upload;
    /**
     * The Description button.
     */
    Button descriptionButton;
    /**
     * The Review button.
     */
    Button reviewButton;
    /**
     * The Description box.
     */
    TextView descriptionBox;
    /**
     * The Rating bar.
     */
    RatingBar ratingBar;
    /**
     * The Marker hash location.
     */
    String markerHashLocation;
    /**
     * The User name.
     */
    String userName;

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater Instantiates a layout XML file into its corresponding View objects
     * @param container this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state
     * @return Return the View for the fragment's UI
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        //imageView = view.findViewById(R.id.imageView);
        viewPager = view.findViewById(R.id.viewPager);
        upload = view.findViewById(R.id.uploadButton);
        descriptionButton = view.findViewById(R.id.descriptionButton);
        reviewButton = view.findViewById(R.id.reviewButton);
        descriptionBox = view.findViewById(R.id.descriptionBox);
        ratingBar = view.findViewById(R.id.ratingBar);
        imageTransfer = new FirebaseStorageTransfer();
        textTransfer = new FirebaseDataTransfer();
        markerHashLocation = getArguments().getString("latLng");
        userName = getArguments().getString("userName");
        //imageTransfer.storageImageToView(markerHashLocation, imageView);
        imageTransfer.storageImageToViewPager(markerHashLocation, viewPager, getContext());
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

        if(userName.equals("")){
            guestRestriction();
        }
        return view;
    }

    /**
     * sets the guest restriction
     */
    private void guestRestriction(){
        descriptionButton.setVisibility(View.INVISIBLE);
        upload.setVisibility(View.INVISIBLE);
    }

    /**
     * opens the file chooser
     */
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Called when an activity you launched exits
     * @param requestCode The integer request code originally supplied to startActivityForResult()
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri pickImage = data.getData();
            imageTransfer.uploadImage(pickImage, markerHashLocation);
            //imageView.setImageURI(pickImage);
            this.dismiss();

        }
    }
}
