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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TripDatabaseHelper helper = new TripDatabaseHelper(this);
        setContentView(R.layout.triplist);
        ListView view = (ListView) findViewById(R.id.listView);
        final List<Trip> trips = helper.getAllTrips();
        List<String> tripList = new ArrayList<String>();
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
                Intent intent =new Intent(view.getContext(),ViewTripActivity.class);
                intent.putExtra("Trip",trips.get(i));
                startActivity(intent);
                finish();
            }
        });


    }
}
