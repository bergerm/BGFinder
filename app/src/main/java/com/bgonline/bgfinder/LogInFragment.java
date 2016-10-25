package com.bgonline.bgfinder;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends SynchronizedLoadFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onChangeFragment(Fragment newFragment);
    }

    private static final String TAG = "BGFinderLogInFragment";

    private FirebaseAuth mAuth;

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    public LogInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View logInView = inflater.inflate(R.layout.log_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        final ViewGroup exContainer = container;

        final Button logInButton = (Button)logInView.findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText emailField = (EditText)logInView.findViewById(R.id.log_in_email);
                String email = emailField.getText().toString();

                final EditText passField = (EditText)logInView.findViewById(R.id.log_in_password);
                final String password = passField.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(exContainer.getContext().getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(exContainer.getContext().getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressBar progressBar = (ProgressBar) logInView.findViewById(R.id.log_in_progressBar);
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        passField.setError(getString(R.string.password_minimum));
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Welcome Back!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        Button forgotPasswordButton = (Button)logInView.findViewById(R.id.log_in_forgot_password_button);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new ForgotPasswordFragment());
            }
        });

        Button signUpButton = (Button)logInView.findViewById(R.id.log_in_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new SignUpFragment());
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return logInView;
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
