package cazimir.com.magazoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.login.LoginPresenter;
import cazimir.com.magazoo.presenter.login.OnLoginWithFacebookCallback;
import cazimir.com.magazoo.ui.login.LoginActivityView;
import cazimir.com.magazoo.ui.login.OnLoginWithEmailCallback;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LoginPresenterTest {

    private static final String ERROR_MESSAGE = "This is an error message";
    private static String EMAIL = "test@email.com";
    private static String PASSWORD = "1234";

    //class under test
    private LoginPresenter mLoginPresenter;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Mock
    private LoginActivityView mLoginActivityView;

    @Captor
    private ArgumentCaptor<OnLoginWithEmailCallback> mOnLoginWithEmailCallbackCaptor;

    @Captor
    private ArgumentCaptor<OnLoginWithFacebookCallback> mOnLoginWithFacebookCallbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Get a reference to the class under test
        mLoginPresenter = new LoginPresenter(mLoginActivityView, mAuthPresenter);
    }

    @Test
    public void shouldRedirectToMapIfCorrectCredentials() {

        mLoginPresenter.performLoginWithEmail(EMAIL, PASSWORD);

        verify(mAuthPresenter, times(1)).login(mOnLoginWithEmailCallbackCaptor.capture(), eq(EMAIL), eq(PASSWORD));

        mOnLoginWithEmailCallbackCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).hideProgressBar();
        inOrder.verify(mLoginActivityView).checkIfOnboardingNeeded();
    }

    @Test
    public void shouldShowErrorToastIfIncorrectCredentials() {

        mLoginPresenter.performLoginWithEmail(EMAIL, PASSWORD);

        verify(mAuthPresenter, times(1)).login(mOnLoginWithEmailCallbackCaptor.capture(), eq(EMAIL), eq(PASSWORD));

        mOnLoginWithEmailCallbackCaptor.getValue().onFailed(ERROR_MESSAGE);

        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).hideProgressBar();
        inOrder.verify(mLoginActivityView).showToast(ERROR_MESSAGE);
    }

    @Test
    public void shouldRedirectToMapIfFacebookLoginSuccess(){
        mLoginPresenter.performLoginWithFacebook();

        verify(mAuthPresenter, times(1)).loginWithFacebook(mOnLoginWithFacebookCallbackCaptor.capture());

        mOnLoginWithFacebookCallbackCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).hideProgressBar();
        inOrder.verify(mLoginActivityView).checkIfOnboardingNeeded();
    }

    @Test
    public void shouldShowErrorToastIfFacebookLoginFailed(){
        mLoginPresenter.performLoginWithFacebook();

        verify(mAuthPresenter, times(1)).loginWithFacebook(mOnLoginWithFacebookCallbackCaptor.capture());

        mOnLoginWithFacebookCallbackCaptor.getValue().onFailed(ERROR_MESSAGE);

        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).hideProgressBar();
        inOrder.verify(mLoginActivityView).showToast(ERROR_MESSAGE);
    }
}