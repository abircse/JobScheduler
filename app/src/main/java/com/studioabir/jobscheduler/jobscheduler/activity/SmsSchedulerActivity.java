package com.studioabir.jobscheduler.jobscheduler.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.studioabir.jobscheduler.jobscheduler.R;
import com.studioabir.jobscheduler.jobscheduler.adapter.SmsArrayAdapter;
import com.studioabir.jobscheduler.jobscheduler.dbhelper.SmsDatabaseHelper;
import com.studioabir.jobscheduler.jobscheduler.model.Sms;

import java.util.ArrayList;
import java.util.List;

public class SmsSchedulerActivity extends AppCompatActivity {

    private ListView LV;
    private TextView TV;

    private List<Sms> smsArrayList = new ArrayList<Sms>();
    private SmsArrayAdapter smsArrayAdapter;
    SmsDatabaseHelper databaseHelper;

    int position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_scheduler);

        LV = findViewById(R.id.smsListView);
        TV = findViewById(R.id.textViewNoSchedule);

        // initialize database helper class
        databaseHelper = new SmsDatabaseHelper(this);
        // THIS is a build in method with listview Object
        registerForContextMenu(LV);
        // Call fetch method
        fetchSms();
    }

    // method for get sms store data & set it to list view if saved before
    private void fetchSms()
    {
        smsArrayList = databaseHelper.getAll();
        if (smsArrayList.size() > 0)
        {
            LV.setVisibility(View.VISIBLE);
            TV.setVisibility(View.GONE);
        }

            smsArrayAdapter = new SmsArrayAdapter(this, smsArrayList);
            LV.setAdapter(smsArrayAdapter);


    }

    // Just Override onCreateContextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderTitle("What you Want To do");
        inflater.inflate(R.menu.schedulemenu, menu);
    }

    // just override onContextItemSelected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_edit:
                Intent intent = new Intent(this, UpdateSmsSchedule.class);
                intent.putExtra("sms", smsArrayList.get(position));
                startActivity(intent);
                return true;

            case R.id.menu_delete:
                deleteSchedule();
                return true;

            default:
                return super.onContextItemSelected(item);
        }


    }

    // method for delete schedule from list
    private void deleteSchedule()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ara You Sure to Delete This Schedule?");
        builder.setTitle("DELETE ALERT");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);

        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (databaseHelper.deleteById(smsArrayList.get(position).getId()))
                {
                    Toast.makeText(getApplicationContext(),"SMS Schedule has been Deleted",Toast.LENGTH_SHORT).show();
                    smsArrayList.remove(position);
                    smsArrayAdapter.notifyDataSetChanged();
                }

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }


    public void GOTOADDSMS(View view) {

        startActivity(new Intent(getApplicationContext(),AddSmsSchedule.class));
    }
}
