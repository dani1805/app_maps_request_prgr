package com.example.mycitiesvisited;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.math.BigDecimal;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CitiesRepository repository;
    List<Cities> mCities;
    private AdapterCities adapter;

    private static final int REQUEST_PERMISSION = 2;
    Location myLocation; // Declarar un objeto de tipo Location
    SharedPreferences preferences; // Las sharedPreferences se declaran para guardar mi ubicacion. Las he empleado porque se carga antes los registros de la base de datos que los permisos.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvCities = findViewById(R.id.rvCities);
        FloatingActionButton addCities = findViewById(R.id.addCities);
        FloatingActionButton deleteAll = findViewById(R.id.deleteAll);
        FloatingActionButton accessCitiesVisited = findViewById(R.id.accessCitiesVisited);
        FloatingActionButton search = findViewById(R.id.search);
        FloatingActionButton showMap = findViewById(R.id.showMap);

        preferences = this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE); // Cargar las sharedPreferences

        repository = new CitiesRepository(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

            locationClient.getLastLocation().addOnSuccessListener(this, new
                    OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                setMyLocation(location); // Metodo para simplificar codigo
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }

        // Planificamos el diseño que mostraremos nuestro listado
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvCities.setLayoutManager(llm);

        // Para modificar una ciudad desde otra actividad nueva, implementamos una interfaz, cambiamos el numero del requestCode a 4 y hacemos un intent a la nueva actividad

        adapter = new AdapterCities(this, new CustomItemClick() {
            @Override
            public void onItemClick(int position) {
                Cities city = mCities.get(position);
                Intent intent = new Intent (MainActivity.this, UpdateCityActivity.class);
                intent.putExtra("city", city);
                startActivityForResult(intent, 4);
            }

            @Override
            public void onItemClickLong(int position) {
                Cities city = mCities.get(position);
                Intent intent = new Intent(MainActivity.this, ShowInformationActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);

            }

        });
        rvCities.setAdapter(adapter);

        // Método para cargar las ciudades cuando las añadimos

        repository.load().observe(this, new Observer<List<Cities>>() {
            @Override
            public void onChanged(List<Cities> cities) {
                //mCities = new ArrayList<>(cities);
                //Collections.sort(cities);

                String latitude = preferences.getString("latitude", "0"); // Recuperamos la preferencia de la latitud
                String longitude = preferences.getString("longitude", "0"); // Recuperamos la preferencia de la longitud

                myLocation = new Location("myLocation");
                myLocation.setLatitude(Double.parseDouble(latitude)); // Creamos mi ubicacion y la parseamos a double
                myLocation.setLatitude(Double.parseDouble(longitude));

                for (int i = 0; i < cities.size(); i++) {

                    Location cityLocation = new Location("cityLocation"); // Crear un objeto de tipo Location, que seria la ubicacion de cada ciudad
                    cityLocation.setLatitude(cities.get(i).getLatitude());
                    cityLocation.setLongitude(cities.get(i).getLongitude());

                    double distance = myLocation.distanceTo(cityLocation) / 1000;
                    distance = Math.floor(distance * 100) / 100; // Funcion para redondear a dos decimales
                    cities.get(i).setDistance(distance);
                }

                Collections.sort(cities,Cities.orderDistance); // Ordenando de menor a mayor segun la distancia en km


                adapter.updateCities(cities);
                mCities = cities;
            }
        });

        // Boton para acceder a la actividad para añadir ciudades
        addCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(intent, 5);

            }
        });

        // Método para borrar todas las ciudades mediante un button

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.deleteAllCities();
            }
        });

        // Boton que nos permite acceder a la actividad donde se encuentran las ciudades que hemos marcado la casilla de visitadas

        accessCitiesVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, CitiesVisitedActivity.class);
                startActivity(intent);
            }
        });

        // Boton que nos lleva a la actividad del buscador

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // Boton que nos lleva a la actividad donde se muestran los markers guardados

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowAllMarkersActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("data",(Serializable) mCities);
                intent.putExtra("cities", args);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);

                locationClient.getLastLocation().addOnSuccessListener(this, new
                        OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {
                                    setMyLocation(location);
                                }
                            }
                        });

            } else {
                Toast.makeText(MainActivity.this, "No has aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setMyLocation(Location location) {

        // Guardar mi ubicacion. Aplicacion de las sharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Cities city = data.getParcelableExtra("city");
                    repository.insert(city);
                }
            } else {
                Toast.makeText(this, "Error al insertar la ciudad", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Cities city = data.getParcelableExtra("city");
                    Log.i("here", city.getName());
                    repository.update(city.getName(), city.getCountry(), city.getWeather(), city.getId());
                    adapter.notifyDataSetChanged();
                }
            }
        }


    }
}