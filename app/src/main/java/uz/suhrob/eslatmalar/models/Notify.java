package uz.suhrob.eslatmalar.models;

/**
 * Created by User on 19.12.2019.
 */

public class Notify {

    private int id;
    private int eventId;

    public Notify() {
    }

    public Notify(int eventId) {

        this.eventId = eventId;
    }

    public int getId() {
        return id;
    }

    public Notify(int id, int eventId) {
        this.id = id;
        this.eventId = eventId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
