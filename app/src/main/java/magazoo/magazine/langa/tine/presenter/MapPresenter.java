package magazoo.magazine.langa.tine.presenter;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.model.Shop;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.repository.Repository;
import magazoo.magazine.langa.tine.ui.map.IMapActivityView;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToAddListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;
import magazoo.magazine.langa.tine.ui.map.OnReportWrittenToDatabaseListener;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter {

    private Repository mRepository;
    private IGeneralView mView;
    private AuthPresenter mAuthenticationPresenter;
    private String userId;

    public MapPresenter(IGeneralView mView) {
        this.mView = mView;
        this.mRepository = new Repository();
        this.mAuthenticationPresenter = new AuthPresenter(mView);
        this.userId = mAuthenticationPresenter.getUserId();
    }

    public void checkIfAllowedToReport(final OnIsAllowedToReportListener mapActivityView) {

        mRepository.getReportsAddedToday(new OnGetReportsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Report> reportsAddedToday) {
                if (isUnderTheReportLimit(reportsAddedToday)) {
                    mapActivityView.isAllowedToReport();
                } else {
                    mapActivityView.isNotAllowedToReport();
                }
            }
        }, userId);
    }

    @Override
    public void checkIfDuplicateReport(Report currentReportedShop) {
        mRepository.checkIfDuplicateReport(new OnDuplicateReportListener() {
            @Override
            public void isDuplicateReport(String regards) {
                getMapActivityView().closeReportDialog();
                getMapActivityView().showDuplicateReportErrorDialog(regards);
            }

            @Override
            public void isNotDuplicateReport(String regards) {
                getMapActivityView().closeReportDialog();
                Shop currentReportedShopMarker = getMapActivityView().getCurrentSelectedShop();
                mRepository.writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                    @Override
                    public void onReportWrittenSuccess() {
                        getMapActivityView().closeReportDialog();
                        getMapActivityView().showReportThanksPopup();
                    }

                    @Override
                    public void onReportWrittenFailed(String error) {
                        getMapActivityView().showToast(error);
                    }
                }, userId, currentReportedShopMarker, regards, false);
            }
        }, userId, currentReportedShop);
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
        mRepository.addChildEventListenerForMarker(new OnAddListenerForNewMarkerAdded() {
            @Override
            public void onAddListenerForNewMarkerAddedSuccess(Shop marker, String title) {
                getMapActivityView().addNewlyAddedMarkerToMap(marker, title);
            }

            @Override
            public void onAddListenerForNewMarkerAddedFailed() {

            }
        });
    }

    @Override
    public void getAllMarkers(LatLngBounds bounds) {
        mRepository.getAllMarkers(new OnGetAllMarkersListener() {
            @Override
            public void onGetAllMarkersSuccess(ArrayList<Shop> markers) {
                getMapActivityView().addMarkersToMap(markers);
            }

            @Override
            public void onGetAllMarkersFailed(String message) {
                getMapActivityView().showToast(message);
            }
        }, bounds);
    }

    @Override
    public void addMarkerToFirebase(Shop markerToAdd) {
        markerToAdd.setCreatedBy(mAuthenticationPresenter.getUserId());
        mRepository.addMarkerToDatabase(new OnAddMarkerToDatabaseListener() {
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
        }, markerToAdd);
    }

    private boolean isUnderTheReportLimit(ArrayList<Report> reportsAddedToday) {
        return reportsAddedToday.size() <= Constants.REPORT_SHOP_LIMIT;
    }

    public void checkIfAllowedToAdd(final OnIsAllowedToAddListener mapActivityView) {
        mRepository.getShopsAddedToday(new OnGetShopsAddedTodayListener() {
            @Override
            public void onGetShopsAddedTodaySuccess(ArrayList<Shop> shopsAddedToday) {
                if (isUnderTheAddLimit(shopsAddedToday)) {
                    mapActivityView.isAllowedToAdd();
                } else {
                    mapActivityView.isNotAllowedToAdd();
                }
            }

            @Override
            public void onGetShopsAddedTodayFailed() {
                //TODO: handle this
            }
        }, userId);
    }

    private boolean isUnderTheAddLimit(ArrayList<Shop> shopsAddedToday) {
        return shopsAddedToday.size() <= Constants.ADD_SHOP_LIMIT;
    }

    private IMapActivityView getMapActivityView() {
        return (IMapActivityView) mView.getInstance();
    }

    public String getUserId() {
        return mAuthenticationPresenter.getUserId();
    }

    public void signOut() {
        mAuthenticationPresenter.signOut();
    }
}