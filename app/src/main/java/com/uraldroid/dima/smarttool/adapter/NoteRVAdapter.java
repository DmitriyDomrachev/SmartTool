package com.uraldroid.dima.smarttool.adapter;

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

import com.uraldroid.dima.smarttool.DB.NoteHelper;
import com.uraldroid.dima.smarttool.Note;
import com.uraldroid.dima.smarttool.R;
import com.uraldroid.dima.smarttool.activity.ShowActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by dima on 19.04.2018.
 */

public class NoteRVAdapter extends RecyclerView.Adapter<NoteRVAdapter.ContactsViewHolder> {

    private static ArrayList<Note> notes;
    private Context context;

    public NoteRVAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cv, parent, false);
        return new ContactsViewHolder(view); //передаём вьюшку в качестве аргумента для холдера
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {
        final Note note = notes.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(note.getStartTime());
        long millis = calendar.getTimeInMillis();
        int hour = (int) TimeUnit.MILLISECONDS.toHours(millis);
        int minute = (int) TimeUnit.MILLISECONDS.toMinutes(millis - hour * 3600000);
        holder.txtName.setText(String.valueOf(note.getName()));
        if (note.getLat() == 0 && note.getStartTime() != 999999999) {
            holder.iconImageView.setImageResource(R.drawable.alarm);
            if (minute < 10)
                holder.txtTime.setText(String.valueOf(hour + ":0" + minute));
            else holder.txtTime.setText(String.valueOf(hour + ":" + minute));

            holder.txtTime.setVisibility(View.VISIBLE);
        } else if (note.getLat() == 0 && note.getLng() == 0 && note.getStartTime() == 999999999) {
            holder.iconImageView.setImageResource(R.drawable.hand);
            holder.noteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.iconImageView.setImageResource(R.drawable.my_location);
            holder.gpsImageView.setVisibility(View.VISIBLE);
        }
        holder.cvListener.setRecord(note);// как-то надо понимать с каким напоминанием работаем
        String noteText = note.getText();
        if (noteText.length() > 100)
            noteText = noteText.substring(0, 100) + "...";
        holder.txtText.setText(noteText);
        holder.btnClickListener.setRecord(note);
        //напоминание, с которым работаем

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

   //инциализации всех View-элементов
    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtText, txtStart, txtTime, txtDelete;
        CardView cv;
        ImageView iconImageView, gpsImageView, noteImageView;


        //Инициализируем слушатели
        CardViewClickListener cvListener = new CardViewClickListener();
        ButtonRemoveClickListener btnClickListener = new ButtonRemoveClickListener();

        ContactsViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.cvNoteNameTextView);
            txtStart = itemView.findViewById(R.id.cvNoteStartTextView);
            txtText = itemView.findViewById(R.id.cvNoteTextTextView);
            txtTime = itemView.findViewById(R.id.сvNoteTimeTextView);
            txtDelete = itemView.findViewById(R.id.cvNoteDeleteTextView);
            cv = itemView.findViewById(R.id.note_rv);
            iconImageView = itemView.findViewById(R.id.cvNoteImageView);
            gpsImageView = itemView.findViewById(R.id.cvNoteMapImageView);
            noteImageView = itemView.findViewById(R.id.cvNoteNoteImageView);


            cv.setOnClickListener(cvListener);
            txtDelete.setOnClickListener(btnClickListener);


        }
    }

    //классы для обработки нажатий

    class CardViewClickListener implements View.OnClickListener {

        private Note note;

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ShowActivity.class);
            intent.putExtra("type", "Note");
            intent.putExtra("name", note.getName());
            intent.putExtra("text", note.getText());
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
            nh.deleteNote(String.valueOf(note.getId()));
            Log.d("DB", nh.getAll().toString());
            notes.remove(note);                     // удаляем его из списка
            notifyItemRemoved(position);            // метод для удалаении из RecyclerView

        }

        void setRecord(Note note) {
            this.note = note;
        }
    }


}

