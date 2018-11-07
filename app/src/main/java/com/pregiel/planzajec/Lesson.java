package com.pregiel.planzajec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pregiel on 14.02.2018.
 */

public class Lesson {
    private String begin;
    private String end;
    private String name;
    private String classroom;
    private String teacher;
    private String type;
    private String type2;
    private List<String> absence;

    public Lesson() {
        this.begin = null;
        this.end = null;
        this.name = null;
        this.classroom = null;
        this.teacher = null;
        this.type = null;
        this.type2 = null;
        this.absence = new ArrayList<>();
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public void addAbsence(String date) {
        absence.add(date);
    }

    public String getBegin() {
        return begin;
    }

    public String getBegin2() {
        String[] time = begin.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        if (hour < 10) {
            return "0" + hour + ":" + minute;
        }
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getType() {
        return type;
    }

    public String getType2() {
        return type2;
    }

    public List<String> getAbsence() {
        return absence;
    }
}
