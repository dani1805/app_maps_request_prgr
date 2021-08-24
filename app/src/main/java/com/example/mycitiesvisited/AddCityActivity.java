package com.example.mycitiesvisited;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCityActivity extends AppCompatActivity implements OnMapReadyCallback {

    double latitude;
    double longitude;
    // Declarar las variables y hacerlas globales para utilizarlas en la recogida de los datos
    boolean haveMarker = false; // Variable de tipo booleano para detectar si hay un click en el mapa

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 11;
    
    ImageView myPhoto;
    String myImageString;
    private Uri myImageUri;

    private static final String FOLDER_NAME = "Images";
    private static final String APP_TAG = "Get File App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cities);

        EditText etName = findViewById(R.id.etName);
        EditText etCountry = findViewById(R.id.etCountry);
        EditText etWeather = findViewById(R.id.etWeather);
        //EditText etLatitude = findViewById(R.id.etLatitude);
        //EditText etLongitude = findViewById(R.id.etLongitude);
        CheckBox chVisited = findViewById(R.id.chVisited);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnPhoto = findViewById(R.id.btnPhoto);
        myPhoto = findViewById(R.id.myPhoto);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if (haveMarker) {
                     Log.i("imagen", myImageString);
                     // Si hay una chincheta marcada, guarda los datos en la base de datos
                     Cities city = new Cities(etName.getText().toString(), etCountry.getText().toString(), etWeather.getText().toString(),chVisited.isChecked(),latitude,longitude, myImageString);

                     Intent intent = new Intent();
                     intent.putExtra("city", city);
                     setResult(RESULT_OK, intent);
                     finish();
                 } else {

                     // Si intenta guardar una ciudad y no clickea sobre el mapa, el toast muestra un imperativo para que seleccione alguna ubicacion
                     Toast.makeText(AddCityActivity.this, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show();
                 }

                // Podemos utilizar directamente el dao pero con el activity for result se emplea para pasar datos también hacia atrás
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddCityActivity.this);
                builder.setMessage("¿Desde donde desea seleccionar la imagen?");
                builder.setPositiveButton("Imagen desde la galería", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent contentSelector = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        contentSelector.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelector.setType("image/*");
                        startActivityForResult(contentSelector, REQUEST_GALLERY);
                    }
                });
                builder.setNegativeButton("Imágen desde la cámara", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // crea el archivo donde se almacenará la imagen de la cámara
                        String nombreArchivo = createFileName();
                        File photoFile = getPhotoFileUri(nombreArchivo);

                        // Si la versión de la App es superior a la 29 (Android X)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // clase que permite agrupar un conjunto de valores para que puedan ser procesados
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo); //nombre del archivo
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png"); // formato
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/" + FOLDER_NAME); // ruta donde se almacenará la imagen

                            // se inserta la información en el almacenamiento externo. getContentResolver devuelve la uri con la ruta tipo "content" a la imagen
                            myImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            // para el almacenamiento interno, sería  mImageUri = getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
                        } else {
                            // Si la versión de la App es superior a la 24 (Nougat)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                // insertar el archivo en el provider (proveedor de contenido)
                                myImageUri = FileProvider.getUriForFile(AddCityActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                            } else {
                                myImageUri = Uri.fromFile(photoFile);
                            }
                        }
                        // se pasa el uri con la info donde se almacenará la imagen, al intent,
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, myImageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    public File getPhotoFileUri(String fileName) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + FOLDER_NAME;
        File mediaStorageDir = new File(path); //crea un archivo en el directorio de la ruta anterior

        // crea el directorio si no existe
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        // Devuelve el archivo donde se almacenará la imagen
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    private String createFileName() {
        String format = "yyyyMMdd_HHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        Date now = new Date();
        String timeStamp = sdf.format(now);
        return "IMG_" + timeStamp + ".png";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getDataString() != null) {
                    myImageString = data.getDataString();
                    Uri uri = Uri.parse(myImageString);

                    myPhoto.setImageURI(uri);
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                myImageString = myImageUri.toString();

                myPhoto.setImageURI(myImageUri);
            // ya teniamos la uri donde se iba a almacenar la imagen de la cámara
                //imageView.setImageURI(mImageUri);
            }
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setTrafficEnabled(true);
        // Diseño del mapa

        // Metodo que ofrece la longitud y la latitud y cuando clickas el mapa introduce un marcador en la posicion del click
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Marker marker;

            @Override
            public void onMapClick(LatLng latLng) {
                if (!haveMarker) {

                    // Si no hay una chincheta, se crea por primera vez
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                    marker.setDraggable(true);
                    haveMarker = true;
                } else {

                    // Si la hay, pues se hace dragable para poder arrastrarla
                    marker.setPosition(latLng);
                }
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }

        });

    }
}