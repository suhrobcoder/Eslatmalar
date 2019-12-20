package uz.suhrob.eslatmalar.models;

import android.util.Log;

/**
 * Created by User on 13.12.2019.
 */

public class Time {

    private int hour, minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(String s) {
        String []s1 = s.split(":");
        this.hour = Integer.parseInt(s1[0]);
        this.minute = Integer.parseInt(s1[1]);
        Log.d("test", hour+"  "+minute);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }
}
