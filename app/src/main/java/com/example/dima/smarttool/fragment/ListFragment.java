package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dima.smarttool.R;
import com.example.dima.smarttool.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 27.02.2018.
 */

public class ListFragment extends Fragment {
    List<Rule> rules = new ArrayList<>();
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        rules.add(new Rule(1,"уловие1","действие1"));
        rules.add(new Rule(2,"уловие2","действие2"));
        rules.add(new Rule(3,"уловие3","действие3"));
        rules.add(new Rule(4,"уловие4","действие4"));
        rules.add(new Rule(5,"уловие5","действие5"));
        rules.add(new Rule(6,"уловие6","действие6"));
        rules.add(new Rule(7,"уловие7","действие7"));
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext())); // устанавливаем разметку для списка.
        rv.setItemAnimator(new DefaultItemAnimator()); //устанавливаем класс, отвечающий за анимации в списке
        rv.setAdapter(new RVAdapter(rules, view.getContext())); //устанавливаем наш адаптер
        return view;
    }
}
