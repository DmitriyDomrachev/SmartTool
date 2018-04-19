package com.example.dima.smarttool.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.example.dima.smarttool.AddNoteActivity;
import com.example.dima.smarttool.AddStateActivity;

import java.util.Calendar;

/**
 * Created by dima on 02.04.2018.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    // TODO: start this activity for result

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d ("time"," set time: "+String.valueOf(hourOfDay)+":"+String.valueOf(minute));
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

