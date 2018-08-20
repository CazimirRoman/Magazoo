package cazimir.com.magazoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.magazoo.R;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.register.RegisterPresenter;
import cazimir.com.magazoo.ui.register.OnRegisterWithEmailCallback;
import cazimir.com.magazoo.ui.register.RegisterActivityView;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class RegisterPresenterTest {

    private static final String ERROR_MESSAGE = "This is an error message";
    private static String EMAIL = "test@email.com";
    private static String PASSWORD = "1234";

    private RegisterPresenter mRegisterPresenter;

    @Mock
    private RegisterActivityView mRegisterActivityView;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnRegisterWithEmailCallback> mOnRegisterWithEmailCallbackCaptor;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mRegisterPresenter = new RegisterPresenter(mRegisterActivityView, mAuthPresenter);
    }

    @Test
    public void shouldRedirectToLoginPageIfRegistrationSuccessful(){

        mRegisterPresenter.performRegisterWithEmail(EMAIL, PASSWORD);

        verify(mAuthPresenter, times(1)).register(mOnRegisterWithEmailCallbackCaptor.capture(), eq(EMAIL), eq(PASSWORD));

        mOnRegisterWithEmailCallbackCaptor.getValue().onSuccess(EMAIL);

        InOrder inOrder = Mockito.inOrder(mRegisterActivityView);
        inOrder.verify(mRegisterActivityView).showRegistrationConfirmationToast(EMAIL);
        inOrder.verify(mRegisterActivityView).redirectToLoginPage();
        inOrder.verify(mRegisterActivityView).hideProgressBar();
    }

    @Test
    public void shouldShowAnErrorIfRegistrationNotSuccessful(){

        mRegisterPresenter.performRegisterWithEmail(EMAIL, PASSWORD);

        verify(mAuthPresenter, times(1)).register(mOnRegisterWithEmailCallbackCaptor.capture(), eq(EMAIL), eq(PASSWORD));

        mOnRegisterWithEmailCallbackCaptor.getValue().onFailed(ERROR_MESSAGE);

        InOrder inOrder = Mockito.inOrder(mRegisterActivityView);
        inOrder.verify(mRegisterActivityView).hideProgressBar();
        inOrder.verify(mRegisterActivityView).showToast(ERROR_MESSAGE);
    }
}