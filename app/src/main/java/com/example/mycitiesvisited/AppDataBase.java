package com.example.mycitiesvisited;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Cities.class, version = 1)

// Base de datos donde se sostiene nuestra app
public abstract class AppDataBase extends RoomDatabase {
    public static final String DB_NAME = "db_cities";

    public abstract DaoCities daoCities();

    private static AppDataBase database; //variable para crear una instancia de la base de datos y poder acceder a ella

    public static AppDataBase getDatabase(final Context context) {
        if (database == null) {
            synchronized (AppDataBase.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, DB_NAME)
                            .build();
                }
            }
        }
        return database;
    }

    static final ExecutorService dbWriterExecutor = Executors.newFixedThreadPool(1);
}
