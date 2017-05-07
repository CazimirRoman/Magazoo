package magazoo.magazine.langa.tine.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Suspedeal on 30-Apr-17.
 */

public class StoreMarker implements ClusterItem {

    public String name;
    public Double lat;
    public Double lon;
    public String type;
    public String description;


    public StoreMarker() {
    }

    public StoreMarker(String name, Double lat, Double lon, String type, String description) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat, this.lon);
    }

    @Override
    public String getTitle() {
        return this.name;
    }

    @Override
    public String getSnippet() {
        return this.description;
    }
}
