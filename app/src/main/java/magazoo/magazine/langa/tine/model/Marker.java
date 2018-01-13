package magazoo.magazine.langa.tine.model;

import java.util.Date;

public class Marker {

    private String id;
    private String name;
    private Double lat;
    private Double lon;
    private String type;
    private boolean pos;
    private boolean nonstop;
    private boolean tickets;
    private String description;
    private Double rating;
    private long createdAt;
    private String createdBy;


    public Marker() {
    }

    public Marker(String id, String name, Double lat, Double lon, String type, Boolean pos, Boolean nonstop, Boolean tickets, String description, Double rating, String userId) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.pos = pos;
        this.nonstop = nonstop;
        this.tickets = tickets;
        this.description = description;
        this.rating = rating;
        this.createdAt = new Date().getTime();
        this.createdBy = userId;
    }

    public boolean getTickets() {
        return tickets;
    }

    public void setTickets(boolean tickets) {
        this.tickets = tickets;
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

    public boolean getNonstop() {
        return nonstop;
    }
}
