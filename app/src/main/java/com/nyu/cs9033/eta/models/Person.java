package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    // Member fields should exist here, what else do you need for a person?
    // Please add additional fields
    //A person should include the name of the person and the ETA to the destination and their current location
    private String name;
    private int ETA_hour;
    private int ETA_minute;
    private String CurrentLocation;
    private static final String KEY_NAME = "name";
    private static final String KEY_ETA_HOUR = "ETA_hour";
    private static final String KEY_ETA_MINUTE = "ETA_minute";
    private static final String KEY_CURRENT_LOCATION = "CurrentLocation";

    /**
     * Parcelable creator. Do not modify this function.
     */
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel p) {
            return new Person(p);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    /**
     * Create a Person model object from a Parcel. This
     * function is called via the Parcelable creator.
     *
     * @param p The Parcel used to populate the
     *          Model fields.
     */
    public Person(Parcel p) {
        this.name = p.readString();
        this.ETA_hour = p.readInt();
        this.ETA_minute = p.readInt();
        this.CurrentLocation = p.readString();}

    /**
     * Create a Person model object from arguments
     *
     * @param name Add arbitrary number of arguments to
     *             instantiate Person class based on member variables.
     */
    public Person(String name, int ETA_hour,int ETA_minute, String CurrentLocation) {
        this.name = name;
        this.ETA_hour = ETA_hour;
        this.ETA_minute= ETA_minute;
        this.CurrentLocation = CurrentLocation;
    }

    /**
     * Serialize Person object by using writeToParcel.
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
    //Writes all member fields to the bundle
        dest.writeString(name);
        dest.writeInt(ETA_hour);
        dest.writeInt(ETA_minute);
        dest.writeString(CurrentLocation);

    }

    /**
     * Feel free to add additional functions as necessary below.
     */
    public String getName(){return name;}
    public int getETA_minute(){return ETA_minute;}
    public int getETA_hour(){return ETA_hour;}
    public String getCurrentLocation(){return CurrentLocation;}
    /**
     * Do not implement
     */
    @Override
    public int describeContents() {
        // Do not implement!
        return 0;
    }
}
