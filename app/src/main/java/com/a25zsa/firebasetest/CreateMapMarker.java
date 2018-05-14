package com.a25zsa.firebasetest;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CreateMapMarker extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private String userName;
    boolean okToPlaceMarker;
    boolean okToViewMarker;
    private boolean mLocationPermissionGranted = false;
    Button placeMarker;
    //Button viewMarker;
    Button searchButton;
    DatabaseReference firebase;
    LatLng currentLocation;
    private FusedLocationProviderClient mfusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map_marker);
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton = (Button) findViewById(R.id.search_button);
        placeMarker = (Button) findViewById(R.id.placeMarker);
        //viewMarker = (Button) findViewById(R.id.viewMarker);
        placeMarker.setBackgroundColor(Color.WHITE);
        //viewMarker.setBackgroundColor(Color.WHITE);

        okToPlaceMarker = false;
        okToViewMarker = false;
        userName = getIntent().getExtras().getString("userName");
        firebase = FirebaseDatabase.getInstance().getReference("Marker");

        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                onSearch(this);

            }
        });



        placeMarker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                if (userName.equals("")) {
                    Toast.makeText(getBaseContext(), "Must sign in to place marker", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getBaseContext(), "Tap to place a marker.", Toast.LENGTH_LONG).show();
                togglePlaceMarkerButton();
            }
        });

        /*viewMarker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                toggleViewMarkerButton();
            }
        });*/


    }
    private void onSearch(View.OnClickListener view){
        EditText location_tosearch  = (EditText)findViewById(R.id.Address_text);
        String location = location_tosearch.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("") ) {
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location, 1);

                Address address = addressList.get(0);
                LatLng latLong = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLong).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLong));

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchButton.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    /*private void toggleViewMarkerButton() {
        if (okToViewMarker) {
            okToViewMarker = false;
            viewMarker.setBackgroundColor(Color.WHITE);
            mMap.clear();
        } else {
            okToViewMarker = true;
            viewMarker.setBackgroundColor(Color.GREEN);
            viewMarkers();
            //mMap.clear();
        }
    }*/

    private void viewMarkers() {
        final DatabaseReference directory = firebase;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Double lat = Double.parseDouble(postSnapShot.child("lat").getValue().toString());
                    Double lng = Double.parseDouble(postSnapShot.child("lng").getValue().toString());
                    addMarker(lat, lng);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        directory.addListenerForSingleValueEvent(eventListener);
    }

    private void togglePlaceMarkerButton() {
        if (okToPlaceMarker) {
            okToPlaceMarker = false;
            placeMarker.setBackgroundColor(Color.WHITE);
        } else {
            okToPlaceMarker = true;
            placeMarker.setBackgroundColor(Color.GREEN);
        }
    }

    private void addMarker(Double lat, Double lng) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
        mMap.addMarker(marker);
    }

    private void addMarker(LatLng point) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude));
        mMap.addMarker(marker);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: requesting location permission");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device location");

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                final Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {


                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(CreateMapMarker.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "Security Exception " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        viewMarkers();
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (okToPlaceMarker) {
                    addMarker(latLng);
                    togglePlaceMarkerButton();
                    pushMarkerToDatabase(latLng);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = new Bundle();
                bundle.putString("latLng", pointToFirebaseFormat(marker.getPosition().latitude, marker.getPosition().longitude));
                bundle.putString("userName", userName);
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "");

                return false;
            }
        });
        //viewMarkers();
    }


    public String pointToFirebaseFormat(Double lat, Double lng) {
        String hashLocation = lat + " " + lng;
        return hashLocation.replace(".", ",");
    }

    public void pushMarkerToDatabase(LatLng point) {
        final Double lat = point.latitude;
        final Double lng = point.longitude;
        final String coordinate = pointToFirebaseFormat(lat, lng);
        final DatabaseReference directory = firebase.child(coordinate);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MarkerInformation newMarker = new MarkerInformation(lat, lng, coordinate);
                directory.setValue(newMarker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        directory.addListenerForSingleValueEvent(eventListener);
    }

}