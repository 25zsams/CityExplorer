package com.a25zsa.firebasetest;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class CreateMapMarker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean okToPlaceMarker;
    boolean okToViewMarker;
    Button placeMarker;
    Button viewMarker;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map_marker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeMarker = (Button)findViewById(R.id.placeMarker);
        viewMarker = (Button)findViewById(R.id.viewMarker);
        placeMarker.setBackgroundColor(Color.WHITE);
        viewMarker.setBackgroundColor(Color.WHITE);
        okToPlaceMarker = false;
        okToViewMarker = false;

        placeMarker.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                togglePlaceMarkerButton();
            }
        });

        viewMarker.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //okToPlaceMarker = okToPlaceMarker ? false: true;
                toggleViewMarkerButton();
            }
        });


        System.out.println("testing");

    }

    public void toggleViewMarkerButton(){
        if(okToViewMarker){
            okToViewMarker = false;
            viewMarker.setBackgroundColor(Color.WHITE);
        }
        else{
            okToViewMarker = true;
            viewMarker.setBackgroundColor(Color.GREEN);
        }
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

    public void addMarker(LatLng point){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude));
        mMap.addMarker(marker);
        System.out.println(marker);
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

        LatLng sydney = new LatLng(37, -122);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(okToPlaceMarker){
                    addMarker(latLng);
                    togglePlaceMarkerButton();
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    public void zoomCurrentLocation(){
        LocationManager locationManager = (LocationManager)
                getSystemService(this.LOCATION_SERVICE);
    }


}
