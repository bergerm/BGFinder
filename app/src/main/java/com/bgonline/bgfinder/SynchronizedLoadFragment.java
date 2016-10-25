package com.bgonline.bgfinder;

import android.app.Fragment;
import android.content.Context;

import java.util.ArrayList;

/* this class is used to synchronize a fragment destruction which updates the activity using callbacks and the
construction of a new fragment after it. This new fragment might need the updated values from the destructed one.
 */
public class SynchronizedLoadFragment extends Fragment {

    OnHeadlineSelectedListener mSynchronizedLoadCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSynchronizedFragmentLoad();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mSynchronizedLoadCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSynchronizedLoadCallback.onSynchronizedFragmentLoad();
    }
}
