package com.rcoem.project.skymeet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationBasedOnActivityProvider;

/**
 * Created by dhananjay on 19-03-2017.
 */

public class GeoFenceManager extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Toast.makeText(GeoFenceManager.this, "Service Started", Toast.LENGTH_LONG).show();
        final GPSTracker mGPS = new GPSTracker(this);
        SmartLocation.with(this).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                Toast.makeText(GeoFenceManager.this,"Started Entry",Toast.LENGTH_LONG).show();

            }
        });
        SmartLocation.with(this).location().state().locationServicesEnabled();
        SmartLocation.with(this).location().state().isAnyProviderAvailable();

// Check if GPS is available
        SmartLocation.with(this).location().state().isGpsAvailable();

// Check if Network is available
        SmartLocation.with(this).location().state().isNetworkAvailable();

// Check if the passive provider is available
        SmartLocation.with(this).location().state().isPassiveAvailable();

// Check if the location is mocked


        SmartLocation.with(this).activity().start(new OnActivityUpdatedListener() {
            @Override
            public void onActivityUpdated(DetectedActivity detectedActivity) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Room");
                GeoFire geoFire = new GeoFire(databaseReference);
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude), 100);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(final String s, GeoLocation geoLocation) {



                        SmartLocation.with(GeoFenceManager.this).geofencing()



                                .start(new OnGeofencingTransitionListener() {
                                    @Override
                                    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {
                                        NotificationManager notif=(NotificationManager)getSystemService(GeoFenceManager.NOTIFICATION_SERVICE);
                                        Notification notify=new Notification.Builder
                                                (getApplicationContext()).setContentTitle("DJ").setContentText("There is nearby meeting available").
                                                setContentTitle("Meeting Nearby").setSmallIcon(R.mipmap.ic_launcher).build();

                                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                                        notify.defaults |= Notification.DEFAULT_VIBRATE;
                                        notify.defaults |= Notification.DEFAULT_SOUND;

                                        notif.notify(0, notify);
                                        SmartLocation.with(GeoFenceManager.this).geofencing().remove(s);
                                    }
                                });
                      //  Toast.makeText(GeoFenceManager.this,s,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onKeyExited(String s) {
                        SmartLocation.with(GeoFenceManager.this).geofencing().remove(s);

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

            }
        });
        return START_STICKY;

    }
}
