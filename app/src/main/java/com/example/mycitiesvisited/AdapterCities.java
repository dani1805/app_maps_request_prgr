package com.example.mycitiesvisited;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdapterCities extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Cities> cities;
    private final CitiesRepository repository;
    // Añadimos nuestra interfaz de click
    private CustomItemClick listener;


    public AdapterCities(Context context, CustomItemClick listener) {
        this.context = context;
        this.cities = new ArrayList<>();
        this.listener = listener;
        // La añadimos al constructor
        repository = new CitiesRepository(context);
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        // Diseño de cada celda de nuestra lista
        private TextView tvName;
        private TextView tvCountry;
        private TextView tvWeather;
        private TextView tvLong;
        Button btnDelete;

        public MyHolder(@NonNull View itemView) {
            // Diseño de cada celda
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvLong = itemView.findViewById(R.id.tvLong);
        }

        public void setData(String name, String country, String weather, double distance) {
            // Pasarle los datos
            tvName.setText(name);
            tvCountry.setText(country);
            tvWeather.setText(weather);
            tvLong.setText("Distancia: " + distance + " km");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflar la actividad
        View v = LayoutInflater.from(context).inflate(R.layout.holdercity, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Mostramos nuestros datos
        MyHolder myHolder = (MyHolder) holder;

        // Método para borrar las ciudades de una en una, para ello generamos el getter de nuestra primaryKey, creamos una variable donde le pasamos la posicion y el getId(de nuestra primaryKey)
        myHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = cities.get(position).getId();
                repository.delete(id);
                notifyDataSetChanged();

            }
        });
        Cities city = cities.get(position);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClickLong(position);
                return false;
            }
        });

        myHolder.setData(city.getName(),city.getCountry(), city.getWeather(), city.getDistance());
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    // Metodo para simplificar y no estar poniendo el adapter para actualizar datos
    public void updateCities(List<Cities>list) {
        cities = list;
        notifyDataSetChanged();

    }
}
