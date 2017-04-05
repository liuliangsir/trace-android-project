package com.trace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.trace.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangpanyu on 17/2/28.
 */

public class AtySelectTime extends Activity {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_select_time);
        datePicker = (DatePicker)  findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        try {
            String completeTimeString = getIntent().getStringExtra(
                    AtyEditNote.COMPLETE_TIME);
            if (completeTimeString != null && !completeTimeString.trim().equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date date = sdf.parse(completeTimeString);
                int hour = date.getHours();
                int minutes = date.getMinutes();
                Log.i("dateViewer", "timeHour:" + hour + " timeMinutes:"
                        + minutes);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minutes);
            }

            button = (Button) findViewById(R.id.btnSelected);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(AtyEditNote.COMPLETE_TIME, getCompleteTime());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

        } catch (java.text.ParseException e) {
            Log.i("dateViewer", "error:" + e.getMessage(), e);
        }
    }



    private String getCompleteTime() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        Log.i("dateViewer", "year" + year + " month:" + month + " date :"
                + date + " hour:" + hour + " minutes:" + minute);
        Date completeTime = new Date(year-1900, month, date, hour, minute);
        return new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss").format(completeTime);
    }
}
