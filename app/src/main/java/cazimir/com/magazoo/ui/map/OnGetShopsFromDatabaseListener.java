package cazimir.com.magazoo.ui.map;


import java.util.ArrayList;

import cazimir.com.magazoo.model.Shop;

public interface OnGetShopsFromDatabaseListener {
    void onDataFetched(ArrayList<Shop> shopsAddedToday);
}
