package com.example.chaitanya.notesapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.chaitanya.notesapp.models.Notes;

@Database(entities = Notes.class,version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase{
    private static RoomDB database;
    private static String db_name = "NotesApp";

    public synchronized static RoomDB getInstance(Context context){
        if(database==null){
            database = Room.databaseBuilder(context,RoomDB.class,db_name)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract NotesDAO notesDAO();
}
