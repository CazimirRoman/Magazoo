package magazoo.magazine.langa.tine.model;

import java.util.Date;

public class StoreMarker {

    private String id;
    private String name;
    private Double lat;
    private Double lon;
    private String type;
    private boolean pos;
    private boolean nonstop;
    private String description;
    private Double rating;
    private long createdAt;
    private String createdBy;


    public StoreMarker() {
    }

    public StoreMarker(String id, String name, Double lat, Double lon, String type, Boolean pos, Boolean nonstop, String description, Double rating, String userId) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.pos = pos;
        this.nonstop = nonstop;
        this.description = description;
        this.rating = rating;
        this.createdAt = new Date().getTime();
        this.createdBy = userId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean getPos() {
        return pos;
    }

    public boolean getNonStop() {
        return nonstop;
    }
}
