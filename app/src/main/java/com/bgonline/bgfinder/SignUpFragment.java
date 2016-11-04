package com.bgonline.bgfinder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpFragment extends SynchronizedLoadFragment {
    OnHeadlineSelectedListener mCallback;
    DatabaseReference database;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onChangeFragment(Fragment newFragment);
    }

    private static final String TAG = "BGFinderSignUpFragment";

    private FirebaseAuth mAuth;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();
        final View signUpView = inflater.inflate(R.layout.sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        final ViewGroup exContainer = container;

        final Button signUpButton = (Button) signUpView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText userNameField = (EditText) signUpView.findViewById(R.id.sign_up_user_name);
                final String userName = userNameField.getText().toString();

                database.child("userNames").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.getValue() == null) || (dataSnapshot.getValue() == "")) {
                            final EditText emailField = (EditText) signUpView.findViewById(R.id.sign_up_email);
                            String email = emailField.getText().toString();

                            final EditText passField = (EditText) signUpView.findViewById(R.id.sign_up_password);
                            final String password = passField.getText().toString();

                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(exContainer.getContext().getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(password)) {
                                Toast.makeText(exContainer.getContext().getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final ProgressBar progressBar = (ProgressBar) signUpView.findViewById(R.id.sign_up_progress_bar);
                            progressBar.setVisibility(View.VISIBLE);

                            //authenticate user
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Toast.makeText(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Authentication failed." + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "Account created! Should log in automatically now.");
                                                String userId = task.getResult().getUser().getUid();
                                                String email = task.getResult().getUser().getEmail();
                                                database.child("users").child(userId).child("country").setValue("Israel");
                                                database.child("users").child(userId).child("description").setValue("New User");
                                                database.child("users").child(userId).child("email").setValue(email);
                                                database.child("users").child(userId).child("userName").setValue(userName);

                                                database.child("userNames").child(userName).setValue(userId);

                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Sorry, user name already taken!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Sorry, user name already taken!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        final Button backButton = (Button)signUpView.findViewById(R.id.sign_up_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new LogInFragment());
            }
        });

        return signUpView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
