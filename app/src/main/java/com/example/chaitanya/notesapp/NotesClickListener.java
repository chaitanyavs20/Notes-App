package com.example.chaitanya.notesapp;

import androidx.cardview.widget.CardView;

import com.example.chaitanya.notesapp.models.Notes;

public interface NotesClickListener {

    void onClick(Notes notes);

    void onLongClickListener(Notes notes, CardView cardView);
}
