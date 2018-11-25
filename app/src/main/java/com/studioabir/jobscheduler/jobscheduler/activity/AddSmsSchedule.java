package com.studioabir.jobscheduler.jobscheduler.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.studioabir.jobscheduler.jobscheduler.MyBroadCastReceiver;
import com.studioabir.jobscheduler.jobscheduler.R;
import com.studioabir.jobscheduler.jobscheduler.dbhelper.SmsDatabaseHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.util.Calendar;

public class AddSmsSchedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Initialization
    EditText EDITPHONENO;
    EditText EDITMESSAGE;
    TextView TEXTVIEWDATE;
    TextView TEXTVIEWTIME;
    Button BTNSETSCHEDULE;
    Calendar calendar;
    private static final int PERMISSION_REQUEST_TO_SEND_SMS = 101;
    int mMinute,mHour,mDay,mMonth,mYear;
    SmsDatabaseHelper smsDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sms_schedule);

        // Initialize Object
        EDITPHONENO = findViewById(R.id.editTextToRecipient);
        EDITMESSAGE = findViewById(R.id.editTextMessage);
        TEXTVIEWDATE = findViewById(R.id.textViewDate);
        TEXTVIEWTIME = findViewById(R.id.textViewTime);
        BTNSETSCHEDULE = findViewById(R.id.btnSetSchedule);
        smsDatabaseHelper = new SmsDatabaseHelper(this);
        calendar = Calendar.getInstance();

        // SET SCHEDULE FOR SMS BUTTON
        BTNSETSCHEDULE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_TO_SEND_SMS);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }
                else
                {
                    // if device under API 22 where no to give runtimepermission
                    setSmsSchedule();
                }


            }
        });


    }
    ///////////////////////////END OF ON CREARTE////////////////////////////////////

    // Override this callback method if user gives permission then what to do
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_TO_SEND_SMS)
        {
            // permission granted
            setSmsSchedule();
        }
        else
        {
            Toast.makeText(this,"Until You Grand Permission, We can give you permission to set schedule", Toast.LENGTH_LONG).show();
        }

    }

    private void setSmsSchedule()
    {
        String ContactNo = EDITPHONENO.getText().toString();
        String MessageText = EDITMESSAGE.getText().toString();

        /*
        * Due to we have to send information to
        * our broadcastreceiver class. For That we use here Bundle
        * */
        Bundle bundle = new Bundle();
        bundle.putString("number", ContactNo);
        bundle.putString("messege", MessageText);

        // Pass bundle to Broadcast receiver with action uri of a userdefine string
        Intent intent = new Intent(this, MyBroadCastReceiver.class);
        intent.putExtras(bundle);
        String actionURI = "com.scheduler.action.SMS_SEND";
        intent.setAction(actionURI);

        // make a request code by system for use in pending intent set it a simple string
        int _id = (int) System.currentTimeMillis();

        // set it to calender object finally......YAHOOOOOOO
        calendar.set(mYear,mMonth,mDay,mHour,mMinute);

        // Initialize Pending intent for pending schedule time to work
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,_id,intent,0);

        // iNITIALIZE ALARM SERVICE FOR USE ALAMR MANAGER
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        // if pending intent set with time & date then show a toast
        Toast.makeText(getApplicationContext(), "Scheduled Added", Toast.LENGTH_LONG).show();

        // Added Schedule all data to SQLITE DATABASE
        // Call addSms Method from smsdatabasehelper class for save data
        if(smsDatabaseHelper.addSms(_id,ContactNo,MessageText,TEXTVIEWTIME.getText().toString(),TEXTVIEWDATE.getText().toString(),(int) calendar.getTimeInMillis()))
        {
            Toast.makeText(this, "Scheduled Sms Information Added to List", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, SmsSchedulerActivity.class));
        }
        else
        {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;

// initialy calender date will be zero
        calendar.add(Calendar.DATE,0);

// Create a array with name of month shortly to show in textdate with replace of original month
        final String[] MONTHS = {"Jan", "Feb", "Mar",
                "Apr", "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec"};

// Get Month from Array helped by method month parameter & set to string mon
        String mon = MONTHS[monthOfYear];

// Finally get date in full format & set it to a String Nicely
        String selectDate = dayOfMonth + " " + mon + " " + year;

// Set full date in textdate by help of Spannable StRING
        SpannableString ss1 = new SpannableString(selectDate);
        ss1.setSpan(new RelativeSizeSpan(1.5f),0,2,0); // set
        TEXTVIEWDATE.setText(ss1);

// Set day, Month & year
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, dayOfMonth);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        // invoked object with method parameter
        mHour = hourOfDay;
        mMinute = minute;

// Set Hour, Minute & Second
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR, hourOfDay);

// FIRST ONE is for SET PM & SECOND ONE IS FOR SET AM
        calendar.set(Calendar.AM_PM, Calendar.AM);

// Initialize String
        String str;

// Control String for text AM & PM
// IF AM SET STRING TO AM
        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
        {
            str = "AM";
        }
// ELSE PM
        else
        {
            str = "PM";
        }

// Initialize String
        String hours;

// Below checker is for check hour is in one digit or not
// IF grater than 9 meance it has already 2 digit type of hour
        if (calendar.get(Calendar.HOUR) > 9)
        {
            hours = String.valueOf(calendar.get(Calendar.HOUR));

        }
// else hour like one digit
        else
        {
            hours = "0" + String.valueOf(calendar.get(Calendar.HOUR));
        }

// Initialize String
        String minutes;

// if our selected time has minute with on digit like 9...0
// here grater then 9 meance it has already 2digit type of minit
        if (minute > 9)
        {
            minutes = String.valueOf(minute);
        }
        else
        {
            minutes = "0" + String.valueOf(minute);
        }

// Maintain Hour for 12/24
        if (hours.equalsIgnoreCase("00"))
        {
            // it meance time will not be like 13PM....i will be like 1PM...
            hours = "12";
        }

// Finally get SELECTED TIME in full format & set it to a String Nicely
        String selectedTime = hours + ":" + minutes
                + "  " + str;

// Set full Time in texttime by help of Spannable StRING
        SpannableString ss2 = new SpannableString(selectedTime);
        ss2.setSpan(new RelativeSizeSpan(1.5f), 0, 5, 0);
        TEXTVIEWTIME.setText(ss2);

// Set Hour, Minute & Second
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR, hourOfDay);


    }




    ///   ----- click to open Datepicker
    public void dateeeee(View view) {

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, mYear, mMonth, mDay);
        datePickerDialog.show(getFragmentManager(), "datePickerDialog");

    }

    ///   ----- click to open timepicker
    public void timessss(View view) {

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, mHour, mMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");


    }
}
