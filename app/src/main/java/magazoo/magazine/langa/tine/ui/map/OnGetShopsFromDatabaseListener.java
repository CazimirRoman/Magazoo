package magazoo.magazine.langa.tine.ui.map;


import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Marker;

public interface OnGetShopsFromDatabaseListener {
    void onDataFetched(ArrayList<Marker> shopsAddedToday);
}
