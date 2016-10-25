package com.bgonline.bgfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AccountFragment extends SynchronizedLoadFragment {

    UserInfo userInfo;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    public AccountFragment() {
    }

    public void populateUserInfo(UserInfo userInfo) {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        userInfo.setUserId(sharedPref.getInt(getResources().getString(R.string.user_info_id), 0));

        if (userInfo.getUserId() == 213) {
            userInfo.setBirthDate("");
            userInfo.setBggAccount("");
            userInfo.setCity("");
            userInfo.setCountry("Israel");
            userInfo.setDescription("Please log in!");
            userInfo.setFirstName("");
            userInfo.setLastName("");
            userInfo.setEmail("You are not logged in");
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

        editor.putInt(getResources().getString(R.string.user_info_id), userInfo.getUserId());
        editor.putString(getResources().getString(R.string.user_info_first_name), userInfo.getFirstName());
        editor.putString(getResources().getString(R.string.user_info_last_name), userInfo.getLastName());
        editor.putString(getResources().getString(R.string.user_info_email), userInfo.getEmail());
        editor.putString(getResources().getString(R.string.user_info_bgg_account), userInfo.getBggAccount());
        editor.putString(getResources().getString(R.string.user_info_description), userInfo.getDescription());
        editor.putString(getResources().getString(R.string.user_info_country), userInfo.getCountry());
        editor.putString(getResources().getString(R.string.user_info_city), userInfo.getCity());
        editor.putString(getResources().getString(R.string.user_info_year_of_birth), userInfo.getBirthDate());

        editor.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userInfo = new UserInfo();
        populateUserInfo(userInfo);
        com.bgonline.bgfinder.databinding.UserInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_info, null, true);
        binding.setUserInfo(userInfo);

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        saveUserInfo(userInfo);
        userInfo = null;

        super.onDestroyView();
    }
}
