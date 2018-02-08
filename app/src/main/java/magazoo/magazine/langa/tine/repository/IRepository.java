package magazoo.magazine.langa.tine.repository;

import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;

public interface IRepository {
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener);
}
