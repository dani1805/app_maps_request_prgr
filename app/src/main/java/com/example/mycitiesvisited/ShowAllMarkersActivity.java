package com.example.mycitiesvisited;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ShowAllMarkersActivity extends AppCompatActivity implements OnMapReadyCallback {
    List<Cities> mCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_markers);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("cities");
        mCities = (List<Cities>) args.getSerializable("data");

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.secondMap);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setTrafficEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        for (int i = 0; i < mCities.size(); i++) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(mCities.get(i).getLatitude(),mCities.get(i).getLongitude())).title(mCities.get(i).name).snippet(mCities.get(i).country));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}