package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripHistoryActivity extends Activity {

    ArrayAdapter<String> arrayAdapter;
    int REQUEST_CODE = 1;
    private static String KEY_TRIP = "Trip";
    List<Trip> trips = new ArrayList<Trip>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TripDatabaseHelper helper = new TripDatabaseHelper(this);
        setContentView(R.layout.triplist);
        ListView view = (ListView) findViewById(R.id.listView);
        List<String>tripList = new ArrayList<String>();
        trips = helper.getAllTripsID();
        for(Trip trip: trips)
        {
            Date d = new Date(trip.getDateTime());
            String s = d.toString();
            String tripString = trip.getDestination() + "@ " +s;
            tripList.add(tripString);
        }
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tripList);
        view.setAdapter(arrayAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ViewTripActivity.class);
                intent.putExtra("Trip", trips.get(i));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });



    }
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Trip trip = (Trip)b.getParcelable(KEY_TRIP);
            Intent newData = new Intent();
            data.putExtra(KEY_TRIP,trip);
            setResult(RESULT_OK, data);
            finish();
        }
    }}
