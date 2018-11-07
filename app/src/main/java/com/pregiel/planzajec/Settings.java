package com.pregiel.planzajec;

/**
 * Created by Pregiel on 22.02.2018.
 */

public class Settings {
    private String currentTimetable;
    private boolean[] visibleDays;

    public Settings() {
        this.currentTimetable = "timatable1.xml";
        this.visibleDays = new boolean[]{true, true, true, true, true, true, true};
    }

    public Settings(String currentTimetable, boolean[] visibleDays) {
        this.currentTimetable = currentTimetable;
        this.visibleDays = visibleDays;
    }

    public String getCurrentTimetable() {
        return currentTimetable;
    }

    public void setCurrentTimetable(String currentTimetable) {
        this.currentTimetable = currentTimetable;
    }

    public boolean[] getVisibleDays() {
        return visibleDays;
    }

    public void setVisibleDays(boolean[] visibleDays) {
        this.visibleDays = visibleDays;
    }
}
