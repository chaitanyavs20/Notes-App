package com.example.chaitanya.notesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chaitanya.notesapp.Adapters.NoteListAdapter;
import com.example.chaitanya.notesapp.Database.RoomDB;
import com.example.chaitanya.notesapp.models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerview;
    NoteListAdapter noteListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab;
    SearchView searchView;
    Notes selectednote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.recycler_home);
        fab = findViewById(R.id.fab_add);
        searchView = findViewById(R.id.searchView_home);

        database = RoomDB.getInstance(this);
        notes = database.notesDAO().getAll();

        updateRecyclerView(notes);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
                startActivityForResult(intent,101);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Notes> filteredNotes = new ArrayList<>();
        for (Notes singleNote: notes){
            if(singleNote.getTitle().toLowerCase().contains(newText) || singleNote.getNotes().toLowerCase().contains(newText)){
                filteredNotes.add(singleNote);
            }
        }
        noteListAdapter.filterList(filteredNotes);
    }

    private void updateRecyclerView(List<Notes> notes) {
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this,notes,notesClickListener);
        recyclerview.setAdapter(noteListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
            intent.putExtra("old_name",notes);
            startActivityForResult(intent,102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
           selectednote= new Notes();
           selectednote = notes;
           showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101){
            if(resultCode== Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.notesDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.notesDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==102){
            if(resultCode==Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.notesDAO().update(new_notes.getId(),new_notes.getTitle(),new_notes.getNotes());
                notes.clear();
                notes.addAll(database.notesDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.pin:
                if(selectednote.isPinned){
                    database.notesDAO().pin(selectednote.getId(),false);
                    Toast.makeText(MainActivity.this,"Unpinned",Toast.LENGTH_SHORT);
                }
                else {
                    database.notesDAO().pin(selectednote.getId(),true);
                    Toast.makeText(MainActivity.this,"Pinned",Toast.LENGTH_SHORT);
                }
                notes.clear();
                notes.addAll(database.notesDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.notesDAO().delete(selectednote);
                notes.remove(selectednote);
                noteListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Note Deleted",Toast.LENGTH_SHORT);
                return true;
            default:
                return false;
        }

    }
}





