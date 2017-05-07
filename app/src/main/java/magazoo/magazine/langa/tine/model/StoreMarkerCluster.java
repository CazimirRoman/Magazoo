package magazoo.magazine.langa.tine.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Suspedeal on 30-Apr-17.
 */

public class StoreMarkerCluster implements ClusterItem {

    public String name;
    public Double lat;
    public Double lon;
    public String type;
    public String description;
    public Double rating;
    public long createdAt;
    public String createdBy;

    public StoreMarkerCluster(){}

    public StoreMarkerCluster(String name, Double lat, Double lon, String type, String description, Double rating, long createdAt, String userId) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.createdBy = userId;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat,lon);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return description;
    }
}