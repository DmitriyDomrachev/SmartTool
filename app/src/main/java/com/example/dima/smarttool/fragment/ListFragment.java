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

import com.example.dima.smarttool.MainActivity;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class ListFragment extends Fragment {
    ArrayList<State> stateArrayList = new ArrayList<>(MainActivity.getStateArr());
    RecyclerView rv;
    TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        name = view.findViewById(R.id.fragListNameTextView);
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        name.setText("Последнее состояние: "+String.valueOf(prefs.getString("stateName","")));
        rv = view.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator()); //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new RuleRVAdapter(stateArrayList, view.getContext())); //устанавливаем наш адаптер
        return view;
    }
}