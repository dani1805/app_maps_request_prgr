package com.example.mycitiesvisited;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Dao
public interface DaoCities {

    //Crear una ciudad

    @Insert
    void insertCity (Cities city);

    // Obtener todas las ciudades

    @Query("SELECT * FROM cities")
    LiveData<List<Cities>> loadCities();

    // Obtener lista de ciudades visitadas

    @Query("SELECT * FROM cities WHERE isVisited = 1")
    LiveData<List<Cities>> loadCitiesVisited();

    // Eliminar una ciudad

    @Delete
    void deleteOneCity(Cities city);

    // Eliminar todas las ciudades

    @Query("DELETE FROM cities")
    void deleteAllCities();

    @Query("DELETE FROM cities WHERE id = :id")
    void deleteById(int id);

    // Buscador para la actividad

    @Query("SELECT * FROM cities WHERE name = :name")
    LiveData<List<Cities>> searchByName (String name);

    // Modificar ciudad, país o clima

    @Query("UPDATE cities SET name = :name, country = :country, weather = :weather WHERE id = :id")
    void updateCity(String name, String country, String weather, int id);

    //@Update void updateCity(Cities cities);
}
