package com.example.dima.smarttool.fragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.smarttool.R;
import com.example.dima.smarttool.Rule;

import java.util.List;

/**
 * Created by dima on 19.03.2018.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ContactsViewHolder> {

    private List<Rule> rules;
    private Context context;

    public RVAdapter(List<Rule> rules, Context context) {
        this.rules = rules;
        this.context = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_cv, parent, false); // создаём вьюшку для кажого элемента
        return new ContactsViewHolder(view); //передаём вьюшку в качестве аргумента для холдера
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) { //тут будет просходить обработка каждого элемента, кога он появится на экране
        final Rule rule = rules.get(position);// получаем элемент для удобства использования

        holder.txtCondition.setText(String.valueOf(rule.getCondition()));
        holder.txtAct.setText(rule.getAct());


        holder.cvListener.setRecord(rule);// как-то надо понимать с каким фильмом работаем
        holder.btnClickListener.setRecord(rule); // как-то надо понимать с фильмом  работаем

    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    //это самый первый класс, который вы должны создать при содании адептера. В нём происходит инциализации всех View-элементов. Ага!
    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView txtCondition, txtAct;
        Button btnRefactor;
        CardView cv;

        //Инициализируем слушатели
        CardViewClickListener cvListener = new CardViewClickListener();
        ButtonRemoveClickListener btnClickListener = new ButtonRemoveClickListener();

        ContactsViewHolder(View itemView) {
            super(itemView);

            txtCondition = itemView.findViewById(R.id.cvConditionTextView);
            txtAct = itemView.findViewById(R.id.cvActTextView);
            btnRefactor = itemView.findViewById(R.id.cvButton);
            cv = itemView.findViewById(R.id.cv_rv);

            //цепляем слушатели
            cv.setOnClickListener(cvListener);
            btnRefactor.setOnClickListener(btnClickListener);


        }
    }

    //классы для обработки нажатий. Главное, чтобы они релизовывали интерфейс View.OnClickListener

    class CardViewClickListener implements View.OnClickListener {

        private Rule rule;

        @Override
        public void onClick(View v) {
            Toast.makeText(context,"он клик, гав",Toast.LENGTH_SHORT).show();
        }

        void setRecord(Rule rule) {
            this.rule = rule;
        }
    }

    class ButtonRemoveClickListener implements View.OnClickListener {
        Rule rule;

        @Override
        public void onClick(View v) {
            int position = rules.indexOf(rule); // получаем индекс удаляемого элемента
//            ContactsHelper ch = new ContactsHelper(context);
//            ch.deleteContact(String.valueOf(contact.getId()));
//
//
//            Rules.remove(rule); // удаляем его из списка
            notifyItemRemoved(position); // метод для удалаении из самого RecyclerView. Именно он отвечает за анимации
        }

        void setRecord(Rule rule) {
            this.rule = rule;
        }
    }
}
