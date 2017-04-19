package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;
import com.nyu.cs9033.eta.models.WebTalker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CreateTripActivity extends Activity {
    Button button;
    Button button2;
    Button button3;
    private static final String TAG = "CreateTripActivity";
    final Context context = this;
    TripDatabaseHelper db = new TripDatabaseHelper(this);
    ArrayList<String> apidata;
    String url = "http://cs9033-homework.appspot.com/";
    String Location;
    String result;
    Vector<String> contactnames = new Vector<String>();
    int minute;
    int hour;
    int REQUEST_CODE = 1;
    int REQUEST_CONTACT = 1;
    int day;
    int year;
    int month;
    long datetime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.create_trip);
        super.onCreate(savedInstanceState);
        button = (Button) findViewById(R.id.button3);
        button2 = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Boolean created = createTrip();
                if( created == true)
                {saveTrip();}}
        });
        Button b = (Button) findViewById(R.id.contacts);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }

        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTrip(); }
        });
        Button apiButton = (Button)findViewById(R.id.apiButton);
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { String intent = "";
                TextView view1 = (TextView)findViewById(R.id.location);
                TextView view2 = (TextView)findViewById(R.id.locationType);
                intent = intent + view1.getText()+"::"+view2.getText();
                Uri search = Uri.parse("location://com.example.nyu.hw3api");
                Intent i = new Intent(Intent.ACTION_VIEW,search);
                i.putExtra( "searchVal", intent);
                PackageManager manager = getPackageManager();
                List<ResolveInfo> activities = manager.queryIntentActivities(i, 0);
                boolean isSafe = activities.size() > 0;
                if(isSafe)
                {
                    startActivityForResult(i,REQUEST_CODE);
                }
            }
        });
        Button button3 = (Button)findViewById(R.id.button6);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year = yearSelected;
                    month = monthOfYear;
                    day = dayOfMonth;
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        final Calendar c = Calendar.getInstance();
                return new DatePickerDialog(this, mDateSetListener, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * This method should be used to
     * instantiate a Trip model object.
     *
     * @return Trip_id as represented
     * by the View.
     */
    public boolean createTrip() {
        if( Location==null || contactnames.size()==0)
        {
            Toast.makeText(this,"Invalid Trip, Check trip details",Toast.LENGTH_LONG).show();

        }
        else{
            TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
            hour = tp.getCurrentHour();
            minute = tp.getCurrentMinute();
            String mon = Integer.toString(month);
            String sInt = Integer.toString(day);
            String sMin = Integer.toString(minute);
            String sHour = Integer.toString(hour);
            if (month < 10){mon = "0"+Integer.toString(month);}
            if (day < 10){sInt = "0"+Integer.toString(day);}
            if (hour < 10){sHour = "0"+Integer.toString(hour);}
            if (minute < 10){sMin = "0"+Integer.toString(minute);}
                Date mDate = new Date();
                mDate.setMinutes(minute);
                mDate.setHours(hour);
                mDate.setDate(day);
                mDate.setMonth(month);
                mDate.setYear(year - 1900);
                datetime = mDate.getTime();

            ConnectivityManager cnnmanager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cnnmanager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                String[] params = {"CREATE_TRIP",new JSONArray(apidata).toString(),String.valueOf(datetime),contactnames.toString()};
                ServerRequest wt = (ServerRequest) new ServerRequest().execute(params);
            }
            else
            {
                Toast.makeText(this,"Error",Toast.LENGTH_LONG);
            }
            return true;
        }

    return false;}


    /**
     * For HW2 you should treat this method as a
     * way of sending the Trip data back to the
     * main Activity.
     * <p/>
     * Note: If you call finish() here the Activity
     * will end and pass an Intent back to the
     * previous Activity using setResult().
     *
     * @return whether the Trip was successfully
     * saved.
     */
    public boolean saveTrip() {

        setContentView(R.layout.activity_main);
        finish();
            return true;
    }

    /**
     * This method should be used when a
     * user wants to cancel the creation of
     * a Trip.
     * <p/>
     * Note: You most likely want to call this
     * if your activity dies during the process
     * of a trip creation or if a cancel/back
     * button event occurs. Should return to
     * the previous activity without a result
     * using finish() and setResult().
     */
    public void cancelTrip() {
        setContentView(R.layout.activity_main);
        setResult(RESULT_CANCELED, new Intent());
        this.finish();
    }
    //Gets name form contacts
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
            String name = "";
            if(resultCode != Activity.RESULT_OK) return;

            Uri contactUri = data.getData();
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = this.getContentResolver().query(contactUri, queryFields, null, null, null);
            if(c.getCount() == 0) {
                c.close();
                return;
            }

            // Get first row (will be only row in most cases)
            c.moveToFirst();
            String person = c.getString(0);
            c.close();
            TextView view = (TextView)findViewById(R.id.listOfAttendees);
            String text = (String)view.getText();
            contactnames.add(person);
            view.setText(text+"\n"+person);
        }
        if( requestCode==REQUEST_CODE && resultCode == RESULT_FIRST_USER)
            {
                apidata = data.getExtras().getStringArrayList("retVal");
                Location = apidata.get(0);

            }


        }
    class ServerRequest extends AsyncTask<String, String, String> {
        /**
         * Creating user
         * */
        protected String doInBackground(String... args) {

            String status = "Failure";
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null & netInfo.isConnected()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("command", args[0]);
                    jsonObject.put("location", args[1]);
                    jsonObject.put("datetime", args[2]);
                    jsonObject.put("people", args[3]);
                } catch (Exception e) {
                    Log.e("Error!", e.toString());
                    e.printStackTrace();
                }
                JSONObject json = WebTalker.makeHttpRequest(url, "POST", jsonObject);

                Log.d("JSON",
                        "JSON Response in CreateTripActivity :"
                                + json.toString());

                // check for success tag
                try {
                    int response = json.getInt("response_code");

                    if (response == 0) {
                        long trip_id = json.getLong("trip_id");
                        Trip trip = new Trip(trip_id, apidata.get(0),Long.getLong(apidata.get(1)),Long.getLong(apidata.get(2)), datetime);
                        db.insertTripId(trip_id, trip);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
                    }
                    ;
                } catch (JSONException je) {
                    Log.e("ERROR", "Error in CreateNewTrip.doInBackground()"
                            + je.toString());
                }
                status = "Success";
            } else {
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG);
                status = "Failure";
            }
            return status;
        }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            // pDialog.dismiss();
        }

    }