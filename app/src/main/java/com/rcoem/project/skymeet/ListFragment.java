package com.rcoem.project.skymeet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.luseen.simplepermission.permissions.MultiplePermissionCallback;
import com.luseen.simplepermission.permissions.Permission;
import com.luseen.simplepermission.permissions.PermissionFragment;
import com.luseen.simplepermission.permissions.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshat Shukla on 15-03-2017.
 */

public class ListFragment extends PermissionFragment {

    View listView;
    Activity context;

    private ListView list;
    private List<String> stringsList = new ArrayList<>();

    private FloatingActionButton actionA;
    private FloatingActionButton actionB;

    private Boolean isPermissionGranted;

    public ListFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getActivity();
        listView = inflater.inflate(R.layout.activity_list_view, container, false);

        isPermissionGranted = false;

        Permission[] permissions = {Permission.COARSE_LOCATION, Permission.FINE_LOCATION, Permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, new MultiplePermissionCallback() {
            @Override
            public void onPermissionGranted(boolean allPermissionsGranted, List<Permission> grantedPermissions) {
                    isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(List<Permission> deniedPermissions, List<Permission> foreverDeniedPermissions) {
                    isPermissionGranted = false;
                    Toast.makeText(context,"Sorry, the app would not work if permissions are not granted",Toast.LENGTH_LONG).show();
            }
        });

        actionA = (FloatingActionButton) listView.findViewById(R.id.action_1);
        actionA.setTitle("Add Meeting");
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((PermissionUtils.isGranted(context,Permission.FINE_LOCATION)) && ((PermissionUtils.isGranted(context,Permission.COARSE_LOCATION)))) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(context,"Please grant permission and try again",Toast.LENGTH_SHORT).show();
                }
            }
        });

        actionB = (FloatingActionButton) listView.findViewById(R.id.action_b);
        actionB.setIcon(R.drawable.nearby);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"Put Geofence View activity here",Toast.LENGTH_SHORT).show();
                if ((PermissionUtils.isGranted(context,Permission.FINE_LOCATION)) && ((PermissionUtils.isGranted(context,Permission.COARSE_LOCATION)))) {
                    Intent intent = new Intent(context, MapsActivity2.class);
                    startActivity(intent);
                }
                else {
                    PermissionUtils.openApplicationSettings(context);
                    Toast.makeText(context,"Please grant permission and try again",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return listView;
    }
}
