package com.studioabir.jobscheduler.jobscheduler.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.studioabir.jobscheduler.jobscheduler.model.Sms;

import java.util.ArrayList;
import java.util.List;


public class SmsDatabaseHelper extends SQLiteOpenHelper {

    private static final String SMS_TABLE_NAME = "sms_schedules";


    // Default Constructor
    public SmsDatabaseHelper(Context context) {
        super(context, SMS_TABLE_NAME, null, 1);
    }

    // Default Method by SQLiteOpenHelper Class for Create DATABASE TABLE
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE IF NOT EXISTS "+ SMS_TABLE_NAME
                + "(ID INTEGER PRIMARY KEY,"
                + "number VARCHAR,"
                + "msg VARCHAR,"
                + "time VARCHAR,"
                + "date VARCHAR,"
                + "sms_mili INTEGER);";

        db.execSQL(query);
    }

    // Default Method by SQLiteOpenHelper Class for Update DATABASE TABLE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ SMS_TABLE_NAME);
        onCreate(db);
    }

    // Create Method For Add Sms data into Database Table
    public boolean addSms(int id, String number, String message, String time, String date, int sms_milli)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("number", number);
        contentValues.put("msg", message);
        contentValues.put("time", time);
        contentValues.put("date", date);
        contentValues.put("sms_mili", sms_milli);

        long result = db.insert(SMS_TABLE_NAME,null,contentValues);

        return result != -1;
    }

    // method for fetch data from sqlite table
    public List<Sms> getAll()
    {
        List<Sms> smsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + SMS_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String id = String.valueOf(cursor.getInt(0));
            String number = cursor.getString(1);
            String message = cursor.getString(2);
            String time = cursor.getString(3);
            String date = cursor.getString(4);
            int sms_milli = cursor.getInt(5);

            Sms sms = new Sms(id,number,message,time,date,sms_milli);
            smsList.add(sms);
        }

        cursor.close();
        return smsList;
    }

    public boolean deleteById(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(SMS_TABLE_NAME, "ID=?",new String[]{id})>0;
    }




}
