package com.a25zsa.firebasetest;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

    public void uploadImage(Uri imageToUpload, String imageName){
        firebaseStorage.child(imageName).putFile(imageToUpload);
    }

    public void storageImageToView(String markerHashLocation, final ImageView imageView){
        Task<Uri> downloadUrl = firebaseStorage.child(markerHashLocation).getDownloadUrl();
        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(imageView);

            }
        });

    }

}
