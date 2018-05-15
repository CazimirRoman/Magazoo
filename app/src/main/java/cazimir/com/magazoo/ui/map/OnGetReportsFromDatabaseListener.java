package cazimir.com.magazoo.ui.map;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Report;

public interface OnGetReportsFromDatabaseListener {
        void onDataFetched(ArrayList<Report> reportsAddedToday);
}
