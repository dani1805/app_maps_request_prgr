package com.example.mycitiesvisited;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity

// Clase que empleamos como tabla al ser una @Entity y la hacemos parcelable para poder pasar los datos de forma rápida
public class Cities implements Parcelable, Comparable<Cities> {
    @PrimaryKey(autoGenerate = true)
    public int id;

    String name;
    String country;
    String weather;
    double latitude;
    double longitude;
    boolean isVisited;
    String photo;

    @Ignore

    double distance;

    public Cities(String name, String country, String weather, boolean isVisited, double latitude, double longitude, String photo) {
        this.name = name;
        this.country = country;
        this.weather = weather;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVisited = isVisited;
        this.photo = photo;
    }

    @Ignore
    protected Cities(Parcel in) {
        //id = in.readInt();
        name = in.readString();
        country = in.readString();
        weather = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isVisited = in.readByte() != 0;
        photo = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<Cities> CREATOR = new Creator<Cities>() {
        @Override
        public Cities createFromParcel(Parcel in) {
            return new Cities(in);
        }

        @Override
        public Cities[] newArray(int size) {
            return new Cities[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(weather);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (isVisited ? 1 : 0));
        dest.writeString(photo);
        dest.writeDouble(distance);
    }

    @Override
    public int compareTo(Cities o) {
        String o_name = o.getName().toUpperCase();
        String name_this = this.name.toUpperCase();
        return name_this.compareTo(o_name);
    }

    public static Comparator<Cities> orderDistance = new
            Comparator<Cities>() {
                @Override
                public int compare(Cities o1, Cities o2) {
                    double firstDistance = o1.getDistance();
                    double secondDistance = o2.getDistance();
                    return (int) (firstDistance - secondDistance);
                }
            };

}
