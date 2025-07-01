package com.atipls.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.Callable;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private State s;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mApiUrlView;
    private EditText mCdnUrlView;
    private EditText mGatewayUrlView;
    private EditText mTokenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_login);
        s = new State(this);

        // Set up the login form.
        mApiUrlView = (EditText) findViewById(R.id.api_url);
        mCdnUrlView = (EditText) findViewById(R.id.cdn_url);
        mGatewayUrlView = (EditText) findViewById(R.id.gateway_url);
        mTokenView = (EditText) findViewById(R.id.token);

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mApiUrlView.setError(null);
        mCdnUrlView.setError(null);
        mGatewayUrlView.setError(null);
        mTokenView.setError(null);

        // Store values at the time of the login attempt.
        String api_url = mApiUrlView.getText().toString();
        String cdn_url = mCdnUrlView.getText().toString();
        String gateway_url = mGatewayUrlView.getText().toString();
        String token = mTokenView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(api_url)) {
            mApiUrlView.setError(getString(R.string.error_field_required));
            focusView = mApiUrlView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cdn_url)) {
            mCdnUrlView.setError(getString(R.string.error_field_required));
            focusView = mCdnUrlView;
            cancel = true;
        }

        if (TextUtils.isEmpty(gateway_url)) {
            mGatewayUrlView.setError(getString(R.string.error_field_required));
            focusView = mGatewayUrlView;
            cancel = true;
        }

        if (TextUtils.isEmpty(token)) {
            mTokenView.setError(getString(R.string.error_field_required));
            focusView = mTokenView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(this, api_url, cdn_url,
                    gateway_url, token);
            mAuthTask.call();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        this.setProgressBarVisibility(show);
        this.setProgressBarIndeterminate(show);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask implements Callable<Void> {

        private final Context mContext;
        private final String mApiUrl;
        private final String mCdnUrl;
        private final String mGatewayUrl;
        private final String mToken;
        private Boolean success = false;
        private String error = "";

        UserLoginTask(Context context, String api_url, String cdn_url,
                      String gateway_url, String token) {
            mContext = context;
            mApiUrl = api_url;
            mCdnUrl = cdn_url;
            mGatewayUrl = gateway_url;
            mToken = token;
        }

        @Override
        public Void call() {
            s.executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.login(mApiUrl, mGatewayUrl, mCdnUrl, mToken);
                        success = true;
                    } catch (Exception e) {
                        success = false;
                        error = e.toString();
                    }

                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("api", mApiUrl);
                    editor.putString("cdn", mCdnUrl);
                    editor.putString("gateway", mGatewayUrl);
                    editor.putString("token", mToken);
                    editor.putInt("messageLoadCount", 25);
                    editor.commit();

                    s.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAuthTask = null;
                            showProgress(false);

                            if (success) {
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                s.error(error);
                            }
                        }
                    });
                }
            });
            return null;
        }

    }
}
