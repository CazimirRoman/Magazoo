package magazoo.magazine.langa.tine.model;

import java.util.Date;

public class StoreMarker {

    public String name;
    public Double lat;
    public Double lon;
    public String type;
    public String description;
    public Double rating;
    public long createdAt;
    public String createdBy;


    public StoreMarker() {
    }

    public StoreMarker(String name, Double lat, Double lon, String type, String description, Double rating, String userId) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.description = description;
        this.rating = rating;
        this.createdAt = new Date().getTime();
        this.createdBy = userId;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Double getRating() {
        return rating;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
