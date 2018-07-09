package cazimir.com.magazoo.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

public class Shop implements ClusterItem {

    private String id;
    private Double lat;
    private Double lon;
    private String type;
    private boolean pos;
    private boolean nonstop;
    private boolean tickets;
    private long createdAt;
    private String createdBy;
    private String city;
    private String country;


    public Shop() {
    }

    public Shop(String id, Double lat, Double lon, String type, Boolean pos, Boolean nonstop, Boolean tickets, String userId, String city, String country) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.pos = pos;
        this.nonstop = nonstop;
        this.tickets = tickets;
        this.createdAt = new Date().getTime();
        this.createdBy = userId;
        this.city = city;
        this.country = country;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    @Exclude
    public LatLng getPosition() {
        return new LatLng(getLat(), getLon());
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}