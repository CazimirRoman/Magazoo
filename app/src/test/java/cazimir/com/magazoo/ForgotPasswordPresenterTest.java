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
import cazimir.com.magazoo.presenter.reset.ForgotPasswordPresenter;
import cazimir.com.magazoo.ui.reset.ForgotPasswordActivityView;
import cazimir.com.magazoo.ui.reset.OnResetInstructionsCallback;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class ForgotPasswordPresenterTest {

    private static final String ERROR_MESSAGE = "This is an error message";
    private static String EMAIL = "test@email.com";

    private ForgotPasswordPresenter mForgotPasswordPresenter;

    @Mock
    ForgotPasswordActivityView mForgotPasswordActivityView;

    @Mock
    AuthPresenter mAuthPresenter;

    @Captor
    ArgumentCaptor<OnResetInstructionsCallback> mOnResetInstructionsCallbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mForgotPasswordPresenter = new ForgotPasswordPresenter(mForgotPasswordActivityView, mAuthPresenter);
    }

    @Test
    public void shouldRedirectToLoginScreenAndShowSuccessToast() {

        mForgotPasswordPresenter.sendResetInstructions(EMAIL);

        verify(mAuthPresenter, times(1)).sendResetInstructions(mOnResetInstructionsCallbackArgumentCaptor.capture(), eq(EMAIL));

        mOnResetInstructionsCallbackArgumentCaptor.getValue().onSuccess();

        InOrder inOrder = Mockito.inOrder(mForgotPasswordActivityView);
        inOrder.verify(mForgotPasswordActivityView).hideProgress();
        inOrder.verify(mForgotPasswordActivityView).showEmailResetSentToast();
        inOrder.verify(mForgotPasswordActivityView).redirectToLogin();
    }
}