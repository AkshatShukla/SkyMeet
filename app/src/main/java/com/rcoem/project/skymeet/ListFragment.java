package com.rcoem.project.skymeet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.Geofence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.simplepermission.permissions.MultiplePermissionCallback;
import com.luseen.simplepermission.permissions.Permission;
import com.luseen.simplepermission.permissions.PermissionFragment;
import com.luseen.simplepermission.permissions.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;

/**
 * Created by Akshat Shukla on 15-03-2017.
 */

public class ListFragment extends PermissionFragment {

    View listView;
    Activity context;

    RecyclerView recyclerView;
    // private ListView list;

    private List<String> stringsList = new ArrayList<>();
    private ArrayList<BlogModel> blogList=new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;

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

        final GPSTracker mGPS = new GPSTracker(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.da_item,stringsList);
        // ListView list = (ListView) listView.findViewById(R.id.itemdjdolls);
        recyclerView = (RecyclerView) listView.findViewById(R.id.recylcerView1);


        getActivity().startService(new Intent(getActivity(),GeoFenceManager.class));
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Room");
        GeoFire geoFire1 = new GeoFire(databaseReference1);
        GeoQuery geoQuery1 = geoFire1.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude), 100);
        geoQuery1.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation geoLocation) {
                GeofenceModel mestalla = new GeofenceModel.Builder(s)
                        .setTransition(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setLatitude(geoLocation.latitude)
                        .setLongitude(geoLocation.longitude)
                        .setRadius(100)
                        .build();
                SmartLocation.with(context).geofencing().add(mestalla);
            }

            @Override
            public void onKeyExited(String s) {

            }

            @Override
            public void onKeyMoved(String s, GeoLocation geoLocation) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError databaseError) {

            }
        });

        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(blogList,context);
        recyclerView.setAdapter(recyclerViewAdapter);






        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        GeoFire geoFire = new GeoFire(databaseReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude),0.5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String s, GeoLocation geoLocation) {
                final BlogModel b1=new BlogModel();
                DatabaseReference db1=FirebaseDatabase.getInstance().getReference("Room").child(s);

                db1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        b1.setName(dataSnapshot.child("Name").getValue().toString());
                        b1.setMsg(s);
                        blogList.add(b1);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                stringsList.add(s);
                Toast.makeText(context,s,Toast.LENGTH_SHORT).show();

                //GeoFire geoFire = new GeoFire(ref);
                //  Toast.makeText(context,s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKeyExited(final String s) {
                stringsList.remove(s);
            }

            @Override
            public void onKeyMoved(String s, GeoLocation geoLocation) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError databaseError) {

            }
        });

        return listView;
    }
}
