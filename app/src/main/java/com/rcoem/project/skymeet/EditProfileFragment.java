package com.rcoem.project.skymeet;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Akshat Shukla on 16-03-2017.
 */

public class EditProfileFragment extends android.support.v4.app.Fragment {

    Activity context;
    View listView;

    public EditProfileFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getActivity();
        listView = inflater.inflate(R.layout.activity_list_view, container, false);

        return listView;
    }
}
