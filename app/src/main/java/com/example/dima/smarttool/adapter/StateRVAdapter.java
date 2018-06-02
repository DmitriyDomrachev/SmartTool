package com.example.dima.smarttool.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;
import com.example.dima.smarttool.activity.ShowActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by dima on 19.03.2018.
 */
public class StateRVAdapter extends RecyclerView.Adapter<StateRVAdapter.ContactsViewHolder> {

    private static ArrayList<State> states;
    private Context context;

    public StateRVAdapter(ArrayList<State> states, Context context) {
        this.states = states;
        this.context = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_cv, parent, false);
        return new ContactsViewHolder(view); //передаём view в качестве аргумента для холдера
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {
        final State state = states.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(state.getStartTime());
        long millis = calendar.getTimeInMillis();
        int hour = (int) TimeUnit.MILLISECONDS.toHours(millis);
        int minute = (int) TimeUnit.MILLISECONDS.toMinutes(millis - hour * 3600000);
        holder.txtName.setText(String.valueOf(state.getName()));
        if (state.getLat() == 0 && state.getStartTime() != 999999999) {
            if (minute > 10)
                holder.txtTime.setText(String.valueOf(hour + ":" + minute));
            else holder.txtTime.setText(String.valueOf(hour + ":0" + minute));

            holder.txtTime.setVisibility(View.VISIBLE);
            holder.iconImageView.setImageResource(R.drawable.alarm);
        } else if (state.getLat() == 0 && state.getLng() == 0 && state.getStartTime() == 999999999) {
            holder.iconImageView.setImageResource(R.drawable.hand);
            holder.settingImageView.setVisibility(View.VISIBLE);
        } else {
            holder.iconImageView.setImageResource(R.drawable.my_location);
            holder.gpsImageView.setVisibility(View.VISIBLE);
        }
        holder.cvListener.setRecord(state);
        holder.btnClickListener.setRecord(state);
        // состояние, с которым работаем

    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    //инциализация всех View-элементов.
    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtDelete, txtTime;
        CardView cv;
        ImageView iconImageView, gpsImageView, settingImageView;

        //Инициализируем слушатели
        CardViewClickListener cvListener = new CardViewClickListener();
        ButtonRemoveClickListener btnClickListener = new ButtonRemoveClickListener();

        ContactsViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.cvRuleNameTextView);
            txtDelete = itemView.findViewById(R.id.cvRuleDeleteTextView);
            txtTime = itemView.findViewById(R.id.сvRuleTimeTextView);
            iconImageView = itemView.findViewById(R.id.cvRuleImageView);
            gpsImageView = itemView.findViewById(R.id.cvRuleMapImageView);
            settingImageView = itemView.findViewById(R.id.cvRuleSettingImageView);

            cv = itemView.findViewById(R.id.rule_rv);

            //цепляем слушатели
            cv.setOnClickListener(cvListener);
            txtDelete.setOnClickListener(btnClickListener);


        }
    }

    //классы для обработки нажатий

    class CardViewClickListener implements View.OnClickListener {

        private State state;

        @Override
        public void onClick(View v) {
            String bt, wifi;
            if (!state.isBluetoothState())
                bt = "off";
            else bt = "on";

            if (!state.isWiFiState())
                wifi = "off";
            else wifi = "on";

            Intent intent = new Intent(context, ShowActivity.class);
            intent.putExtra("name", state.getName());
            intent.putExtra("text", "Wifi: " + wifi
                    + "\nBluetooth: " + bt + "\nMedia: " + state.getMediaSoundState()
                    + "%\nSystem: " + state.getSystemSoundState() + "%");
            Log.d("showActivity", "IntentPut lat: " + state.getLat() + "    lng: " + state.getLng());
            intent.putExtra("type", "State");
            intent.putExtra("lat", state.getLat());
            intent.putExtra("lng", state.getLng());
            intent.putExtra("time", state.getStartTime());
            context.startActivity(intent);
        }

        void setRecord(State state) {
            this.state = state;
        }
    }

    class ButtonRemoveClickListener implements View.OnClickListener {
        State state;

        @Override
        public void onClick(View v) {
            int position = states.indexOf(state); // получаем индекс удаляемого элемента
            StateHelper sh = new StateHelper(context);
            sh.deleteState(String.valueOf(state.getId()));
            Log.d("DB", sh.getAll().toString());
            states.remove(state); // удаляем его из списка
            notifyItemRemoved(position); // метод для удалаении из RecyclerView

        }

        void setRecord(State state) {
            this.state = state;
        }
    }


}

