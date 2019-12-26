package uz.suhrob.eslatmalar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Date;
import uz.suhrob.eslatmalar.models.Event;
import uz.suhrob.eslatmalar.models.EventType;
import uz.suhrob.eslatmalar.models.Frequency;
import uz.suhrob.eslatmalar.models.Notify;
import uz.suhrob.eslatmalar.models.Time;

public class AddEventActivity extends AppCompatActivity implements ActionBottomSheetDialogFragment.ItemClickListener {

    TextInputEditText eventTitleText, eventContentText;
    TextView eventDateText, eventTimeText, repeatType;
    Button saveButton, weekButtons[];
    ConstraintLayout eventType, eventTimeLay, eventDateLay;
    LinearLayout weekLayout;

    String[] months = {"yanvar", "fevral", "mart", "aprel", "may", "iyun", "iyul", "avgust", "sentabr", "oktabr", "noyabr", "dekabr"};
    boolean checkedDays[] = {false, false, false, false, false, false, false};

    Event event;
    Calendar setTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setTitle(R.string.add_note);

        eventTitleText = findViewById(R.id.event_title);
        eventContentText = findViewById(R.id.event_content);
        eventTimeText = findViewById(R.id.event_time);
        eventDateText = findViewById(R.id.event_date);

        eventType = findViewById(R.id.event_type);
        eventTimeLay = findViewById(R.id.event_time_l);
        eventDateLay = findViewById(R.id.event_date_l);
        repeatType = findViewById(R.id.repeat_type);

        saveButton = findViewById(R.id.save_button);

        event = new Event();
        event.setActive(true);
        event.setType(EventType.ONCE);

        weekLayout = findViewById(R.id.week_btns_layout);
        weekButtons = new Button[7];
        weekButtons[0] = findViewById(R.id.du_week_btn);
        weekButtons[1] = findViewById(R.id.se_week_btn);
        weekButtons[2] = findViewById(R.id.ch_week_btn);
        weekButtons[3] = findViewById(R.id.pa_week_btn);
        weekButtons[4] = findViewById(R.id.ju_week_btn);
        weekButtons[5] = findViewById(R.id.sh_week_btn);
        weekButtons[6] = findViewById(R.id.ya_week_btn);

        setTime = Calendar.getInstance();

        final Calendar calendar = Calendar.getInstance();
        event.setTime(new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        eventTimeText.setText(String.format(Locale.getDefault(), "Eslatish vaqti: %d:%d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

        eventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
        eventTimeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(calendar);
            }
        });

        eventDateLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(calendar);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = eventTitleText.getText().toString().trim();
                if (title.isEmpty()) {
                    eventTitleText.setError("Eslatma nomi kiritilmagan");
                    return;
                }
                event.setName(title);
                event.setContent(eventContentText.getText().toString().trim());
                Frequency frequency = new Frequency();
                if (event.getType().equals(EventType.WEEKLY.name())) {
                    for (int i=0; i<7; i++) {
                        if (checkedDays[i]) {
                            frequency.add(i+1);
                        }
                    }
                    event.setFrequency(frequency);
                } else {
                    event.setFrequency();
                }
                long event_id = new EventDBHelper(getApplicationContext()).insertData(event);
                if (event_id > 0) {
                    EventAlarm alarm = new EventAlarm();
                    if (event.getType().equals(EventType.WEEKLY.name())) {
                        alarm.setAlarm(getApplicationContext(), whenNextAlarm(event), event.getName(), event.getContent(), (int)new EventDBHelper(getApplicationContext()).insertNotify(new Notify((int)event_id)));
                    } else {
                        if (setTime.getTimeInMillis() < System.currentTimeMillis()) {
                            setTime.setTimeInMillis(setTime.getTimeInMillis() + 86400*1000);
                        }
                        alarm.setAlarm(getApplicationContext(), setTime, event.getName(), event.getContent(), (int)new EventDBHelper(getApplicationContext()).insertNotify(new Notify((int)event_id)));
                    }
                    insertedDialog();
                }
            }
        });

        for (int i=0; i<7; i++) {
            final int j = i;
            weekButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    weekBtnClicked(j);
                }
            });
        }

    }

    public void showBottomSheet() {
        ActionBottomSheetDialogFragment dialogFragment = ActionBottomSheetDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), ActionBottomSheetDialogFragment.TAG);
    }

    @Override
    public void onItemClick(String item) {
        if (item.equals(getResources().getString(R.string.everyday))) {
            event.setType(EventType.EVERYDAY);
            weekLayout.setVisibility(View.GONE);
            for (int i=0; i<7; i++) {
                if (checkedDays[i]) {
                    weekBtnClicked(i);
                }
            }
        } else if (item.equals(getResources().getString(R.string.week))) {
            event.setType(EventType.WEEKLY);
            weekLayout.setVisibility(View.VISIBLE);
        } else if (item.equals(getResources().getString(R.string.once))) {
            event.setType(EventType.ONCE);
            weekLayout.setVisibility(View.GONE);
            for (int i=0; i<7; i++) {
                if (checkedDays[i]) {
                    weekBtnClicked(i);
                }
            }
        }
        repeatType.setText(getResources().getString(R.string.takrorlanish_turi1) + ": " + item);
    }

    public void showTimePickerDialog(Calendar calendar) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                eventTimeText.setText(getResources().getString(R.string.event_time) + ": " + (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute));
                event.setTime(new Time(hour, minute));
                setTime.set(Calendar.HOUR_OF_DAY, hour);
                setTime.set(Calendar.MINUTE, minute);
                Log.d("TimeDialog", ""+hour + " " + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void showDatePickerDialog(final Calendar calendar) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                eventDateText.setText(getResources().getString(R.string.event_date) + ": " + i2 + "-" + months[i1] + " " + i + "-yil");
                event.setDate(new Date(i, i1, i2));
                setTime.set(Calendar.YEAR, i);
                setTime.set(Calendar.MONTH, i1);
                setTime.set(Calendar.DAY_OF_MONTH, i2);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void insertedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getResources().getString(R.string.app_name));
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage("Yangi eslatma qo`shildi");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialogBuilder.create().show();
    }

    public void weekBtnClicked(int j) {
        if (checkedDays[j]) {
            weekButtons[j].setBackgroundColor(Color.WHITE);
            checkedDays[j] = false;
        } else {
            weekButtons[j].setBackgroundColor(Color.parseColor("#dfdfdf"));
            checkedDays[j] = true;
        }
    }

    public static Calendar whenNextAlarm(Event event) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, event.getTime1().getHour());
        calendar.set(Calendar.MINUTE, event.getTime1().getMinute());
        calendar.set(Calendar.SECOND, 0);
        Log.d("TimeDialog", ""+calendar.get(Calendar.HOUR_OF_DAY));
        while (!isPossibleDate(calendar, event.getFrequency1())) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400*1000);
        }
        return calendar;
    }

    public static boolean isPossibleDate(Calendar calendar, Frequency frequency) {
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) return false;
        for (int x: frequency.getAll()) {
            int x1 = (x+1) % 7;
            if (x1 == 0) x1 = 7;
            if (calendar.get(Calendar.DAY_OF_WEEK) == x1) return true;
        }
        return false;
    }

}
