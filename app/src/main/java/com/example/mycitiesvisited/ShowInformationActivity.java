package com.example.mycitiesvisited;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

public class ShowInformationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Cities city; //Ponerlo global conlleva poder pasar la longitud y la latitud
    private static final int REQUEST_PERMISSION = 1;
    TextView tvDistance;
    ImageView imgReturnPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        TextView tvCityMap = findViewById(R.id.tvCityMap);
        TextView tvCountryMap = findViewById(R.id.tvCountryMap);
        TextView tvWeatherMap = findViewById(R.id.tvWeatherMap);
        tvDistance = findViewById(R.id.tvDistance);
        imgReturnPhoto = findViewById(R.id.imgReturnPhoto);


        // Recoges los datos
        Bundle bundle = getIntent().getExtras();
        city = bundle.getParcelable("city");

        tvCityMap.setText(city.getName());
        tvCountryMap.setText(city.getCountry());
        tvWeatherMap.setText(city.getWeather());

        Log.i("imagen", city.getPhoto());

        Uri uri = Uri.parse(city.getPhoto());
        imgReturnPhoto.setImageURI(uri);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.otherMap);
        fragment.getMapAsync(this); // Mostrar el fragment del mapa

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            checkLocation();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setTrafficEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);


        googleMap.addMarker(new MarkerOptions().position(new LatLng(city.getLatitude(),city.getLongitude()))); // añadir el marker pasandole la longitud y la latitud
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(city.getLatitude(),city.getLongitude()))); // Centrar la camara en la ubicacion escogida

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @SuppressLint("MissingPermission")

    public void checkLocation() {

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

        locationClient.getLastLocation().addOnSuccessListener(this, new
                OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            Location cityLocation = new Location("cityLocation");
                            cityLocation.setLatitude(city.getLatitude());
                            cityLocation.setLongitude(city.getLongitude());

                            double distance = location.distanceTo(cityLocation) / 1000;
                            distance = Math.floor(distance * 100) / 100;
                            tvDistance.setText("Ubicacion: " + distance + " km");

                        }
                    }
                });
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocation();

            } else {
                Toast.makeText(ShowInformationActivity.this, "No has aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}