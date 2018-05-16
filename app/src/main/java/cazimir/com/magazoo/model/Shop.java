package cazimir.com.magazoo.model;

import java.util.Date;

public class Shop {

    private String id;
    private Double lat;
    private Double lon;
    private String type;
    private boolean pos;
    private boolean nonstop;
    private boolean tickets;
    private long createdAt;
    private String createdBy;


    public Shop() {
    }

    public Shop(String id, Double lat, Double lon, String type, Boolean pos, Boolean nonstop, Boolean tickets, String userId) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.pos = pos;
        this.nonstop = nonstop;
        this.tickets = tickets;
        this.createdAt = new Date().getTime();
        this.createdBy = userId;
    }

    public void setTickets(boolean tickets) {
        this.tickets = tickets;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean getPos() {
        return pos;
    }

    public boolean getNonstop() {
        return nonstop;
    }

    public boolean getTickets() {
        return tickets;
    }
}