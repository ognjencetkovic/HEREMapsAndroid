package ba.bitcamp.bitNavigator.models;

import java.util.Calendar;

/**
 * Created by semir.sahman on 22.10.15..
 */
public class Place {

    private int id;
    private String title;
    private String address;
    private Double longitude;
    private Double latitude;
    private Calendar dateAdded;

    public Place(int id, String title, String address, Double longitude, Double latitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Calendar getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Calendar dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ba.bitcamp.bitNavigator.models.Place){
            ba.bitcamp.bitNavigator.models.Place p = (ba.bitcamp.bitNavigator.models.Place) o;
            return this.id == p.id;
        }
        return false;
    }
}
