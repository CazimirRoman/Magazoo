package cazimir.com.magazoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.presenter.authentication.AuthenticationPresenter;
import cazimir.com.magazoo.presenter.map.MapPresenter;
import cazimir.com.magazoo.presenter.map.OnDuplicateReportCallback;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.ui.map.MapActivityView;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseCallback;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenterTest {

    private MapPresenter mMapPresenter;

    @Mock
    private AuthenticationPresenter mAuthenticationPresenter;

    @Mock
    private MapActivityView mMapActivityView;

    @Mock
    private Repository mRepository;

    @Captor
    private ArgumentCaptor<OnDuplicateReportCallback> mOnDuplicateReportCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnReportWrittenToDatabaseCallback> mOnReportWrittenToDatabaseCallbackArgumentCaptor;

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
    public void isUserLoggedIn() {
    }

    @Test
    public void getUserEmail() {
    }

    @Test
    public void getAllMarkers() {
    }

    @Test
    public void addMarkerToFirebase() {
    }

    @Test
    public void deleteShopFromDB() {
    }

    @Test
    public void checkIfAllowedToAdd() {
    }
}