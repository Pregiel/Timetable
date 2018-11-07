package com.pregiel.planzajec;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LessonsAdapter extends ArrayAdapter<Lesson> {
    public int cDay;


    public LessonsAdapter(DayPageFragment context, Day day, int currDay) {
        super(context.getContext(), 0, day.getLessonList());
        cDay = currDay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Lesson lesson = getItem(position);
        final TextView begin, end, name, classroom, teacher, absenceText;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_element, parent, false);
        }


        begin = convertView.findViewById(R.id.begin);
        end = convertView.findViewById(R.id.end);
        name = convertView.findViewById(R.id.name);
        classroom = convertView.findViewById(R.id.classroom);
        teacher = convertView.findViewById(R.id.teacher);
        absenceText = convertView.findViewById(R.id.absenceText);
        final View absenceInfo = convertView.findViewById(R.id.absenceInfo);

        begin.setText(lesson.getBegin());
        end.setText(lesson.getEnd());

        final String lessonName;
        if (lesson.getType().equals("")) {
            lessonName = lesson.getName();
        } else {
            lessonName = lesson.getName() + " (" + lesson.getType() + ")";
        }
        name.setText(lessonName);
        classroom.setText(lesson.getClassroom());
        teacher.setText(lesson.getTeacher());


        LinearLayout layout = convertView.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.findViewById(R.id.expandable_layout).getVisibility() == View.GONE) {
                    view.findViewById(R.id.expandable_layout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.expandable_layout).setVisibility(View.GONE);
                }
            }
        });

        setAbsenceText(lesson, absenceText, convertView);
        setAbsenceInfo(lesson, absenceInfo);

        final View finalConvertView = convertView;
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                String[] options = {view.getResources().getString(R.string.add),
                        view.getResources().getString(R.string.edit),
                        view.getResources().getString(R.string.delete),
                        view.getResources().getString(R.string.add_absence),
                        view.getResources().getString(R.string.remove_absence)};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                addNewLesson();
                                break;

                            case 1: // edit
                                editLesson(lesson, finalConvertView);
                                break;

                            case 2:
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                                builder3.setMessage(getContext().getResources().getString(R.string.are_you_sure) + " " + lessonName + "?");
                                builder3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.dayList.get(cDay).getLessonList().remove(lesson);

                                        MainActivity.writeToXMLTimetableFile(getContext());

                                        notifyDataSetChanged();
                                    }
                                });
                                builder3.setNegativeButton(R.string.cancel, null);
                                AlertDialog dial3 = builder3.create();
                                dial3.show();
                                break;

                            case 3:
                                addNewAbsence(lesson, finalConvertView);
                                break;

                            case 4:
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(getContext());
                                builder4.setMessage(getContext().getResources().getString(R.string.are_you_sure_absence) + " " + lessonName + "?");
                                builder4.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        MainActivity.dayList.get(cDay).getLessonList().remove(lesson);
                                        lesson.getAbsence().clear();
                                        setAbsenceText(lesson, absenceText, finalConvertView);
                                        MainActivity.writeToXMLTimetableFile(getContext());

                                        notifyDataSetChanged();
                                    }
                                });
                                builder4.setNegativeButton(R.string.cancel, null);
                                AlertDialog dial4 = builder4.create();
                                dial4.show();
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });

        return convertView;
    }

    private void setAbsenceInfo(Lesson lesson, View absenceInfo) {
        int absenceCount = lesson.getAbsence().size();
        if (absenceCount > MainActivity.settings.getThirdWarning()) {
            absenceInfo.setBackgroundColor(getContext().getResources().getColor(R.color.absence_last));
        } else if (absenceCount > MainActivity.settings.getSecondWarning()) {
            absenceInfo.setBackgroundColor(getContext().getResources().getColor(R.color.absence_third));
        } else if (absenceCount > MainActivity.settings.getFirstWarning()) {
            absenceInfo.setBackgroundColor(getContext().getResources().getColor(R.color.absence_second));
        } else if (absenceCount > 0 || MainActivity.settings.getFirstWarning() == 0) {
            absenceInfo.setBackgroundColor(getContext().getResources().getColor(R.color.absence_first));
        } else {
            absenceInfo.setBackgroundColor(getContext().getResources().getColor(R.color.absence_safe));
        }
    }

    private void setAbsenceText(Lesson lesson, TextView absenceText, View convertView) {
        if (lesson.getAbsence().size() > 0) {
            absenceText.setVisibility(View.VISIBLE);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(convertView.getResources().getString(R.string.absence)).append(" ");
            for (String s : lesson.getAbsence()) {
                stringBuilder.append("\n").append("   ").append(s);
            }

            absenceText.setText(stringBuilder.toString());
        } else {
            absenceText.setVisibility(View.GONE);
        }
    }

    private void editLesson(final Lesson lesson, View convertView) {
        final TextView begin, end, name, classroom, teacher, absenceText;

        begin = convertView.findViewById(R.id.begin);
        end = convertView.findViewById(R.id.end);
        name = convertView.findViewById(R.id.name);
        classroom = convertView.findViewById(R.id.classroom);
        teacher = convertView.findViewById(R.id.teacher);
        absenceText = convertView.findViewById(R.id.absenceText);


        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
        builder2.setTitle(getContext().getResources().getString(R.string.edit));
        LayoutInflater inflater2 = LayoutInflater.from(getContext());
        builder2.setView(inflater2.inflate(R.layout.dialog_edit, null));

        builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                lesson.setName(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.name)).getText()));
                lesson.setBegin(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.beginTime)).getText()));
                lesson.setEnd(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.endTime)).getText()));
                lesson.setClassroom(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.classroom)).getText()));
                lesson.setTeacher(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.teacher)).getText()));
                lesson.setType(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.type)).getText()));

                MainActivity.writeToXMLTimetableFile(getContext());

                begin.setText(lesson.getBegin());
                end.setText(lesson.getEnd());
                final String lessonName;
                if (lesson.getType().equals("")) {
                    lessonName = lesson.getName();
                } else {
                    lessonName = lesson.getName() + " (" + lesson.getType() + ")";
                }
                name.setText(lessonName);
                classroom.setText(lesson.getClassroom());
                teacher.setText(lesson.getTeacher());
            }
        });
        builder2.setNegativeButton(R.string.cancel, null);

        AlertDialog dial2 = builder2.create();
        dial2.show();

        ((EditText) dial2.findViewById(R.id.name)).setText(lesson.getName());
        ((EditText) dial2.findViewById(R.id.beginTime)).setText(lesson.getBegin());
        ((EditText) dial2.findViewById(R.id.endTime)).setText(lesson.getEnd());
        ((EditText) dial2.findViewById(R.id.classroom)).setText(lesson.getClassroom());
        ((EditText) dial2.findViewById(R.id.teacher)).setText(lesson.getTeacher());
        ((EditText) dial2.findViewById(R.id.type)).setText(lesson.getType());

        final EditText beginTime2 = dial2.findViewById(R.id.beginTime);
        beginTime2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] time = beginTime2.getText().toString().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        beginTime2.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });
