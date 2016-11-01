package com.bgonline.bgfinder;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

public class UserImage implements Serializable{
    Bitmap bitmap;
    String userId;
    Context context;

    public UserImage(String userId, final RoundedImageView imageView, final Context context) {
        this.userId = userId;
        this.context = context;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://bgfinder-b11b2.appspot.com");
        StorageReference imageReference = storageRef.child("userPictures/" + userId + ".jpg");


        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
                imageView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                bitmap = null;
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image, null));
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }

    void applyOnView(ImageView view) {
        view.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
        view.setVisibility(View.VISIBLE);
    }
}
