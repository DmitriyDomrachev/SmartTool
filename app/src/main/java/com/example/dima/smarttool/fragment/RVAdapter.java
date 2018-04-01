package com.example.dima.smarttool.fragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.State;

import java.util.ArrayList;

/**
 * Created by dima on 19.03.2018.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactsViewHolder> {

    private ArrayList<State> states;
    private Context context;

    public RVAdapter(ArrayList<State> states, Context context) {
        this.states = states;
        this.context = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_cv, parent, false); // создаём вьюшку для кажого элемента
        return new ContactsViewHolder(view); //передаём вьюшку в качестве аргумента для холдера
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) { //тут будет просходить обработка каждого элемента, кога он появится на экране
        final State state = states.get(position);// получаем элемент для удобства использования

        holder.txtName.setText(holder.txtName.getText()+String.valueOf(state.getName()));
        holder.txtWiFi.setText(holder.txtWiFi.getText()+String.valueOf(state.isWiFiState()));
        holder.txtMobile.setText(holder.txtMobile.getText()+String.valueOf(state.isMobileState()));
        holder.txtBluetooth.setText(holder.txtBluetooth.getText()+String.valueOf(state.isBluetoothState()));
        holder.cvListener.setRecord(state);// как-то надо понимать с каким фильмом работаем
        holder.btnClickListener.setRecord(state); // как-то надо понимать с фильмом  работаем

    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    //это самый первый класс, который вы должны создать при содании адептера. В нём происходит инциализации всех View-элементов. Ага!
    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtWiFi, txtMobile, txtBluetooth;
        Button btnRefactor;
        CardView cv;

        //Инициализируем слушатели
        CardViewClickListener cvListener = new CardViewClickListener();
        ButtonRemoveClickListener btnClickListener = new ButtonRemoveClickListener();

        ContactsViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.cvNameTextView);
            txtWiFi = itemView.findViewById(R.id.cvWiFiTextView);
            txtMobile = itemView.findViewById(R.id.cvMobileTextView);
            txtBluetooth= itemView.findViewById(R.id.cvBluetoothTextView);
            btnRefactor = itemView.findViewById(R.id.cvButton);
            cv = itemView.findViewById(R.id.cv_rv);

            //цепляем слушатели
            cv.setOnClickListener(cvListener);
            btnRefactor.setOnClickListener(btnClickListener);


        }
    }

    //классы для обработки нажатий. Главное, чтобы они релизовывали интерфейс View.OnClickListener

    class CardViewClickListener implements View.OnClickListener {

        private State state;

        @Override
        public void onClick(View v) {
            Toast.makeText(context,"он клик, гав",Toast.LENGTH_SHORT).show();
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
            Log.d("BD",sh.getAll().toString());
            states.remove(state); // удаляем его из списка
            notifyItemRemoved(position); // метод для удалаении из самого RecyclerView. Именно он отвечает за анимации
        }

        void setRecord(State state) {
            this.state = state;
        }
    }
}

