package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dima.smarttool.MainActivity;
import com.example.dima.smarttool.Note;
import com.example.dima.smarttool.R;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class NoteFragment extends Fragment {
    ArrayList<Note> noteArrayList = new ArrayList<>(MainActivity.getNoteArr());
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        rv = view.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator()); //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new NoteRVAdapter(noteArrayList, view.getContext())); //устанавливаем наш адаптер
        return view;
    }
}


