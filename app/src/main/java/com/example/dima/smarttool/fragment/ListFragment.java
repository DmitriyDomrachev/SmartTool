package com.example.dima.smarttool.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;

/**
 * Created by dima on 27.02.2018.
 */

public class ListFragment extends Fragment {
    State state = new State();
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        StateHelper sh = MainActivity.sh;
//
//        state.addState(sh.getAll().get(1).getName(), sh.getAll().get(1).isWiFiState(), sh.getAll().get(1).isBluetoothState(), sh.getAll().get(1).isMobileState(), 1, 1);
//        state.addState(sh.getAll().get(2).getName(), sh.getAll().get(2).isWiFiState(), sh.getAll().get(2).isBluetoothState(), sh.getAll().get(2).isMobileState(), 2, 2);
//        state.addState(sh.getAll().get(3).getName(), sh.getAll().get(3).isWiFiState(), sh.getAll().get(3).isBluetoothState(), sh.getAll().get(3).isMobileState(), 3, 3);
//        state.addState(sh.getAll().get(4).getName(), sh.getAll().get(4).isWiFiState(), sh.getAll().get(4).isBluetoothState(), sh.getAll().get(4).isMobileState(), 4, 4);


        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator()); //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new RVAdapter(state, view.getContext())); //устанавливаем наш адаптер
        return view;
    }
}