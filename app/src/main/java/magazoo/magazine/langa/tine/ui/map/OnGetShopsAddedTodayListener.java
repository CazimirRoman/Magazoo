package magazoo.magazine.langa.tine.ui.map;


import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Marker;

interface OnGetShopsAddedTodayListener {
    void onDataFetched(ArrayList<Marker> markersAddedToday);
}
