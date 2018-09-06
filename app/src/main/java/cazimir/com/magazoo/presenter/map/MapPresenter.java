package cazimir.com.magazoo.presenter.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.ui.map.IMapActivityView;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddListener;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseListener;

import static cazimir.com.magazoo.constants.Constants.ANA_MARIA;
import static cazimir.com.magazoo.constants.Constants.CAZIMIR;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter {

    private static final String TAG = MapPresenter.class.getSimpleName();

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

    @Override
    public void checkIfDuplicateReport(Report currentReportedShop) {
        mRepository.checkIfDuplicateReport(new OnDuplicateReportListener() {
            @Override
            public void isDuplicateReport() {
                getMapActivityView().showReportThanksPopup();
                getMapActivityView().hideProgressBar();
                Log.d(TAG, "Duplicate " + getMapActivityView().getCurrentReportedShop().getRegards() + " report");
            }

            @Override
            public void isNotDuplicateReport() {
                Report currentReportedShop = getMapActivityView().getCurrentReportedShop();
                mRepository.writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                    @Override
                    public void onReportWrittenSuccess() {
                        getMapActivityView().closeReportDialog();
                        getMapActivityView().showReportThanksPopup();
                        getMapActivityView().hideProgressBar();
                        Log.d(TAG, "Report for " + getMapActivityView().getCurrentReportedShop().getRegards() + " written to DB");
                    }

                    @Override
                    public void onReportWrittenFailed(String error) {
                        getMapActivityView().showToast(error);
                    }
                }, currentReportedShop);
            }
        }, currentReportedShop);
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
    public void getAllMarkers(LatLngBounds bounds) {
        mRepository.getMarkers(new OnGetMarkersListener() {
            @Override
            public void onGetAllMarkersSuccess(ArrayList<Shop> markers) {
                Log.d(TAG, "onGetAllMarkersSuccess: " + markers.size());
                getMapActivityView().addMarkersToMap(markers);
            }

            @Override
            public void onGetAllMarkersFailed(String message) {
                getMapActivityView().showToast(message);
            }
        }, bounds);
    }

    @Override
    public void addMarkerToFirebase(Shop shop) {
        mRepository.addMarkerToDatabase(new OnAddMarkerToDatabaseListener() {
            @Override
            public void onAddMarkerSuccess() {

                Log.d(TAG, "onAddMarkerSuccess: called");
                getMapActivityView().hideProgressBar();
                getMapActivityView().refreshMarkersOnMap();
                getMapActivityView().showAddThanksPopup();
            }

            @Override
            public void onAddMarkerFailed(String error) {
                getMapActivityView().hideProgressBar();
                getMapActivityView().showToast(error);
            }
        }, shop);
    }

    @Override
    public void deleteShopFromDB(String shopId) {
        mRepository.deleteShop(new OnDeleteShopListener() {
            @Override
            public void onDeleteSuccess() {
                Log.d(TAG, "onDeleteSuccess: true");
                getMapActivityView().showToast("Deleted!");
                getMapActivityView().closeShopDetails();
                getMapActivityView().refreshMarkersOnMap();
                getMapActivityView().hideProgressBar();
            }

            @Override
            public void onDeleteFailed(String error) {
                getMapActivityView().showToast(error);
            }
        }, shopId);
    }

    @Override
    public boolean isAdmin() {
        return mAuthenticationPresenter.isAdmin();
    }

    private boolean isUnderTheReportLimit(ArrayList<Report> reportsAddedToday) {
        return reportsAddedToday.size() <= Constants.REPORT_SHOP_LIMIT;
    }

    public void checkIfAllowedToAdd(final OnIsAllowedToAddListener mapActivityView) {

        if(userId.equals(CAZIMIR)|| userId.equals(ANA_MARIA)){
            mapActivityView.isAllowedToAdd();
            return;
        }

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