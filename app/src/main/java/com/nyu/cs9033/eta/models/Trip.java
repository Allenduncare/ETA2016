package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {

    // Member fields should exist here, what else do you need for a trip?
    // Please add additional fields
    //Time format is YYYY/MM/DD/HH/MM/SS
    //Can be parsed using / delimiter
    private static Long Trip_ID;
    private String destination;
    private Double longitude;
    private Double latitude;
    private Long datetime;

    /**
     * Parcelable creator. Do not modify this function.
     */
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel p) {
            return new Trip(p);
        }
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    /**
     * Create a Trip model object from a Parcel. This
     * function is called via the Parcelable creator.
     *
     * @param p The Parcel used to populate the
     *          Model fields.
     */
    public Trip(Parcel p) {
        Trip_ID = p.readLong();
        destination = p.readString();
        longitude = p.readDouble();
        latitude = p.readDouble();
        datetime = p.readLong();

    }

    /**
     * Create a Trip model object from arguments
     *
     * @param trip_id,destination,datetime Add arbitrary number of arguments to
     *             instantiate Trip class based on member variables.
     */
    public Trip(Long trip_id,String destination,Double longitude,Double latitude, Long datetime ) {
        this.Trip_ID = trip_id;
        this.destination = destination;
        this.longitude = longitude;
        this.latitude = latitude;
        this.datetime = datetime;

    }

    /**
     * Serialize Trip object by using writeToParcel.
     * This function is automatically called by the
     * system when the object is serialized.
     *
     * @param dest  Parcel object that gets written on
     *              serialization. Use functions to write out the
     *              object stored via your member variables.
     * @param flags Additional flags about how the object
     *              should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
     *              In our case, you should be just passing 0.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Trip_ID);
        dest.writeString(destination);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeLong(datetime);
    }

    /**
     * Feel free to add additional functions as necessary below.
     */

    public Long getDateTime(){return datetime;}
    public String getDestination(){return  destination;}
    public Long getTripID(){return Trip_ID;}
    public Double getLongitude(){return longitude;}
    public Double getLatitude(){return latitude;}
    /**
     * Do not implement
     */
    @Override


    public int describeContents() {
        // Do not implement!
        return 0;
    }
}
