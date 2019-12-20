package uz.suhrob.eslatmalar.models;

public class Event {

    private int id;
    private EventType type;
    private Frequency frequency;
    private String name;
    private String content;
    Date date;
    private Time time;
    private boolean isActive;

    public Event() {
    }

    public Event(int id, EventType type, Frequency frequency, String name, String content, Date date, Time time, boolean isActive) {
        this.id = id;
        this.type = type;
        this.frequency = frequency;
        this.name = name;
        this.content = content;
        this.date = date;
        this.time = time;
        this.isActive = isActive;
    }

    public Event(EventType type, Frequency frequency, String name, String content, Date date, Time time, boolean isActive) {
        this.type = type;
        this.frequency = frequency;
        this.name = name;
        this.content = content;
        this.date = date;
        this.time = time;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type.name();
    }

    public void setType(String type) {
        this.type = EventType.valueOf(type);
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency.toString();
    }

    public void setFrequency(String frequency1) {
        this.frequency.fromString(frequency1);
    }

    public void setFrequency() {
        this.frequency = new Frequency();
        for (int i=1; i<8; i++) {
            this.frequency.add(i);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String s) {
        this.date = new Date(s);
    }

    public String getTime() {
        return time.toString();
    }

    public void setTime(String s) {
        this.time = new Time(s);
    }

    public void setTime(Time t) {
        this.time = t;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
