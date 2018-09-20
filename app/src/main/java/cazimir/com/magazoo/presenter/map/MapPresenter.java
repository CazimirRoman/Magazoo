package cazimir.com.magazoo.presenter.map;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.authentication.IAuthPresenter;
import cazimir.com.magazoo.repository.IRepository;
import cazimir.com.magazoo.ui.map.IMapActivityView;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddCallback;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseCallback;

import static cazimir.com.magazoo.constants.Constants.ANA_MARIA;
import static cazimir.com.magazoo.constants.Constants.CAZIMIR;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter {

    private static final String TAG = MapPresenter.class.getSimpleName();

    private IRepository mRepository;
    private IMapActivityView mMapActivityView;
    private IAuthPresenter mAuthenticationPresenter;

    public MapPresenter(IMapActivityView view, IAuthPresenter authenticationPresenter, IRepository repository) {
        mMapActivityView = view;
        mRepository = repository;
        mAuthenticationPresenter = authenticationPresenter;
    }

    @Override
    public void writeReportToDatabase(Report currentReportedShop) {
        mRepository.checkIfDuplicateReport(new OnDuplicateReportCallback() {
            @Override
            public void isDuplicate() {
                mMapActivityView.closeReportDialog();
                mMapActivityView.showReportThanksPopup();
                mMapActivityView.hideProgressBar();
                //Log.d(TAG, "Duplicate " + mMapActivityView.getCurrentReportedShop().getRegards() + " report");
            }

            @Override
            public void isNotDuplicate() {
                Report currentReportedShop = mMapActivityView.getCurrentReportedShop();
                mRepository.writeReportToDatabase(new OnReportWrittenToDatabaseCallback() {
                    @Override
                    public void onSuccess() {
                        mMapActivityView.closeReportDialog();
                        mMapActivityView.showReportThanksPopup();
                        mMapActivityView.hideProgressBar();
                        //Log.d(TAG, "Report for " + mMapActivityView.getCurrentReportedShop().getRegards() + " written to DB");
                    }

                    @Override
                    public void onFailed(String error) {
                        mMapActivityView.showToast(error);
                    }
                }, currentReportedShop);
            }
        }, currentReportedShop);
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
                //Log.d(TAG, "onGetAllMarkersSuccess: " + markers.size());
                mMapActivityView.addMarkersToMap(markers);
            }

            @Override
            public void onGetAllMarkersFailed(String message) {
                mMapActivityView.showToast(message);
            }
        }, bounds);
    }

    @Override
    public void addMarkerToFirebase(Shop shop) {
        mRepository.addMarkerToDatabase(new OnAddMarkerToDatabaseCallback() {
            @Override
            public void onSuccess() {
                //Log.d(TAG, "onSuccess: called");
                mMapActivityView.hideProgressBar();
                mMapActivityView.refreshMarkersOnMap();
                mMapActivityView.showAddThanksPopup();
            }

            @Override
            public void onFailed(String error) {
                mMapActivityView.hideProgressBar();
                mMapActivityView.showToast(error);
            }
        }, shop);
    }

    @Override
    public void deleteShopFromDB(String shopId) {
        mRepository.deleteShop(new OnDeleteShopCallback() {
            @Override
            public void onSuccess() {
                //Log.d(TAG, "onSuccess: true");
                mMapActivityView.showToast("Deleted!");
                mMapActivityView.closeShopDetails();
                mMapActivityView.refreshMarkersOnMap();
                mMapActivityView.hideProgressBar();
            }

            @Override
            public void onFailed(String error) {
                mMapActivityView.showToast(error);
            }
        }, shopId);
    }

    @Override
    public boolean isAdmin() {
        return mAuthenticationPresenter.isAdmin();
    }

    public void checkIfAllowedToAddShop(final OnIsAllowedToAddCallback mapActivityView) {

        String userId = mAuthenticationPresenter.getUserId();

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

    public String getUserId() {
        return mAuthenticationPresenter.getUserId();
    }

    public void signOut() {
        mAuthenticationPresenter.signOut();
    }
}
