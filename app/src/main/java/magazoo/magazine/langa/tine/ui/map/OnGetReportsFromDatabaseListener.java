package magazoo.magazine.langa.tine.ui.map;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Report;

interface OnGetReportsFromDatabaseListener {
        void onDataFetched(ArrayList<Report> reportsAddedToday);
}
