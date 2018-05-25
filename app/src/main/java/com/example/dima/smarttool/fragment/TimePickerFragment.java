package com.example.dima.smarttool.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.example.dima.smarttool.activity.AddNoteActivity;
import com.example.dima.smarttool.activity.AddStateActivity;

import java.util.Calendar;

/**
 * Created by dima on 02.04.2018.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        Log.d ("addState"," createDialog");

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    // TODO: start this activity for result

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d ("addState"," set time: "+String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        try {
            AddStateActivity.setTime(hourOfDay, minute);
            AddStateActivity.setHour(hourOfDay);
            AddStateActivity.setMinute(minute);
        } catch (Throwable t){}

        try {
            AddNoteActivity.setTime(hourOfDay, minute);
            AddNoteActivity.setHour(hourOfDay);
            AddNoteActivity.setMinute(minute);
        } catch (Throwable t){}
    }
}

