package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.WebTalker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ViewTripActivity extends Activity {

    private static final String TAG = "ViewTripActivity";
    private static Trip trip;
    private static String KEY_TRIP = "Trip";
    private static String url = "http://cs9033-homework.appspot.com/";
    String displayText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trip);
        trip=getTrip(getIntent());

        if (trip != null){
            viewTrip(trip);
        }
        else{
            Toast.makeText(this,"No trips found",Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
        }
        Button activeButton = (Button)findViewById(R.id.activeButton);
        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(KEY_TRIP,trip);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        Button nullButton = (Button)findViewById(R.id.NullActive);
        nullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(KEY_TRIP, (Parcelable[]) null);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    /**
     * Create a Trip object via the recent trip that
     * was passed to TripViewer via an Intent.
     *
     * @param i The Intent that contains
     *          the most recent trip data.
     * @return The Trip that was most recently
     * passed to TripViewer, or null if there
     * is none.
     */
    public Trip getTrip(Intent i) {

        Bundle bundle = i.getExtras();
        Trip trip = null;
        if (bundle != null)
        {trip = bundle.getParcelable(KEY_TRIP);}

    return trip;
    }

    /**
     * Populate the View using a Trip model.
     *
     * @param trip The Trip model used to
     *             populate the View.
     */
    public void viewTrip(Trip trip) {
        String destination = trip.getDestination();
        Date d = new Date(trip.getDateTime());
        Long trip_id = trip.getTripID();
        ConnectivityManager cnnmanager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cnnmanager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            String[] params = {"TRIP_STATUS",trip_id.toString()};
            ServerRequest wt = (ServerRequest) new ServerRequest().execute(params);
            TextView view = (TextView)findViewById(R.id.textView2);
            try{displayText= wt.get();}
            catch(InterruptedException i){}
            catch(ExecutionException e){}
            view.setText(displayText);
        }

        else
        {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG);
        }
        Button button = (Button)findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
                finish();
            }
        });
    }
    class ServerRequest extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            String status = "Failure";
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null & netInfo.isConnected()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("command", args[0]);
                    jsonObject.put("trip_id", args[1]);
                } catch (Exception e) {
                    Log.e("Error!", e.toString());
                    e.printStackTrace();
                }
                JSONObject json = WebTalker.makeHttpRequest(url, "POST", jsonObject);

                Log.d("JSON",
                        "JSON Response in CreateTripActivity :"
                                + json.toString());

                try {
                        JSONArray distanceleft = json.getJSONArray("distance_left");
                        JSONArray timeleft = json.getJSONArray("time_left");
                        JSONArray people = json.getJSONArray("people");
                        String destination = trip.getDestination();
                        displayText= "Destination: " +destination + "\n";
                        for(int i = 0; i <distanceleft.length();i++)
                        {
                            displayText+= people.get(i) + " is "+ distanceleft.get(i) + " with an ETA of "+timeleft.get(i)+".\n";
                        }
                } catch (JSONException je) {
                    Log.e("ERROR", "Error in CreateNewTrip.doInBackground()"
                            + je.toString());
                }
                status = displayText;
            } else {
                Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_LONG);
                status = "Failed to get trip details from server";
            }
            return status;
        }
        protected void onPostExecute(String text) {
        }

    }
}
