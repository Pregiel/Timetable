package com.pregiel.planzajec;

import java.util.ArrayList;

/**
 * Created by Pregiel on 14.02.2018.
 */

public class Day {
    private String name;
    private ArrayList<Lesson> lessonList;

    public Day(String name) {
        this.name = name;
        lessonList = new ArrayList<Lesson>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Lesson> getLessonList() {
        return lessonList;
    }

    public void setLessonList(ArrayList<Lesson> lessonList) {
        this.lessonList = lessonList;
    }
}
