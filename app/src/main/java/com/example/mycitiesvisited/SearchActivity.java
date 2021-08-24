package com.example.mycitiesvisited;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    CitiesRepository repository;
    private AdapterCities adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText etSearch = findViewById(R.id.etSearch);
        RecyclerView rvSearchingCities = findViewById(R.id.rvSearchingCities);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository = new CitiesRepository(getApplicationContext());

                LinearLayoutManager llm = new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false);
                rvSearchingCities.setLayoutManager(llm);

                adapter = new AdapterCities(SearchActivity.this, new CustomItemClick() {
                    @Override
                    public void onItemClick(int position) {
                    }

                    @Override
                    public void onItemClickLong(int position) {

                    }

                });
                rvSearchingCities.setAdapter(adapter);

                String search = etSearch.getText().toString();

                // Método para el buscador de la actividad nueva

                repository.searchByName(search).observe(SearchActivity.this, new Observer<List<Cities>>() {
                    @Override
                    public void onChanged(List<Cities> cities) {
                        //mCities = new ArrayList<>(cities);
                        adapter.updateCities(cities);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }
}