package diabetes.aclass.diabetes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import diabetes.aclass.dagger.component.DataCallback;
import diabetes.aclass.model.UserEntity;
import diabetes.aclass.presenter.ProfilePresenterImpl;

import static diabetes.aclass.utils.Component.API_BASE;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private static final String TAG = "AndroidClarified";
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;
    ProfilePresenterImpl mainPresenter ;
    public static final String GOOGLE_ACCOUNT = "google_account";
    private static final String API_URL = API_BASE + "users";
    private int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);

        } else {
            setContentView(R.layout.activity_login);
            googleSignInButton = findViewById(R.id.sign_in_button);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.

            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }


    private void handleSignInResult(final GoogleSignInAccount account) {
        Intent myIntent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(myIntent);
        loadData(account);

    }

    private void loadData(final GoogleSignInAccount account){
        try {
            mainPresenter = new ProfilePresenterImpl();
            mainPresenter.fetchData(API_URL, new DataCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    UserEntity user = new UserEntity();
                    user.setId(account.getId());
                    user.setFirst_name(account.getDisplayName());
                    user.setLast_name(account.getFamilyName());
                    user.setUsername(account.getGivenName());
                    user.setEmail(account.getEmail());
                    user.setOauth_token(account.getIdToken());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(LoginActivity.GOOGLE_ACCOUNT, googleSignInAccount);
        startActivity(intent);
        finish();
    }

}