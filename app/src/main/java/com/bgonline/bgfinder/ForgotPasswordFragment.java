package com.bgonline.bgfinder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Manu on 10/24/2016.
 */

public class ForgotPasswordFragment extends SynchronizedLoadFragment {
    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onChangeFragment(Fragment newFragment);
    }

    private static final String TAG = "BGFinderForgotPasswordFragment";

    private FirebaseAuth mAuth;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    public ForgotPasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View forgotPasswordView = inflater.inflate(R.layout.forgot_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        final ViewGroup exContainer = container;

        final Button resetPasswordButton = (Button)forgotPasswordView.findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)forgotPasswordView.findViewById(R.id.email)).getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(exContainer.getContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressBar progressBar = (ProgressBar) forgotPasswordView.findViewById(R.id.forgot_password_progressBar);
                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    mCallback.onChangeFragment(new LogInFragment());
                                } else {
                                    Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });


        final Button backButton = (Button)forgotPasswordView.findViewById(R.id.forgot_password_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onChangeFragment(new LogInFragment());
            }
        });

        return forgotPasswordView;
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
