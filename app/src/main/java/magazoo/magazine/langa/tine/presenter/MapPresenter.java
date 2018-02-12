package magazoo.magazine.langa.tine.presenter;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.repository.Repository;
import magazoo.magazine.langa.tine.ui.map.IMapActivityView;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnGetShopsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToAddListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;
import magazoo.magazine.langa.tine.ui.map.OnReportWrittenToDatabaseListener;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter, OnDuplicateLocationReportListener, OnReportWrittenToDatabaseListener {

    private Repository mRepository;
    private IGeneralView mView;
    private AuthenticationPresenter mAuthenticationPresenter;
    public MapPresenter(IGeneralView mView) {
        this.mView = mView;
        this.mRepository = new Repository();
        this.mAuthenticationPresenter = new AuthenticationPresenter(mView);
    }

    public void checkIfAllowedToReport(final OnIsAllowedToReportListener listener) {

        mRepository.getReportsAddedToday(new OnGetReportsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Report> reportsAddedToday) {
                if (isUnderTheReportLimit(reportsAddedToday)) {
                    listener.isAllowedToReport();
                } else {
                    listener.isNotAllowedToReport();
                }
            }
        }, mAuthenticationPresenter.getUserId());

    }

    @Override
    public void checkIfDuplicateLocationReport(Report currentReportedShop) {
        mRepository.checkIfDuplicateLocationReport(this, mAuthenticationPresenter.getUserId(), currentReportedShop);
    }

    @Override
    public boolean isUserLoggedIn() {
        return mAuthenticationPresenter.isLoggedIn();
    }

    @Override
    public String getUserEmail() {
        return mAuthenticationPresenter.getUserEmail();
    }

    private boolean isUnderTheReportLimit(ArrayList<Report> reportsAddedToday) {
        return reportsAddedToday.size() <= Constants.REPORT_SHOP_LIMIT;
    }

    public void checkIfAllowedToAdd(final OnIsAllowedToAddListener listener) {
        getShopsAddedToday(new OnGetShopsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Marker> shopsAddedToday) {
                if (isUnderTheAddLimit(shopsAddedToday)) {
                    listener.isAllowedToAdd();
                } else {
                    listener.isNotAllowedToAdd();
                }
            }
        });
    }

    private boolean isUnderTheAddLimit(ArrayList<Marker> shopsAddedToday) {
        return shopsAddedToday.size() <= Constants.ADD_SHOP_LIMIT;
    }

    @Override
    public void isDuplicateLocationReport() {
        getMapActivityView().showDuplicateLocationReportErrorDialog();
    }

    @Override
    public void isNotDuplicateLocationReport() {

        mRepository.writeReportToDatabase(this);
        writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
            @Override
            public void onReportWritten() {
                showReportThanksPopup();
            }
        }, mCurrentOpenShop, Constants.REPORT_LOCATION, false);
    }

    private IMapActivityView getMapActivityView() {
        return (IMapActivityView) mView.getInstance();
    }

    @Override
    public void onReportWritten() {

    }
}
