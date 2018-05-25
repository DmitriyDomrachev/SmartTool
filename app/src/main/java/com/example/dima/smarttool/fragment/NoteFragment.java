package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dima.smarttool.Note;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.activity.MainActivity;
import com.example.dima.smarttool.adapter.NoteRVAdapter;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class NoteFragment extends Fragment {
    public static final String FIRST_START_NOTES = "firstNotes";
    ArrayList<Note> noteArrayList = new ArrayList<>(MainActivity.getNoteArr());
    RecyclerView rv;
    TextView firstTextView;
    Boolean firstStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        firstTextView = view.findViewById(R.id.stateFragmentStartTextView);
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        firstStart = prefs.getBoolean(FIRST_START_NOTES, true);
        if (firstStart)
            firstTextView.setVisibility(View.VISIBLE);
        //текст подсказка при первом запуске


        rv = view.findViewById(R.id.rvNote);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator());          //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new NoteRVAdapter(noteArrayList, view.getContext())); //устанавливаем наш адаптер

        return view;
    }
}


