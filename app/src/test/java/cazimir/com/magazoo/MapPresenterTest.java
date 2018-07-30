package cazimir.com.magazoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.presenter.authentication.IAuthPresenter;
import cazimir.com.magazoo.presenter.map.MapPresenter;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.ui.map.IMapActivityView;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddListener;

import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenterTest {

    @Mock
    private Repository mShopsRepository;

    @Mock
    private IMapActivityView mMapActivityView;

    @Mock
    private IAuthPresenter mAuthPresenter;

    private MapPresenter mMapPresenter;

    @Before
    public void setupMapPresenter() {
        MockitoAnnotations.initMocks(this);
        mMapPresenter = new MapPresenter((IGeneralView) mMapActivityView, mAuthPresenter, mShopsRepository);
    }

    @Test
    public void clickOnAddFab_ShowsAddShopDialog() {

        OnIsAllowedToAddListener listener = new OnIsAllowedToAddListener() {
            @Override
            public void isAllowedToAdd() {

            }

            @Override
            public void isNotAllowedToAdd() {

            }
        };

        mMapPresenter.checkIfAllowedToAdd(listener);
        verify(mMapActivityView).showAddShopDialog();

    }
}