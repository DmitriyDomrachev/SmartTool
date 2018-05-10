package com.example.dima.smarttool.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.Note;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.ShowActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by dima on 19.04.2018.
 */

public class NoteRVAdapter extends RecyclerView.Adapter<NoteRVAdapter.ContactsViewHolder> {

    private static ArrayList<Note> notes;
    private Context context;

    NoteRVAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cv, parent, false); // создаём вьюшку для кажого элемента
        return new ContactsViewHolder(view); //передаём вьюшку в качестве аргумента для холдера
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {                       //тут будет просходить обработка каждого элемента, кога он появится на экране
        final Note note = notes.get(position);                                                       // получаем элемент для удобства использования
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(note.getStartTime());
        long millis = calendar.getTimeInMillis();
        int hour = (int) TimeUnit.MILLISECONDS.toHours(millis);
        int minute = (int) TimeUnit.MILLISECONDS.toMinutes(millis - hour * 3600000);
        holder.txtName.setText(String.valueOf(note.getName()));
        if (note.getLat() == 0 && note.getStartTime() != 999999999) {
            holder.txtStart.setText("Время включения " + String.valueOf(hour) + ":" + String.valueOf(minute));
            holder.imageView.setImageResource(R.drawable.alarm);
        } else if (note.getLat() == 0 && note.getLng() == 0 && note.getStartTime() == 999999999)
            holder.imageView.setImageResource(R.drawable.hand);
        else {
            holder.imageView.setImageResource(R.drawable.my_location);

        }
        holder.cvListener.setRecord(note);// как-то надо понимать с каким состоянием работаем
        String noteText = note.getText();
        if(noteText.length() > 25)
            noteText = noteText.substring(0,20) + "...";
        holder.txtText.setText(noteText);
        holder.btnClickListener.setRecord(note);                                                       // как-то надо понимать с состоянием  работаем

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    //это самый первый класс, который вы должны создать при содании адептера. В нём происходит инциализации всех View-элементов.
    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtText, txtStart;
        ImageButton btnRefactor;
        CardView cv;
        ImageView imageView;


        //Инициализируем слушатели
        CardViewClickListener cvListener = new CardViewClickListener();
        ButtonRemoveClickListener btnClickListener = new ButtonRemoveClickListener();

        ContactsViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.cvNoteNameTextView);
            txtStart = itemView.findViewById(R.id.cvNoteStartTextView);
            txtText = itemView.findViewById(R.id.cvNoteTextTextView);
            btnRefactor = itemView.findViewById(R.id.cvNoteRemoveButton);
            cv = itemView.findViewById(R.id.note_rv);
            imageView = itemView.findViewById(R.id.cvNoteImageView);


            //цепляем слушатели
            cv.setOnClickListener(cvListener);
            btnRefactor.setOnClickListener(btnClickListener);


        }
    }

    //классы для обработки нажатий. Главное, чтобы они релизовывали интерфейс View.OnClickListener

    class CardViewClickListener implements View.OnClickListener {

        private Note note;

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ShowActivity.class);
            intent.putExtra("name", note.getName());
            intent.putExtra("note", note.getText());
            intent.putExtra("lat", note.getLat());
            intent.putExtra("lng", note.getLng());
            intent.putExtra("time", note.getStartTime());
            context.startActivity(intent);
        }

        void setRecord(Note note) {
            this.note = note;
        }
    }


    class ButtonRemoveClickListener implements View.OnClickListener {
        Note note;

        @Override
        public void onClick(View v) {
            int position = notes.indexOf(note);        // получаем индекс удаляемого элемента
            NoteHelper nh = new NoteHelper(context);
            nh.deleteState(String.valueOf(note.getId()));
            Log.d("DB", nh.getAll().toString());
            notes.remove(note);                     // удаляем его из списка
            notifyItemRemoved(position);            // метод для удалаении из самого RecyclerView. Именно он отвечает за анимации

        }

        void setRecord(Note note) {
            this.note = note;
        }
    }


}

