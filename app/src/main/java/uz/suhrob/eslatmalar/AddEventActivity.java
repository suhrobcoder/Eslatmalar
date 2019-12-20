package uz.suhrob.eslatmalar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Date;
import uz.suhrob.eslatmalar.models.Event;
import uz.suhrob.eslatmalar.models.EventType;
import uz.suhrob.eslatmalar.models.Time;

public class AddEventActivity extends AppCompatActivity implements ActionBottomSheetDialogFragment.ItemClickListener {

    TextInputEditText eventTitleText, eventContentText;
    TextView eventDateText, eventTimeText, repeatType;
    Button saveButton;
    ConstraintLayout eventType, eventTimeLay, eventDateLay;

    String[] months = {"Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun", "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"};

    Event event;

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
                event.setFrequency();
                if (new EventDBHelper(getApplicationContext()).insertData(event)) {
                    insertedDialog();
                }
                EventAlarm alarm = new EventAlarm();
                alarm.setAlarm(getApplicationContext(), Calendar.getInstance(), event.getName(), event.getContent(), 1);
            }
        });

    }

    public void showBottomSheet() {
        ActionBottomSheetDialogFragment dialogFragment = ActionBottomSheetDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), ActionBottomSheetDialogFragment.TAG);
    }

    @Override
    public void onItemClick(String item) {
        if (item.equals(getResources().getString(R.string.everyday))) {
            event.setType(EventType.EVERYDAY);
        } else if (item.equals(getResources().getString(R.string.week))) {
            event.setType(EventType.WEEKLY);
        } else if (item.equals(getResources().getString(R.string.once))) {
            event.setType(EventType.ONCE);
        }
        repeatType.setText(getResources().getString(R.string.takrorlanish_turi1) + ": " + item);
    }

    public void showTimePickerDialog(Calendar calendar) {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                eventTimeText.setText(getResources().getString(R.string.event_time) + ": " + (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute));
                event.setTime(new Time(hour, minute));
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void showDatePickerDialog(Calendar calendar) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // TODO: korish kerak
                eventDateText.setText("Eslatish sanasi: " + i1 + "-" + i1 + "-" + i);
                event.setDate(new Date(i, i1, i2));
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

}
