package magazoo.magazine.langa.tine.presenter;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.repository.Repository;
import magazoo.magazine.langa.tine.ui.map.IMapActivityView;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToAddListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;
import magazoo.magazine.langa.tine.ui.map.OnReportWrittenToDatabaseListener;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter, OnDuplicateReportListener, OnReportWrittenToDatabaseListener, OnGetShopsAddedTodayListener, OnAddListenerForNewMarkerAdded, OnGetAllMarkersListener, OnAddMarkerToDatabaseListener {

    private Repository mRepository;
    private IGeneralView mView;
    private AuthenticationPresenter mAuthenticationPresenter;
    private String userId;
    public MapPresenter(IGeneralView mView) {
        this.mView = mView;
        this.mRepository = new Repository();
        this.mAuthenticationPresenter = new AuthenticationPresenter(mView);
        this.userId = mAuthenticationPresenter.getUserId();
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
        }, userId);
    }

    @Override
    public void checkIfDuplicateReport(Report currentReportedShop) {
        mRepository.checkIfDuplicateReport(this, userId, currentReportedShop);
    }

    @Override
    public boolean isUserLoggedIn() {
        return mAuthenticationPresenter.isLoggedIn();
    }

    @Override
    public String getUserEmail() {
        return mAuthenticationPresenter.getUserEmail();
    }

    @Override
    public void addListenerForNewMarkerAdded() {
        mRepository.addChildEventListenerForMarker(this);
    }

    @Override
    public void getAllMarkers(LatLngBounds bounds) {
        mRepository.getAllMarkers(this, bounds);
    }

    @Override
    public void addMarkerToFirebase(Marker markerToAdd) {
        markerToAdd.setCreatedBy(mAuthenticationPresenter.getUserId());
        mRepository.addMarkerToDatabase(this, markerToAdd);
    }

    private boolean isUnderTheReportLimit(ArrayList<Report> reportsAddedToday) {
        return reportsAddedToday.size() <= Constants.REPORT_SHOP_LIMIT;
    }

    public void checkIfAllowedToAdd(final OnIsAllowedToAddListener listener) {
        mRepository.getShopsAddedToday(new OnGetShopsAddedTodayListener() {
            @Override
            public void onGetShopsAddedTodaySuccess(ArrayList<Marker> shopsAddedToday) {
                if (isUnderTheAddLimit(shopsAddedToday)) {
                    listener.isAllowedToAdd();
                } else {
                    listener.isNotAllowedToAdd();
                }
            }

            @Override
            public void onGetShopsAddedTodayFailed() {
                //TODO: handle this
            }
        }, userId);
    }

    private boolean isUnderTheAddLimit(ArrayList<Marker> shopsAddedToday) {
        return shopsAddedToday.size() <= Constants.ADD_SHOP_LIMIT;
    }

    @Override
    public void isDuplicateReport(String regards) {
        getMapActivityView().closeReportDialog();
        getMapActivityView().showDuplicateReportErrorDialog(regards);
    }

    @Override
    public void isNotDuplicateReport(String regards) {
        getMapActivityView().closeReportDialog();
        Marker currentReportedShopMarker = getMapActivityView().getCurrentOpenShop();
        mRepository.writeReportToDatabase(this, userId, currentReportedShopMarker, regards, false);
    }

    private IMapActivityView getMapActivityView() {
        return (IMapActivityView) mView.getInstance();
    }

    @Override
    public void onReportWrittenSuccess() {
        getMapActivityView().closeReportDialog();
        getMapActivityView().showReportThanksPopup();
    }

    @Override
    public void onReportWrittenFailed(String error) {
        getMapActivityView().showToast(error);
    }

    @Override
    public void onGetShopsAddedTodaySuccess(ArrayList<Marker> shopsAddedToday) {

    }

    @Override
    public void onGetShopsAddedTodayFailed() {
        getMapActivityView().showShopLimitErrorDialog();
    }

    @Override
    public void onAddListenerForNewMarkerAddedSuccess(Marker marker, String title) {
        getMapActivityView().addNewlyAddedMarkerToMap(marker, title);
    }

    @Override
    public void onAddListenerForNewMarkerAddedFailed() {

    }

    @Override
    public void onGetAllMarkersSuccess(ArrayList<Marker> markers) {
        getMapActivityView().addMarkersToMap(markers);
    }

    @Override
    public void onGetAllMarkersFailed(String message) {
        getMapActivityView().showToast(message);
    }

    @Override
    public void onAddMarkerSuccess() {
        getMapActivityView().closeAddShopDialog();
        getMapActivityView().showAddThanksPopup();
    }

    @Override
    public void onAddMarkerFailed(String error) {
        getMapActivityView().closeAddShopDialog();
        getMapActivityView().showToast(error);
    }
}
