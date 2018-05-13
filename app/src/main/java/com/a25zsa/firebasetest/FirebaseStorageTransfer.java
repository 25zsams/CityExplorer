package com.a25zsa.firebasetest;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 25zsa on 4/19/2018.
 */

public class FirebaseStorageTransfer extends AppCompatActivity {
    private Uri mImageUri;
    private StorageReference firebaseStorage;

    public FirebaseStorageTransfer(){
        firebaseStorage = FirebaseStorage.getInstance().getReference("Image");

    }

    public String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadImage(Uri imageToUpload, String imageLocationFolder){
        String imageHashTag = UUID.randomUUID().toString();
        firebaseStorage.child(imageLocationFolder).child(imageHashTag).putFile(imageToUpload);
        pushUriOnDatabase(imageLocationFolder, imageHashTag);
    }

    public void pushUriOnDatabase(final String imageLocationFolder, final String imageHashTag){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("Image").child(imageLocationFolder);
        firebase.push().setValue(imageHashTag);
    }

    public void storageImageToView(String markerHashLocation, final ImageView imageView){
        Task<Uri> downloadUrl = firebaseStorage.child(markerHashLocation).getDownloadUrl();
        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(imageView);

            }
        });
    }

    public void storageImageToViewPager(final String markerHashLocation, final ViewPager viewPager, final Context context){
        DatabaseReference imageDirectory = FirebaseDatabase.getInstance().getReference("Image/" + markerHashLocation);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){

                    final LinkedList<String> imageHashTag = new LinkedList<>();

                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        //imageHashTag[imageIndex] = child.getValue().toString();
                        System.out.println("the child value is " + child.getValue().toString());

                        Task<Uri> downloadUrl = firebaseStorage.child(markerHashLocation).child(child.getValue().toString()).getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println("inner uri is  " + uri.toString());
                                final String temp = uri.toString();
                                imageHashTag.add(temp);
                                ViewPagerAdapter adapter = new ViewPagerAdapter(context, imageHashTag);
                                viewPager.setAdapter(adapter);

                            }
                        });
                    }

                    for(String x: imageHashTag){
                        System.out.println( "the imageurl is " + x);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        imageDirectory.addListenerForSingleValueEvent(eventListener);

//        String[] url = new String[]{
//            "https://firebasestorage.googleapis.com/v0/b/testing-fbafa.appspot.com/o/Image%2F37%2C0%20-122%2C0%2F06a59972-7537-40a3-a3e4-fa4044a59eca?alt=media&token=a8bbb9f9-ed82-451c-8c06-dd2c8eca44cb",
//                "https://firebasestorage.googleapis.com/v0/b/testing-fbafa.appspot.com/o/Image%2F37%2C0%20-122%2C0%2F379144ef-dc68-4e70-8f57-40894c13f052?alt=media&token=d9224f1a-308e-4cf8-8b6a-adf452555898"
//        };
//        ViewPagerAdapter adapter = new ViewPagerAdapter(context, url);
//        viewPager.setAdapter(adapter);
    }

}
