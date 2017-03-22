package com.rcoem.project.skymeet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.luseen.simplepermission.permissions.Permission;
import com.luseen.simplepermission.permissions.PermissionUtils;

/**
 * Created by Akshat Shukla on 16-03-2017.
 */

public class EditProfileFragment extends android.support.v4.app.Fragment {

    Activity context;
    View listView;

    private FloatingActionButton actionA;

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
        listView = inflater.inflate(R.layout.activity_profile_layout, container, false);

        actionA = (FloatingActionButton) listView.findViewById(R.id.action_1);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Toast.makeText(context,"Edit Profile here",Toast.LENGTH_SHORT).show();
            }
        });


        return listView;
    }
}
