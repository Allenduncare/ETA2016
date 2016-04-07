package com.nyu.cs9033.eta.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen on 4/3/2016.
 */
public class TripDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "trips";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_TRIP = "trip";
    public static final String trip_id = "_id";
    public static final String id = "id";
    public static final String destination_column = "destination";
    public static final String hour_column = "hour";
    public static final String minute_column = "minute";
    public static final String datetime_column = "datetime";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String name_column = "name";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_TRIP + "("
                + trip_id + " integer primary key autoincrement, "
                + destination_column + " text, "
                + datetime_column + " long)");
        database.execSQL("create table " + TABLE_CONTACTS + "("
                        + trip_id + " integer primary key autoincrement, "
                        +id + " long, "
                        +hour_column + " integer, "
                        +minute_column+" integer, "
                        + name_column + " text, "
                        + destination_column + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TripDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        onCreate(db);
    }

    public long insertTrip(Trip trip) {
        ContentValues cv = new ContentValues();
        cv.put(destination_column, trip.getDestination());
        cv.put(datetime_column,trip.getDateTime());
        // return id of new trip
        return getWritableDatabase().insert(TABLE_TRIP, null, cv);
    }

    public long insertPerson(Person person,long tid) {
        ContentValues cv = new ContentValues();
        cv.put(id,tid);
        cv.put(name_column, person.getName());
        cv.put(hour_column, person.getETA_hour());
        cv.put(minute_column, person.getETA_minute());
        cv.put(destination_column, person.getCurrentLocation());
        // return id of new trip
        return getWritableDatabase().insert(TABLE_CONTACTS, null, cv);
    }

    public List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<Trip>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast();
             cursor.moveToNext()) {
            Person[] persons = getAllPeople(cursor.getInt(0));
            String destination = cursor.getString(1);
            long datetime = cursor.getLong(2);
            Trip trip = new Trip(destination, persons,datetime);
            tripList.add(trip);
        }
        return tripList;
    }

    public Person[] getAllPeople(long id) {
        Person[] persons = new Person[100];
        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CONTACTS + " where id = " + id, null);
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast();
             cursor.moveToNext()) {
            String name = cursor.getString(4);
            int hour = cursor.getInt(2);
            int minute = cursor.getInt(3);
            String destination = cursor.getString(5);
            Person person = new Person(name, hour, minute, destination);
            persons[i] = person;
            i++;
        }
        return persons;
    }
}
