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

import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;
import com.example.dima.smarttool.activity.MainActivity;
import com.example.dima.smarttool.adapter.StateRVAdapter;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class StateFragment extends Fragment {
    public static final String FIRST_START_STATES = "firstStates";
    ArrayList<State> stateArrayList = new ArrayList<>(MainActivity.getStateArr());
    RecyclerView rv;
    TextView firstTextView;
    Boolean firstStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_states, container, false);

        firstTextView = view.findViewById(R.id.ruleFragmentStartTextView);
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        firstStart = prefs.getBoolean(FIRST_START_STATES, true);
        if (firstStart)
            firstTextView.setVisibility(View.VISIBLE);
        //текст подсказка при первом запуске

        rv = view.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new StateRVAdapter(stateArrayList, view.getContext()));
        return view;
    }
}