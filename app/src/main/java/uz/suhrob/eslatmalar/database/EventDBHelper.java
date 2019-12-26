package uz.suhrob.eslatmalar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uz.suhrob.eslatmalar.models.Date;
import uz.suhrob.eslatmalar.models.Event;
import uz.suhrob.eslatmalar.models.EventType;
import uz.suhrob.eslatmalar.models.Frequency;
import uz.suhrob.eslatmalar.models.Notify;
import uz.suhrob.eslatmalar.models.Time;

/**
 * Created by User on 15.12.2019.
 */

public class EventDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "eslatmalar";
    private static final String TABLE_NAME = "events";
    private static final String ID = "_id";
    private static final String EVENT_TYPE = "type";
    private static final String FREQUENCY = "frequency";
    private static final String NAME = "name";
    private static final String CONTENT = "content";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String IS_ACTIVE = "is_active";
    private static final String TABLE_NAME2 = "notify";
    private static final String EVENT_ID = "event_id";

    public EventDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        EVENT_TYPE + " TEXT, " + FREQUENCY + " TEXT, " + NAME + " TEXT, " + CONTENT + " TEXT, " + DATE + " TEXT, " + TIME + " TEXT, " +
        IS_ACTIVE + " INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        EVENT_ID + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }

    public long insertData(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENT_TYPE, event.getType());
        cv.put(FREQUENCY, event.getFrequency());
        cv.put(NAME, event.getName());
        cv.put(CONTENT, event.getContent());
        cv.put(TIME, event.getTime());
        cv.put(IS_ACTIVE, event.isActive() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, cv);
        Log.d("DBHelper", ""+id);
        db.close();
        return id;
    }

    public List<Event> getAll() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Event(cursor.getInt(cursor.getColumnIndex(ID)),
                        EventType.valueOf(cursor.getString(cursor.getColumnIndex(EVENT_TYPE))),
                        new Frequency(cursor.getString(cursor.getColumnIndex(FREQUENCY))),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTENT)),
                        new Date(1, 1, 2020),
                        new Time(cursor.getString(cursor.getColumnIndex(TIME))),
                        cursor.getInt(cursor.getColumnIndex(IS_ACTIVE))>0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Event getOne(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "=" + id, null);
        cursor.moveToFirst();
        Event event = new Event(cursor.getInt(cursor.getColumnIndex(ID)),
                EventType.valueOf(cursor.getString(cursor.getColumnIndex(EVENT_TYPE))),
                new Frequency(cursor.getString(cursor.getColumnIndex(FREQUENCY))),
                cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(CONTENT)),
                new Date(1,1,2020),
                new Time(cursor.getString(cursor.getColumnIndex(TIME))),
                cursor.getInt(cursor.getColumnIndex(IS_ACTIVE))>0);
        cursor.close();
        return event;
    }

    public void changeActive(int id, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Event event = getOne(id);
        cv.put(EVENT_TYPE, event.getType());
        cv.put(FREQUENCY, event.getFrequency());
        cv.put(NAME, event.getName());
        cv.put(CONTENT, event.getContent());
        cv.put(TIME, event.getTime());
        cv.put(IS_ACTIVE, active ? 1 : 0);
        db.close();
    }

    public long insertNotify(Notify notify) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENT_ID, notify.getEventId());
        long inserted_id = db.insert(TABLE_NAME2, null, cv);
        db.close();
        return inserted_id;
    }

    public Notify getNotify(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + EVENT_ID + " FROM " + TABLE_NAME2 + " WHERE " + ID + "=" + id, null);
        cursor.moveToFirst();
        Notify notify = new Notify(id, cursor.getInt(cursor.getColumnIndex(EVENT_ID)));
        cursor.close();
        return notify;
    }

    public int getNotifyIdWithEventId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ID + " FROM " + TABLE_NAME2 + " WHERE " + EVENT_ID + "=" + id, null);
        cursor.moveToFirst();
        int notifyId = cursor.getInt(cursor.getColumnIndex(ID));
        cursor.close();
        return notifyId;
    }

    public void deleteNotify(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2, ID + " = ?", new String[]{Integer.toString(id)});
    }
}
