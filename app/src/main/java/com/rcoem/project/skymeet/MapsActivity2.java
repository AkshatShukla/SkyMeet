package com.rcoem.project.skymeet;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // For zooming automatically to the location of the marker
        final GPSTracker mGPS = new GPSTracker(this);
        LatLng sydney = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(19).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Room");
        GeoFire geoFire = new GeoFire(ref);
        Toast.makeText(MapsActivity2.this, "If nothing show up it is probably loading or there is no nearby room", Toast.LENGTH_LONG).show();
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude), 50);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation geoLocation) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(geoLocation.latitude, geoLocation.longitude)).title("Meeting").draggable(true);
                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                mMap.addMarker(marker);
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(geoLocation.latitude, geoLocation.longitude))
                        .radius(100)
                        .strokeColor(Color.TRANSPARENT)
                        .fillColor(0x553F51B5));
                Toast.makeText(MapsActivity2.this, s, Toast.LENGTH_LONG).show();
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    //    mMap.setMyLocationEnabled(true);
      //  mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }
}
