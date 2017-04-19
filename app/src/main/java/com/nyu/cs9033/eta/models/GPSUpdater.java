package com.nyu.cs9033.eta.models;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Allen on 5/1/2016.
 */
public class GPSUpdater extends Service implements LocationListener {
    private LocationManager locationManager;
    private String provider;
    Trip trip;
    private static String url = "http://cs9033-homework.appspot.com/";

    public GPSUpdater(Trip newTrip) {
        trip = newTrip;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
      if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
           Toast.makeText(this,"Could not get location!",Toast.LENGTH_LONG).show();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng =  location.getLongitude();
        if(lat == trip.getLatitude() && lng == trip.getLongitude())
        {
        Toast.makeText(this,"You have arrived at your destination!",Toast.LENGTH_LONG).show();

        }
        long datetime = System.currentTimeMillis();
        String[] params = {"UPDATE_LOCATION",String.valueOf(lat),String.valueOf(lng),String.valueOf(datetime)};
        ServerRequest wt = new ServerRequest();
        wt.execute(params);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
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
                    jsonObject.put("latitude", args[1]);
                    jsonObject.put("longitude", args[2]);
                    jsonObject.put("datetime", args[3]);
                } catch (Exception e) {
                    Log.e("Error!", e.toString());
                    e.printStackTrace();
                }
                JSONObject json = WebTalker.makeHttpRequest(url, "POST", jsonObject);

                Log.d("JSON",
                        "JSON Response in GPSUpdater :"
                                + json.toString());

            } else {
                Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_LONG);
                status = "Failed to send update";
            }
    return status;
        }
        protected void onPostExecute(String text) {
        }
    }}
