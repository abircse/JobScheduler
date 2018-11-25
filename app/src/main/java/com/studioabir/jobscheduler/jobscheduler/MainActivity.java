package com.studioabir.jobscheduler.jobscheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.studioabir.jobscheduler.jobscheduler.activity.SmsSchedulerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void smsactivity(View view) {

        startActivity(new Intent(getApplicationContext(), SmsSchedulerActivity.class));
    }
}
