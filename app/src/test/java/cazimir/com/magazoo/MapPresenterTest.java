package cazimir.com.magazoo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.map.MapPresenter;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseCallback;
import cazimir.com.magazoo.presenter.map.OnDeleteShopCallback;
import cazimir.com.magazoo.presenter.map.OnDuplicateReportCallback;
import cazimir.com.magazoo.presenter.map.OnGetMarkersListener;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.ui.map.MapActivityView;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddCallback;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseCallback;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenterTest {

    private static final String EMAIL_ADDRESS = "test@gmail.com";
    private static final String ERROR_MESSAGE = "This is an error message";
    private static final String SHOP_ID = "1234";
    private MapPresenter mMapPresenter;

    @Mock
    private AuthPresenter mAuthenticationPresenter;

    @Mock
    private MapActivityView mMapActivityView;

    @Mock
    private Repository mRepository;

    @Captor
    private ArgumentCaptor<OnDuplicateReportCallback> mOnDuplicateReportCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnReportWrittenToDatabaseCallback> mOnReportWrittenToDatabaseCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnGetMarkersListener> mOnGetMarkersListenerArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnAddMarkerToDatabaseCallback> mOnAddMarkerToDatabaseCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnDeleteShopCallback> mOnDeleteShopCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnIsAllowedToAddCallback> mOnIsAllowedToAddCallbackArgumentCaptor;

    private Report mReport;

    @Before
    public void setUp() throws Exception {
        mReport = new Report();
        MockitoAnnotations.initMocks(this);
        mMapPresenter = new MapPresenter(mMapActivityView, mAuthenticationPresenter, mRepository);
    }

    /**
     * The user should not be aware that a shop has already been reported. Instead, show the user
     * a success message each time he clicks on mReport.
     */
    @Test
    public void shouldNotWriteReportToDatabaseAndShowThanksPopupIfDuplicateReport() {

        when(mMapActivityView.getCurrentReportedShop())
                .thenReturn(mReport);

        mMapPresenter.writeReportToDatabase(mReport);

        verify(mRepository).checkIfDuplicateReport(mOnDuplicateReportCallbackArgumentCaptor.capture(), eq(mReport));

        mOnDuplicateReportCallbackArgumentCaptor.getValue().isDuplicate();

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).closeReportDialog();
        inOrder.verify(mMapActivityView).showReportThanksPopup();
        inOrder.verify(mMapActivityView).hideProgressBar();
    }

    @Test
    public void shouldWriteReportToDatabaseAndShowThanksPopupIfNotDuplicateReport() {

        when(mMapActivityView.getCurrentReportedShop())
                .thenReturn(mReport);

        mMapPresenter.writeReportToDatabase(mReport);

        verify(mRepository).checkIfDuplicateReport(mOnDuplicateReportCallbackArgumentCaptor.capture(), eq(mReport));

        mOnDuplicateReportCallbackArgumentCaptor.getValue().isNotDuplicate();

        verify(mRepository).writeReportToDatabase(mOnReportWrittenToDatabaseCallbackArgumentCaptor.capture(), eq(mReport));

        mOnReportWrittenToDatabaseCallbackArgumentCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).closeReportDialog();
        inOrder.verify(mMapActivityView).showReportThanksPopup();
        inOrder.verify(mMapActivityView).hideProgressBar();
    }

    @Test
    public void shouldWriteReportToDatabaseAndShowErrorToastIfWritingToDBFailed() {

        when(mMapActivityView.getCurrentReportedShop())
                .thenReturn(mReport);

        mMapPresenter.writeReportToDatabase(mReport);

        verify(mRepository).checkIfDuplicateReport(mOnDuplicateReportCallbackArgumentCaptor.capture(), eq(mReport));

        mOnDuplicateReportCallbackArgumentCaptor.getValue().isNotDuplicate();

        verify(mRepository).writeReportToDatabase(mOnReportWrittenToDatabaseCallbackArgumentCaptor.capture(), eq(mReport));

        mOnReportWrittenToDatabaseCallbackArgumentCaptor.getValue().onFailed(ERROR_MESSAGE);

        verify(mMapActivityView).showToast(ERROR_MESSAGE);
    }

    @Test
    public void getUserEmail() {
        when(mAuthenticationPresenter.getUserEmail()).thenReturn(EMAIL_ADDRESS);
        String email = mMapPresenter.getUserEmail();
        Assert.assertEquals(EMAIL_ADDRESS, email);
    }

    @Test
    public void shouldAddMarkersToMapIfAllShopsFetchedWithSuccess() {

        ArrayList<Shop> shops = new ArrayList<>();

        LatLngBounds bounds = new LatLngBounds(new LatLng(49.46771509317244,11.099801994860172),
                new LatLng(49.46966754906408,11.101733520627022));

        mMapPresenter.getAllMarkers(bounds);

        verify(mRepository).getMarkers(mOnGetMarkersListenerArgumentCaptor.capture(), eq(bounds));

        mOnGetMarkersListenerArgumentCaptor.getValue().onGetAllMarkersSuccess(shops);

        verify(mMapActivityView).addMarkersToMap(shops);
    }

    @Test
    public void shouldAddMarkerToFirebaseAndShowAddThanksOnSuccess() {
        Shop shopToBeAdded = new Shop();
        mMapPresenter.addMarkerToFirebase(shopToBeAdded);
        verify(mRepository).addMarkerToDatabase(mOnAddMarkerToDatabaseCallbackArgumentCaptor.capture(), eq(shopToBeAdded));
        mOnAddMarkerToDatabaseCallbackArgumentCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).hideProgressBar();
        inOrder.verify(mMapActivityView).refreshMarkersOnMap();
        inOrder.verify(mMapActivityView).showAddThanksPopup();
    }

    @Test
    public void shouldAddMarkerToFirebaseAndShowErrorToastOnFailed() {
        Shop shopToBeAdded = new Shop();
        mMapPresenter.addMarkerToFirebase(shopToBeAdded);
        verify(mRepository).addMarkerToDatabase(mOnAddMarkerToDatabaseCallbackArgumentCaptor.capture(), eq(shopToBeAdded));
        mOnAddMarkerToDatabaseCallbackArgumentCaptor.getValue().onFailed(ERROR_MESSAGE);

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).hideProgressBar();
        inOrder.verify(mMapActivityView).showToast(ERROR_MESSAGE);
    }

    @Test
    public void shouldShowToastWithSuccessIfShopDeletedSuccessfully() {
        mMapPresenter.deleteShopFromDB(SHOP_ID);
        verify(mRepository).deleteShop(mOnDeleteShopCallbackArgumentCaptor.capture(), eq(SHOP_ID));
        mOnDeleteShopCallbackArgumentCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).showToast("Deleted!");
        inOrder.verify(mMapActivityView).closeShopDetails();
        inOrder.verify(mMapActivityView).refreshMarkersOnMap();
        inOrder.verify(mMapActivityView).hideProgressBar();
    }

    @Test
    public void shouldShowTheAddShopDialogIfAllowedToAdd() {

        //mMapPresenter.checkIfAllowedToAddShop

        mOnIsAllowedToAddCallbackArgumentCaptor.getValue().isAllowedToAdd();

        InOrder inOrder = Mockito.inOrder(mMapActivityView);
        inOrder.verify(mMapActivityView).showAddShopDialog();
        inOrder.verify(mMapActivityView).hideProgressBar();
    }
}
