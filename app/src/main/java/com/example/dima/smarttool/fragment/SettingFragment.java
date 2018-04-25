package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.R;

/**
 * Created by dima on 27.02.2018.
 */

public class SettingFragment extends Fragment {
    Button clear;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        clear = view.findViewById(R.id.SettingFragmentClearButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
                hh.clearTable();
            }
        });
        return view;
    }
}

