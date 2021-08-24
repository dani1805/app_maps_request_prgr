package com.example.mycitiesvisited;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdateCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_city);

        EditText etSetName = findViewById(R.id.etSetName);
        EditText etSetCountry = findViewById(R.id.etSetCountry);
        EditText etSetWeather = findViewById(R.id.etSetWeather);
        Button btnSetNewCity = findViewById(R.id.btnSetNewCity);

        // Recuperamos los datos
        Bundle bundle = getIntent().getExtras();
        Cities city = bundle.getParcelable("city");

        // Pasarle la id en vez de la ciudad

        // Boton que nos permite guardar la modificacion de la ciudad, clima, o pais
        btnSetNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cities updateCity = new Cities (etSetName.getText().toString(), etSetCountry.getText().toString(), etSetWeather.getText().toString(), city.isVisited());
                
                city.setName(etSetName.getText().toString());
                city.setCountry(etSetCountry.getText().toString());
                city.setWeather(etSetWeather.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("city", city);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}