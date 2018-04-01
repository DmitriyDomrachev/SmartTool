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
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class ListFragment extends Fragment {
    ArrayList<State> stateArrayList = MainActivity.getStateArr();
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

//        StateHelper sh = MainActivity.sh;

        for (int i = 0; i<MainActivity.getCountState(); i++) {
            stateArrayList.add(new State (stateArrayList.get(i).getId(),stateArrayList.get(i).getName(), stateArrayList.get(i).isWiFiState(), stateArrayList.get(i).isBluetoothState(), stateArrayList.get(i).isMobileState(), 1, 1));
        }


        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator()); //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new RVAdapter(stateArrayList, view.getContext())); //устанавливаем наш адаптер
        return view;
    }
}