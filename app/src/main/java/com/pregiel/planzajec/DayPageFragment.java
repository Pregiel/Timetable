package com.pregiel.planzajec;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DayPageFragment extends Fragment {
    public DayPageFragment() {
        // Required empty public constructor
    }
    private PopupMenu popupMenu;
    private LessonsAdapter adapter;

    public LessonsAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_day_page_layout, container, false);



        Bundle bundle = getArguments();
        adapter = new LessonsAdapter(this, MainActivity.dayList.get(bundle.getInt("currentday")), bundle.getInt("currentday"));

        final ListView listView = view.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        TextView dayName = view.findViewById(R.id.day);
        dayName.setText(getDayName());

        ImageButton menu = view.findViewById(R.id.imageButton);
        popupMenu = new PopupMenu(getContext(), menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getContext().getResources().getString(R.string.add));
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        builder.setView(inflater.inflate(R.layout.dialog_edit, null));

                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Lesson newLesson = new Lesson();
                                newLesson.setName(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.name)).getText()));
                                newLesson.setBegin(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.beginTime)).getText()));
                                newLesson.setEnd(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.endTime)).getText()));
                                newLesson.setClassroom(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.classroom)).getText()));
                                newLesson.setTeacher(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.teacher)).getText()));
                                newLesson.setType(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.type)).getText()));
                                newLesson.setType2("normal");

                                Bundle bundle = getArguments();
                                MainActivity.dayList.get(bundle.getInt("currentday")).getLessonList().add(newLesson);

                                MainActivity.sortDayLessons(MainActivity.dayList, bundle.getInt("currentday"));

                                MainActivity.writeToXMLTimetableFile(getContext());


                                adapter.notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, null);

                        AlertDialog dial = builder.create();
                        dial.show();



                        final EditText beginTime = dial.findViewById(R.id.beginTime);
                        final EditText endTime = dial.findViewById(R.id.endTime);

                        beginTime.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String[] time = beginTime.getText().toString().split(":");
                                int hour = Integer.parseInt(time[0]);
                                int minute = Integer.parseInt(time[1]);

                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String t = selectedHour + ":" + String.format("%02d", selectedMinute);
                                        beginTime.setText(t);
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePicker.show();

                            }
                        });
                        beginTime.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                                String[] time = beginTime.getText().toString().split(":");
                                int minute = Integer.parseInt(time[1]) + MainActivity.defLessonDuration;
                                int hour = Integer.parseInt(time[0]) + minute/60;
                                minute = minute % 60;
                                hour = hour % 24;
                                String t = hour + ":" + String.format("%02d", minute);
                                endTime.setText(t);
                            }
                        });
//
                        endTime.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                String[] time = endTime.getText().toString().split(":");
                                int hour = Integer.parseInt(time[0]);
                                int minute = Integer.parseInt(time[1]);

                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String t = selectedHour + ":" + String.format("%02d", selectedMinute);
                                        endTime.setText(t);
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePicker.show();

                            }
                        });
                        break;

                    case R.id.pref:

                        break;
                }
                return false;
            }
        });
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.actions, popupMenu.getMenu());
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        return view;
    }

    public String getDayName() {
        Bundle bundle = getArguments();

        switch (MainActivity.dayList.get(bundle.getInt("currentday")).getName()) {
            case "monday":
                return getResources().getString(R.string.day_monday);

            case "tuesday":
                return getResources().getString(R.string.day_tuesday);

            case "wednesday":
                return getResources().getString(R.string.day_wednesday);

            case "thursday":
                return getResources().getString(R.string.day_thursday);

            case "friday":
                return getResources().getString(R.string.day_friday);

            case "saturday":
                return getResources().getString(R.string.day_saturday);

            case "sunday":
                return getResources().getString(R.string.day_sunday);

        };
        return MainActivity.dayList.get(bundle.getInt("currentday")).getName();
    }


}
