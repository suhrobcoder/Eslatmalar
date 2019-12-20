package uz.suhrob.eslatmalar.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.12.2019.
 */

public class Frequency {

    private List<Integer> list;

    public Frequency() {
        list = new ArrayList<>();
    }

    public Frequency(String s) {
        list = new ArrayList<>();
        fromString(s);
    }

    public void add(int x) {
        if (!list.contains(x)) list.add(x);
    }

    public void remove(int x) {
        list.remove(Integer.valueOf(x));
    }

    public List<Integer> getAll() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i: list) {
            s.append(i).append(":");
        }
        s.deleteCharAt(s.length()-1);
        return s.toString();
    }

    public void fromString(String s) {
        String []arr = s.split(":");
        this.list.clear();
        for (String s1: arr) {
            list.add(Integer.parseInt(s1));
        }
    }
}
