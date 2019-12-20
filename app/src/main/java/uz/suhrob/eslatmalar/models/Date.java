package uz.suhrob.eslatmalar.models;

/**
 * Created by User on 19.12.2019.
 */

public class Date {

    private int day, month, year;

    public Date(String date) {
        String date1[] = date.split("-");
        this.day = Integer.parseInt(date1[0]);
        this.month = Integer.parseInt(date1[1]);
        this.year = Integer.parseInt(date1[2]);
    }

    public Date(int day, int month, int year) {

        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public String toString() {
        return day + "-" + month + "-" + year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
