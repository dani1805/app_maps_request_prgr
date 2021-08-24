package com.example.mycitiesvisited;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Collections;
import java.util.List;

public class CitiesVisitedActivity extends AppCompatActivity {

    CitiesRepository repository;
    private AdapterCities adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities_visited);

        RecyclerView rvCitiesVisited = findViewById(R.id.rvCitiesVisited);

        repository = new CitiesRepository(getApplicationContext());

        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvCitiesVisited.setLayoutManager(llm);

        adapter = new AdapterCities(this, new CustomItemClick() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemClickLong(int position) {

            }
        });
        rvCitiesVisited.setAdapter(adapter);

        repository.loadCitiesVisited().observe(this, new Observer<List<Cities>>() {
            @Override
            public void onChanged(List<Cities> cities) {
                //mCities = new ArrayList<>(cities);
                Collections.sort(cities);
                adapter.updateCities(cities);
            }
        });

    }
}