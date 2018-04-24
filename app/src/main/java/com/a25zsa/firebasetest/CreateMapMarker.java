package com.a25zsa.firebasetest;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateMapMarker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String userName;
    boolean okToPlaceMarker;
    boolean okToViewMarker;
    Button placeMarker;
    Button viewMarker;
    DatabaseReference firebase;
    LatLng currentLocation;
    FusedLocationProviderClient client;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map_marker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeMarker = (Button)findViewById(R.id.placeMarker);
        viewMarker = (Button)findViewById(R.id.viewMarker);
        placeMarker.setBackgroundColor(Color.WHITE);
        viewMarker.setBackgroundColor(Color.WHITE);
        okToPlaceMarker = false;
        okToViewMarker = false;
        userName = getIntent().getExtras().getString("userName");
        firebase = FirebaseDatabase.getInstance().getReference("Marker");

        placeMarker.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                if(userName.equals("")){
                    Toast.makeText(getBaseContext(), "Must sign in to place marker", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getBaseContext(), "Tap to place a marker.", Toast.LENGTH_LONG).show();
                togglePlaceMarkerButton();
            }
        });

        viewMarker.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                toggleViewMarkerButton();
            }
        });



    }

    public void toggleViewMarkerButton() {
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

     public void viewMarkers(){
        final DatabaseReference directory = firebase;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
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

    public void togglePlaceMarkerButton(){
        if(okToPlaceMarker){
            okToPlaceMarker = false;
            placeMarker.setBackgroundColor(Color.WHITE);
        }
        else{
            okToPlaceMarker = true;
            placeMarker.setBackgroundColor(Color.GREEN);
        }
    }

    public void addMarker(Double lat, Double lng){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
        mMap.addMarker(marker);
    }

    public void addMarker(LatLng point){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude));
        mMap.addMarker(marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(37, -122);
        addMarker(37.0, -122.0);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Random"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20f));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(okToPlaceMarker){
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

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                return false;
//            }
//        });
        //zoomCurrentLocation();
        //CurrentLocation c = new CurrentLocation(this);
    }

    public String pointToFirebaseFormat(Double lat, Double lng){
        String hashLocation = lat + " " + lng;
        return hashLocation.replace(".", ",");
    }

    public void pushMarkerToDatabase(LatLng point){
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

    public void zoomCurrentLocation(){
        System.out.println(1);
        client = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(CreateMapMarker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED){
            currentLocation = new LatLng(0, 0);
        }
        System.out.println(2);

        client.getLastLocation().addOnSuccessListener(CreateMapMarker.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    System.out.println(3);
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        });
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
    }


}
