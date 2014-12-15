package ut.handshake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class Login extends ActionBarActivity implements LoginFragment.OnLoginListener {

    private String userId;
    EditText phoneNumber;
    EditText email;
    ArrayList<String> emails;
    ArrayList<String> phoneNumbers;

    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, new LoginFragment())
                    .commit();
        }

        phoneNumber = (EditText) findViewById(R.id.phone_text);
        email = (EditText) findViewById(R.id.email_text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PlusBaseFragment.OUR_REQUEST_CODE) {
            LoginFragment fragment = (LoginFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.login_container);
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void successfulLogin() {
        this.userId = userId;
        launchHandshakeActivity(this.userId, this.emails, this.phoneNumbers);
    }

    @Override
    public void unSuccessfulLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, new LoginFragment())
                .commit();
    }

    @Override
    public void onlySuccessfulGPlus(LoginFragment handle, String userId, String userName, String accountName) {
        this.userId = userId;
        this.emails = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
        String emailAsString = email.getText().toString();
        String phoneNumberAsString = phoneNumber.getText().toString();
        Log.d(TAG,"Email: "+emailAsString);
        Log.d(TAG,"Phone Number: "+phoneNumberAsString);
        emails.add(email.getText().toString());
        phoneNumbers.add(phoneNumber.getText().toString());
        HandshakeHttpHandlers.postUserRegistration(handle, userId, accountName, userName, emails, phoneNumbers);

    }
    public void launchHandshakeActivity(String userId, ArrayList<String> emails,
                                        ArrayList<String> phoneNumbers) {
        Intent intent = new Intent(this, Handshake.class);
        intent.putExtra(Handshake.USER_ID_KEY, userId);
        String[] emailsArr = new String[emails.size()];
        emailsArr = emails.toArray(emailsArr);
        intent.putExtra(Handshake.EMAILS, emailsArr);

        String[] phonesArr = new String[phoneNumbers.size()];
        phonesArr = emails.toArray(phonesArr);
        intent.putExtra(Handshake.PHONES, phonesArr);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
