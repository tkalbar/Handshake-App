package ut.handshake;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginFragment extends android.support.v4.app.Fragment  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginFragment.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    public static final int OUR_REQUEST_CODE = 49404;

    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    private GoogleApiClient mGoogleApiClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;

    private OnLoginListener mListener;

    // UI references.
    private View mProgressView;
    private SignInButton mPlusSignInButton;
    private View mLoginLayoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Login Fragment on create");
        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                //.addScope(SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) view.findViewById(R.id.plus_sign_in_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return view;
        }
        //updateConnectButtonState();

        mLoginLayoutView = view.findViewById(R.id.login_layout);
        mProgressView = view.findViewById(R.id.login_progress);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginLayoutView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void updateConnectButtonState() {
        boolean connected = mGoogleApiClient.isConnected();
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        int isPlayAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        Log.v("LoginFragment", "Play Services code:" + isPlayAvailable);
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext()) ==
                ConnectionResult.SUCCESS;
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public LoginFragment() {
        // Required empty public constructor
    }

    public void signIn() {
        if (!mGoogleApiClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            Log.v(TAG, "signIn()");
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        //updateConnectButtonState();
    }

    /**
     * Connect the {@link com.google.android.gms.plus.PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v(TAG, "connect request");
            mGoogleApiClient.connect();
        }
    }


    private void initiatePlusClientDisconnect() {
        if (mGoogleApiClient.isConnected()) {
            Log.v(TAG, "disconnect request");
            mGoogleApiClient.disconnect();
        }
        updateConnectButtonState();

    }

    /**
     * Revoke Google+ authorization completely.
     */

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
        //initiatePlusClientConnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        //initiatePlusClientDisconnect();
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            Log.v(TAG, "startResolutionForResult()");
            mConnectionResult.startResolutionForResult(getActivity(), OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == Activity.RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            Log.v(TAG, "onActivityResult() success");
            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            Log.v(TAG, "onActivityResult() failure");
            setProgressBarVisible(false);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "onConnected()");
        //String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        //Plus.PeopleApi.loadVisible(mGoogleApiClient, null);
        Person curUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String userId = curUser.getId();
        Log.v(TAG, userId);
        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String userName = curUser.getDisplayName();
        mListener.onlySuccessfulGPlus(this, userId, userName, accountName);

        /*updateConnectButtonState();
        setProgressBarVisible(false);
        mListener.successfulLogin(userId);*/
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //updateConnectButtonState();
        Log.v(TAG, "onConnectionSuspended()");
    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        Log.v(TAG, "onConnectionFailed()");
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                Log.v(TAG, "onConnectionFailed(), starting resolution");
                startResolution();
            }
        }
    }

    public void finishResponse() {
        updateConnectButtonState();
        setProgressBarVisible(false);
        mListener.successfulLogin();
    }

    public void finishWithFail() {
        setProgressBarVisible(false);
        mListener.unSuccessfulLogin();
    }

    public interface OnLoginListener {
        public void unSuccessfulLogin();
        public void successfulLogin();
        public void onlySuccessfulGPlus(LoginFragment handle, String userId, String userName, String accountName);
    }
}
