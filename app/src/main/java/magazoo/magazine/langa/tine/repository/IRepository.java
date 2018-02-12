package magazoo.magazine.langa.tine.repository;

import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnGetShopsFromDatabaseListener;

public interface IRepository {
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener, String userId);
    void getShopsAddedToday(OnGetShopsFromDatabaseListener listener);
}
