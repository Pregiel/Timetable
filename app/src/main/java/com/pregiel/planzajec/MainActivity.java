package com.pregiel.planzajec;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity  {

    static public ArrayList<Day> dayList;
    static public int lessonDayAmount = 7;
    static public String fileName = "timetable1.xml";
    static public String settingsFileName = "settings.xml";
    static public int defLessonDuration = 90;
    static public Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        deleteFile(fileName);
        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s;
//            try {
//                in_s = openFileInput(settingsFileName);
//            } catch (FileNotFoundException e) {
            in_s = getApplicationContext().getAssets().open("settings.xml");
//            }

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            settings = parseXMLSettings(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s;
            try {
                in_s = openFileInput(fileName);
            } catch (FileNotFoundException e) {
                in_s = getApplicationContext().getAssets().open("timetable.xml");
            }

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            dayList = parseXMLTimetable(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        lessonDayAmount = dayList.size();
        ViewPager viewPager = findViewById(R.id.view_pager);

        SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdapter);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (day < 0) {
            day += 7;
        }
        if (day > dayList.size()-1) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(day);
        }

        writeToXMLTimetableFile(this);
    }

    public static void sortDayLessons (ArrayList<Day> list, int day) {
        Collections.sort(list.get(day).getLessonList(), new Comparator<Lesson>() {
            @Override
            public int compare(Lesson l1, Lesson l2) {
                return l1.getBegin2().compareTo(l2.getBegin2());
            }
        });

    }

    public static void writeToXMLTimetableFile(Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(createXMLFile().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Day> parseXMLTimetable(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        ArrayList<Day> dayList = null;
        int eventType = parser.getEventType();
        Day currentDay;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dayList = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if ((name.equals("monday") && settings.getVisibleDays()[0]) ||
                            (name.equals("tuesday") && settings.getVisibleDays()[1]) ||
                            (name.equals("wednesday") && settings.getVisibleDays()[2]) ||
                            (name.equals("thursday") && settings.getVisibleDays()[3]) ||
                            (name.equals("friday") && settings.getVisibleDays()[4]) ||
                            (name.equals("saturday") && settings.getVisibleDays()[5]) ||
                            (name.equals("sunday") && settings.getVisibleDays()[6])) {
                        System.out.println(name);
                        currentDay = readDay(parser, name);
                        dayList.add(currentDay);
                    }
                    break;
            }
            eventType = parser.next();
        }

        return dayList;
    }

    private Day readDay(XmlPullParser parser, String dayName) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, dayName);

        int eventType = parser.getEventType();
        Day currentDay = new Day(dayName);

        while (eventType != XmlPullParser.END_TAG) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("lesson")) {
                        currentDay.getLessonList().add(readLesson(parser));
                    }
                    break;
            }

            eventType = parser.next();
        }
        return currentDay;
    }

    private Lesson readLesson(XmlPullParser parser) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "lesson");

        int eventType = parser.getEventType();
        Lesson currentLesson = new Lesson();

        while (eventType != XmlPullParser.END_TAG) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "beginTime":
                            currentLesson.setBegin(parser.nextText());
                            break;
                        case "endTime":
                            currentLesson.setEnd(parser.nextText());
                            break;
                        case "name":
                            currentLesson.setName(parser.nextText());
                            break;
                        case "classroom":
                            currentLesson.setClassroom(parser.nextText());
                            break;
                        case "teacher":
                            currentLesson.setTeacher(parser.nextText());
                            break;
                        case "type":
                            currentLesson.setType(parser.nextText());
                            break;
                        case "type2":
                            currentLesson.setType2(parser.nextText());
                            break;
                    }
                    break;
            }

            eventType = parser.next();
        }
        return currentLesson;
    }

    public static String createXMLFile() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);

        xmlSerializer.startTag("", "timetable");

        for (Day day: dayList) {
            xmlSerializer.startTag("", day.getName());

            for (Lesson lesson: day.getLessonList()) {
                xmlSerializer.startTag("", "lesson");

                xmlSerializer.startTag("", "beginTime");
                xmlSerializer.text(lesson.getBegin());
                xmlSerializer.endTag("", "beginTime");

                xmlSerializer.startTag("", "endTime");
                xmlSerializer.text(lesson.getEnd());
                xmlSerializer.endTag("", "endTime");

                xmlSerializer.startTag("", "name");
                xmlSerializer.text(lesson.getName());
                xmlSerializer.endTag("", "name");

                xmlSerializer.startTag("", "classroom");
                xmlSerializer.text(lesson.getClassroom());
                xmlSerializer.endTag("", "classroom");

                xmlSerializer.startTag("", "teacher");
                xmlSerializer.text(lesson.getTeacher());
                xmlSerializer.endTag("", "teacher");

                xmlSerializer.startTag("", "type");
                xmlSerializer.text(lesson.getType());
                xmlSerializer.endTag("", "type");

                xmlSerializer.startTag("", "type2");
                xmlSerializer.text(lesson.getType2());
                xmlSerializer.endTag("", "type2");

                xmlSerializer.endTag("", "lesson");
            }



            xmlSerializer.endTag("", day.getName());
        }


        xmlSerializer.endTag("", "timetable");

        xmlSerializer.endDocument();
        return writer.toString();
    }

    private Settings parseXMLSettings(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        Settings newSettings = null;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    newSettings = new Settings();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if (newSettings != null) {
                        String visible = null;
                        switch (name) {
                            case "timatable":
                                newSettings.setCurrentTimetable(parser.nextText());
                                break;

                            case "visibleMonday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[0] = visible.equals("true");
                                break;

                            case "visibleTuesday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[1] = visible.equals("true");
                                break;

                            case "visibleWednesday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[2] = visible.equals("true");
                                break;

                            case "visibleThursday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[3] = visible.equals("true");
                                break;

                            case "visibleFriday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[4] = visible.equals("true");
                                break;

                            case "visibleSaturday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[5] = visible.equals("true");
                                break;

                            case "visibleSunday":
                                visible = parser.nextText();
                                newSettings.getVisibleDays()[6] = visible.equals("true");
                                break;
                        }
                    }

                    break;
                case XmlPullParser.END_TAG:

            }
            eventType = parser.next();
        }

        return newSettings;
    }
}
