package com.bgonline.bgfinder;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.nio.ByteBuffer;

public class AccountFragment extends SynchronizedLoadFragment {
    UserInfo userInfo;
    com.bgonline.bgfinder.databinding.UserInfoBinding binding;
    String connectedUserId;
    DatabaseReference database;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    public AccountFragment() {
    }

    public void populateUserInfo() {
        database.child("users").child(connectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo.setFirstName((String)dataSnapshot.child("firstName").getValue());
                userInfo.setLastName((String)dataSnapshot.child("lastName").getValue());
                userInfo.setDescription((String)dataSnapshot.child("description").getValue());
                userInfo.setCity((String)dataSnapshot.child("city").getValue());
                userInfo.setCountry((String)dataSnapshot.child("country").getValue());
                userInfo.setBggAccount((String)dataSnapshot.child("bggUserName").getValue());
                userInfo.setBirthDate((String)dataSnapshot.child("birthDate").getValue());
                userInfo.setEmail((String)dataSnapshot.child("email").getValue());
                binding.invalidateAll();
                ProgressBar progressBar = (ProgressBar) binding.getRoot().findViewById(R.id.account_progress_bar);
                progressBar.setVisibility(View.GONE);
                binding.getRoot().setEnabled(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        userInfo.setUserId(sharedPref.getString(getResources().getString(R.string.user_info_id), ""));

        if (userInfo.getUserId().equals("")) {
            userInfo.setBirthDate("");
            userInfo.setBggAccount("");
            userInfo.setCity("");
            userInfo.setCountry("");
            userInfo.setDescription("");
            userInfo.setFirstName("");
            userInfo.setLastName("");
            userInfo.setEmail("");
        } else {
            userInfo.setFirstName(sharedPref.getString(getResources().getString(R.string.user_info_first_name), ""));
            userInfo.setLastName(sharedPref.getString(getResources().getString(R.string.user_info_last_name), ""));
            userInfo.setEmail(sharedPref.getString(getResources().getString(R.string.user_info_email), ""));
            userInfo.setBggAccount(sharedPref.getString(getResources().getString(R.string.user_info_bgg_account), ""));
            userInfo.setDescription(sharedPref.getString(getResources().getString(R.string.user_info_description), ""));
            userInfo.setCountry(sharedPref.getString(getResources().getString(R.string.user_info_country), ""));
            userInfo.setCity(sharedPref.getString(getResources().getString(R.string.user_info_city), ""));
            userInfo.setBirthDate(sharedPref.getString(getResources().getString(R.string.user_info_year_of_birth), ""));
        }
    }

    public void saveUserInfo(UserInfo userInfo) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getResources().getString(R.string.user_info_id), userInfo.getUserId());
        editor.putString(getResources().getString(R.string.user_info_first_name), userInfo.getFirstName());
        editor.putString(getResources().getString(R.string.user_info_last_name), userInfo.getLastName());
        editor.putString(getResources().getString(R.string.user_info_email), userInfo.getEmail());
        editor.putString(getResources().getString(R.string.user_info_bgg_account), userInfo.getBggAccount());
        editor.putString(getResources().getString(R.string.user_info_description), userInfo.getDescription());
        editor.putString(getResources().getString(R.string.user_info_country), userInfo.getCountry());
        editor.putString(getResources().getString(R.string.user_info_city), userInfo.getCity());
        editor.putString(getResources().getString(R.string.user_info_year_of_birth), userInfo.getBirthDate());

        editor.commit();

        DatabaseReference user = database.child("users").child(connectedUserId);
        user.child("firstName").setValue(userInfo.getFirstName());
        user.child("lastName").setValue(userInfo.getLastName());
        user.child("description").setValue(userInfo.getDescription());
        user.child("city").setValue(userInfo.getCity());
        //TODO: user.child("country").setValue(userInfo.getFirstName());
        user.child("bggUserName").setValue(userInfo.getBggAccount());
        user.child("birthDate").setValue(userInfo.getBirthDate());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connectedUserId = getArguments().getString("connectedUserId");
        database = FirebaseDatabase.getInstance().getReference();
        userInfo = new UserInfo();
        populateUserInfo();
        binding = DataBindingUtil.inflate(inflater, R.layout.user_info, null, true);
        binding.setUserInfo(userInfo);

        binding.getRoot().setEnabled(false);

        UserImage image = (UserImage) getArguments().getSerializable("currentUserImage");

        ProgressBar progressBar = (ProgressBar) binding.getRoot().findViewById(R.id.account_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        ImageView userImageView = (ImageView) binding.getRoot().findViewById(R.id.user_info_image);
        UserImage newImage = new UserImage(image.userId, userImageView, getContext());

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getActivity(), "There was a problem with the selected picture", Toast.LENGTH_LONG);
                return;
            }
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://bgfinder-b11b2.appspot.com");
                StorageReference imageReference = storageRef.child("userPictures/" + connectedUserId + ".jpg");

                Uri uri = data.getData();

                imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageView userImageView = (ImageView) binding.getRoot().findViewById(R.id.user_info_image);
                        UserImage newImage = new UserImage(connectedUserId, userImageView, getContext());
                    }
                });
            }
            catch (Exception e) {
                Log.e("", "WTF?");
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageButton bggButton = (ImageButton) binding.getRoot().findViewById(R.id.bgg_button);
        bggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.boardgamegeek.com/user/" + userInfo.getBggAccount();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        saveUserInfo(userInfo);
        userInfo = null;

        super.onDestroyView();
    }
}