//
        final EditText endTime2 = dial2.findViewById(R.id.endTime);
        endTime2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] time = endTime2.getText().toString().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime2.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });
    }

    public void addNewLesson() {
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

                MainActivity.dayList.get(cDay).getLessonList().add(newLesson);

                MainActivity.sortDayLessons(MainActivity.dayList, cDay);

                MainActivity.writeToXMLTimetableFile(getContext());

                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dial = builder.create();
        dial.show();


        final EditText beginTime = dial.findViewById(R.id.beginTime);
        beginTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] time = beginTime.getText().toString().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        beginTime.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });
//
        final EditText endTime = dial.findViewById(R.id.endTime);
        endTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] time = endTime.getText().toString().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });
    }

    public void addNewAbsence(final Lesson lesson, final View convertView) {
        final TextView absenceText;

        absenceText = convertView.findViewById(R.id.absenceText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.add_absence));
        LayoutInflater inflater = LayoutInflater.from(getContext());
        builder.setView(inflater.inflate(R.layout.dialog_add_absence, null));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                lesson.addAbsence(String.valueOf(((EditText) ((AlertDialog) dialog).findViewById(R.id.absence_date)).getText()));

                setAbsenceText(lesson, absenceText, convertView);

                MainActivity.writeToXMLTimetableFile(getContext());

                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dial = builder.create();
        dial.show();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        final EditText date = dial.findViewById(R.id.absence_date);
        date.setText(df.format(c));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dateText = ((EditText) v.findViewById(R.id.absence_date)).getText().toString().split("\\.");
                int day = Integer.parseInt(dateText[0]);
                int month = Integer.parseInt(dateText[1]) - 1;
                int year = Integer.parseInt(dateText[2]);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringBuilder stringBuilder = new StringBuilder();
                        if (dayOfMonth < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(dayOfMonth).append(".");
                        if (month + 1 < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(month + 1).append(".").append(year);
                        date.setText(stringBuilder.toString());
                    }
                }, year, month, day);
                mDatePicker.show();

            }
        });
    }

}
