package com.a25zsa.firebasetest;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    Button viewMarker;
    Button logoutButton;
    DatabaseReference firebase;
    LatLng currentLocation;
    private FusedLocationProviderClient mfusedLocationProviderClient;

    BroadcastReceiver b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map_marker);
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeMarker = (Button) findViewById(R.id.placeMarker);
        viewMarker = (Button) findViewById(R.id.viewMarker);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        placeMarker.setBackgroundColor(Color.WHITE);
        viewMarker.setBackgroundColor(Color.WHITE);
        logoutButton.setBackgroundColor(Color.WHITE);
        okToPlaceMarker = false;
        okToViewMarker = false;
        userName = getIntent().getExtras().getString("userName");
        firebase = FirebaseDatabase.getInstance().getReference("Marker");

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

        viewMarker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                toggleViewMarkerButton();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logout();
            }
        });



        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        b = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                Intent signInPage = new Intent(CreateMapMarker.this, LogIn.class);
                startActivity(signInPage);
                unregisterReceiver(b);
                finish();
            }
        };
        registerReceiver(b, intentFilter);


    }

    private void logout()
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
    }

    private void toggleViewMarkerButton() {
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
    }

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

        LatLng sydney = new LatLng(37, -122);
        addMarker(37.0, -122.0);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Random"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20f));
        //mMap.currentLocation();
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