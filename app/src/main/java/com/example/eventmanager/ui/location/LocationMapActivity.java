package com.example.eventmanager.ui.location;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LocationMapActivity extends AppCompatActivity { // implements OnMapReadyCallback {
    // private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_location_map); // TODO: Fix layout

        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        locationName = getIntent().getStringExtra("locationName");

        // TextView locationTextView = findViewById(R.id.locationTextView);
        // TextView coordinatesTextView = findViewById(R.id.coordinatesTextView);

        // locationTextView.setText("Location: " + locationName);
        // coordinatesTextView.setText("Coordinates: " + latitude + ", " + longitude);

        // Temporary: Show toast with location info
        Toast.makeText(this, "Location: " + locationName + " (" + latitude + ", " + longitude + ")", Toast.LENGTH_LONG).show();
    }

    // @Override
    // public void onMapReady(GoogleMap googleMap) {
    //     mMap = googleMap;

    //     LatLng location = new LatLng(latitude, longitude);
    //     mMap.addMarker(new MarkerOptions().position(location).title(locationName));
    //     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    // }
}
