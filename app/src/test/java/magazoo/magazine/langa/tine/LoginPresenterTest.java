package magazoo.magazine.langa.tine;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Executor;

import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.presenter.common.LoginPresenter;
import magazoo.magazine.langa.tine.ui.login.ILoginActivityView;
import magazoo.magazine.langa.tine.ui.login.LoginActivityView;
import magazoo.magazine.langa.tine.ui.login.OnLoginWithEmailFinishedListener;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)

public class LoginPresenterTest {

    @Mock
    AuthPresenter authPresenter;
    @Mock
    ILoginActivityView view;
    @Mock
    FirebaseAuth mFirebaseAuth;

    LoginPresenter presenter;

    @Test
    public void onLoginWithEmailSuccessRedirectToMapScreen() {

        Mockito.when(mFirebaseAuth.signInWithEmailAndPassword("email", "password")).thenReturn(new Task<AuthResult>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public AuthResult getResult() {
                return null;
            }

            @Override
            public <X extends Throwable> AuthResult getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<AuthResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        });

        presenter = new LoginPresenter(view);

        presenter.performLoginWithEmail("henyq@amail.club", "123456");

        Mockito.verify(view).hideProgressBar();
        Mockito.verify(view).goToMap();
    }
}