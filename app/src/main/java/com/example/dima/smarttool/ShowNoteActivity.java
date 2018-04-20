package com.example.dima.smarttool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowNoteActivity extends AppCompatActivity {
    Intent intent;
    TextView name, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        intent = getIntent();
        name = findViewById(R.id.showNoteNameText);
        note = findViewById(R.id.showNoteText);
        name.setText(intent.getStringExtra("name"));
        note.setText(intent.getStringExtra("note"));
            }
}
