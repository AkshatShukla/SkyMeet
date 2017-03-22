package com.rcoem.project.skymeet;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.slideup.SlideUp;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String VALUES = "values";
    private GoogleMap mMap;
    private Button search;
    private double latg;
    private double lang;

    private Geocoder geocoder;
    List<Address> addresses;

    private FloatingActionButton fab;
    private SlideUp slideUp;
    private View dim;
    private View slideView;

    private EditText roomName;
    private TextView currentLoc;
    private EditText rangeValue;

    /*
    public void onSearch() throws IOException {
        EditText location_tf = (EditText) findViewById(R.id.editText1);
        List<Address> addressList = null;
        String location = location_tf.getText().toString();
        if (!TextUtils.isEmpty(location)) {
            Geocoder geocoder = new Geocoder(this);
            addressList = geocoder.getFromLocationName(location, 1);
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney").draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        slideView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        roomName = (EditText) findViewById(R.id.roomName);
        currentLoc = (TextView) findViewById(R.id.latLong);
        rangeValue = (EditText) findViewById(R.id.rangeText);

        slideUp = new SlideUp.Builder(slideView)
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {
                        dim.setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE){
                            fab.show();
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withSavedState(savedInstanceState)
                .withStartState(SlideUp.State.HIDDEN)
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.show();
                fab.hide();
            }
        });

        /*
        search = (Button) findViewById(R.id.button2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onSearch();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        */


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
//Setup multiple marker


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        final GPSTracker mGPS = new GPSTracker(this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Room");
        GeoFire geoFire = new GeoFire(ref);
        Toast.makeText(MapsActivity.this, "If nothing show up it is probably loading or there is no nearby room", Toast.LENGTH_LONG).show();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mGPS.latitude, mGPS.longitude), 0.1);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //runnersNearby.add(key);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).title("Meeting").draggable(true);
                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));

                String range = rangeValue.getText().toString();

                if (range.length() > 0) {
                    int finalRange = Integer.parseInt(range);

                    mMap.addMarker(marker);
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(location.latitude, location.longitude))
                            .radius(finalRange)
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(0x553F51B5));
                }
                //Toast.makeText(MapsActivity.this, key, Toast.LENGTH_LONG).show();
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

            LatLng sydney = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

        try {
            geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
            addresses = geocoder.getFromLocation(latg, lang, 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {

                Address returnAddress = addresses.get(0);

                String knownName = addresses.get(0).getFeatureName();
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();

                str.append(knownName + " " + localityString + " ");
                str.append(city + "");

                currentLoc.setText(str);
            }
        }
        catch (Exception e) {
        }

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
        //mMap.setMyLocationEnabled(true);

        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(19).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                lang = point.longitude;
                latg = point.latitude;

                String langitute = Double.toString(lang);
                String longitute = Double.toString(latg);

                Toast.makeText(MapsActivity.this,langitute+longitute , Toast.LENGTH_SHORT).show();

                try {
                    geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
                    addresses = geocoder.getFromLocation(latg, lang, 1);
                    StringBuilder str = new StringBuilder();
                    if (geocoder.isPresent()) {

                        Address returnAddress = addresses.get(0);

                        String knownName = addresses.get(0).getFeatureName();
                        String localityString = returnAddress.getLocality();
                        String city = returnAddress.getCountryName();


                        str.append(knownName + " " + localityString + " ");
                        str.append(city + "");


                        currentLoc.setText(str);
                    }
                }
                catch (Exception e) {
                }

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));

                String range = rangeValue.getText().toString();

                mMap.addMarker(marker);

                if (range.length() > 0) {
                    int finalRange = Integer.parseInt(range);

                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(point.latitude, point.longitude))
                            .radius(finalRange)
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(0x553F51B5));
                    mMap.addMarker(marker);
                }

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String langitute = Double.toString(lang);
                String longitute = Double.toString(latg);
                Toast.makeText(MapsActivity.this,langitute+longitute , Toast.LENGTH_LONG).show();
                String val = langitute;
                String val2 = longitute;
                //Intent intent =new Intent(MapsActivity.this,AddRoomActivity.class);
                //intent.putExtra("value",val);
                //intent.putExtra("value2",val2);
                //startActivity(intent);
                return false;
            }
        });
    }


}
