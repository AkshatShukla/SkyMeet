package com.rcoem.project.skymeet;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Akshat Shukla on 15-03-2017.
 */

public class ListFragment extends Fragment {

    View listView;
    Activity context;
    public ListFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
