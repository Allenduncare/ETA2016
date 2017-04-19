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
    public static final int DATABASE_VERSION = 6;
    public static final String TABLE_TRIP = "trip";
    public static final String trip_id = "_id";
    public static final String id = "id";
    public static final String location = "location";
    public static final String datetime = "datetime";
    public static final String longitude = "longitude";
    public static final String latitude = "latitude";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_TRIP + "("
                        + trip_id + " integer primary key autoincrement, "
                        + id + " long, "
                        + location + " text,"
                        + longitude + " double,"
                        + latitude + " double, "
                        + datetime + " long)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TripDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        onCreate(db);
    }

    public long insertTripId(long trip_id,Trip trip) {
        ContentValues cv = new ContentValues();
        cv.put(id,trip_id);
        cv.put(location,trip.getDestination());
        cv.put(longitude,trip.getLongitude());
        cv.put(latitude,trip.getLatitude());
        cv.put(datetime,trip.getDateTime());
        // return id of new trip
        return getWritableDatabase().insert(TABLE_TRIP, null, cv);
    }

    public List<Trip> getAllTripsID() {
        List<Trip> tripList = new ArrayList<Trip>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);
        // loop through all query results
        for (cursor.moveToFirst(); !cursor.isAfterLast();
             cursor.moveToNext()) {
            Long trip_id = cursor.getLong(1);
            String destination = cursor.getString(2);
            Double longitude = cursor.getDouble(3);
            Double latitude = cursor.getDouble(4);
            Long datetime = cursor.getLong(5);
            Trip trip = new Trip(trip_id,destination,longitude,latitude,datetime);
            tripList.add(trip);
        }
        return tripList;
    }

}
