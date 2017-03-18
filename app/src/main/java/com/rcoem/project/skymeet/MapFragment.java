package com.rcoem.project.skymeet;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    Activity context = getActivity();

    private double latg;
    private double lang;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.actvitity_map_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());


        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;

                final GPSTracker mGPS = new GPSTracker(context);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Room");
                GeoFire geoFire = new GeoFire(ref);
                //Toast.makeText(context, "If nothing show up it is probably loading or there is no nearby room", Toast.LENGTH_LONG).show();
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude), 0.1);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        //runnersNearby.add(key);
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title("Meeting").draggable(true);
                        //marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                        mMap.addMarker(marker);
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(location.latitude, location.longitude))
                                .radius(100)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(0x553F51B5));
                        Toast.makeText(context, key, Toast.LENGTH_LONG).show();
                        //
                    }

                    @Override
                    public void onKeyExited(String key) {
                        //runnersNearby.remove(key);

                        System.out.println(String.format("Key %s is no longer in the search area", key));
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                    }

                    @Override
                    public void onGeoQueryReady() {
                        System.out.println("All initial data has been loaded and events have been fired!");

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {
                        System.err.println("There was an error with this query: " + error);
                    }
                });

                // Add a marker in Sydney and move the camera

                latg = mGPS.getLatitude();
                lang = mGPS.getLongitude();

                LatLng sydney = new LatLng(latg, lang);

                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.getUiSettings().setZoomControlsEnabled(true);
                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
