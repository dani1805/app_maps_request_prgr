package com.example.mycitiesvisited;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CitiesRepository {

    private final DaoCities daoCities;
    private final AppDataBase dataBase;

    public CitiesRepository(DaoCities daoCities, AppDataBase dataBase) {
        this.daoCities = daoCities;
        this.dataBase = dataBase;
    }

    public CitiesRepository(Context context) {
        dataBase = AppDataBase.getDatabase(context);
        daoCities = dataBase.daoCities();
    }

    public void insert (Cities city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoCities.insertCity(city);
            }
        }).start();
    }

    public LiveData<List<Cities>> load() {
        return daoCities.loadCities();
    }

    public LiveData<List<Cities>>loadCitiesVisited() {
        return daoCities.loadCitiesVisited();
    }

    public void deleteAllCities () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoCities.deleteAllCities();
            }
        }).start();
    }

    public void delete (int city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoCities.deleteById(city);
            }
        }).start();
    }

    public LiveData<List<Cities>> searchByName (String name) {
        return daoCities.searchByName(name);

    }

    public void update (String name, String country, String weather, int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoCities.updateCity(name, country, weather, id);
            }
        }).start();
    }

    /*public void updateCity (Cities city) {
        Log.i("here", city.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoCities.updateCity(city);
            }
        }).start();
    }*/

    // Es una clase que nos permite desarrollar todos los métodos para simplificar un poco el código. La clave de los listados dinámicos a través de las bases de datos es la creación de una appDatabase, Una interfaz de Dao, y una o dos clases según nos convenga


}
