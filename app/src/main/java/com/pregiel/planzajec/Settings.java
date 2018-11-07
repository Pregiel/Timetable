package com.pregiel.planzajec;

/**
 * Created by Pregiel on 22.02.2018.
 */

public class Settings {
    private String currentTimetable;
    private boolean[] visibleDays;
    private int firstWarning, secondWarning, thirdWarning;

    public Settings() {
        this.currentTimetable = "timatable1.xml";
        this.visibleDays = new boolean[]{true, true, true, true, true, true, true};
        this.firstWarning = 1;
        this.secondWarning = 2;
        this.thirdWarning = 3;
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

    public int getFirstWarning() {
        return firstWarning;
    }

    public void setFirstWarning(int firstWarning) {
        this.firstWarning = firstWarning;
    }

    public int getSecondWarning() {
        return secondWarning;
    }

    public void setSecondWarning(int secondWarning) {
        this.secondWarning = secondWarning;
    }

    public int getThirdWarning() {
        return thirdWarning;
    }

    public void setThirdWarning(int thirdWarning) {
        this.thirdWarning = thirdWarning;
    }
}
