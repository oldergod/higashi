package com.oldering.kintone.higashi.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.oldering.kintone.higashi.R;
import com.oldering.kintone.higashi.api.slash.AuthApi;
import com.oldering.kintone.higashi.constant.ExtraKeys;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.CybozuSetting;

import rx.SingleSubscriber;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // UI references.
    private TextInputEditText domainView;
    private TextInputEditText usernameView;
    private TextInputEditText passwordView;
    private TextInputEditText certificatePasswordView;
    View progressView;
    View loginFormView;

    private Account account = new Account();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        domainView = (TextInputEditText) findViewById(R.id.domain);
        usernameView = (TextInputEditText) findViewById(R.id.username);
        certificatePasswordView = (TextInputEditText) findViewById(R.id.certificate_password);

        Intent intent = getIntent();
        manageIntent(intent);

        // TODO(benoit) only relying on cybozu setting but what
        // TODO we can still have certificate data on the Account even
        // TODO after a auth error
        // TODO we should just relogin by default since we have the stored password...

        passwordView = (TextInputEditText) findViewById(R.id.password);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkLoginAndRedirectIfNecessary();
    }

//    private void checkLoginAndRedirectIfNecessary() {
//        Account account = Account.currentAccount();
//        if (account.getRequestToken() != null) {
//            Intent intent = new Intent(this, NotificationPagerActivity.class);
//            startActivity(intent);
//        }
//        // else nothing special.
//    }

    private boolean validateLoginForm(String domain, String username, String password, String certificatePassword) {
        // Reset errors.
        domainView.setError(null);
        usernameView.setError(null);
        passwordView.setError(null);

        boolean isValid = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (certificatePasswordView.getVisibility() == View.VISIBLE && (TextUtils.isEmpty(certificatePassword) || !isCertificatePasswordValid(password))) {
            certificatePasswordView.setError(getString(R.string.error_invalid_certificate_password));
            focusView = certificatePasswordView;
            isValid = false;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            isValid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(domain)) {
            domainView.setError(getString(R.string.error_field_required));
            focusView = domainView;
            isValid = false;
        } else if (!isDomainValid(domain)) {
            domainView.setError(getString(R.string.error_invalid_domain));
            focusView = domainView;
            isValid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            isValid = false;
        } else if (!isUsernameValid(username)) {
            usernameView.setError(getString(R.string.error_invalid_email));
            focusView = usernameView;
            isValid = false;
        }

        if (!isValid) {
            focusView.requestFocus();
        }
        return isValid;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    void attemptLogin() {
        // Store values at the time of the login attempt.
        String domain = domainView.getText().toString();
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String certificatePassword = certificatePasswordView.getText().toString();

        // TODO(benoit) check if not already trying to log i.e. one async task running
        boolean isValid = validateLoginForm(domain, username, password, certificatePassword);

        if (isValid) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            account.setDomain(domain);
            account.setUsername(username);
            account.setPassword(password);
            if (!certificatePassword.isEmpty()) {
                account.setCertificatePassword(certificatePassword);
            }

            AuthApi.attemptLogin(account, new SingleSubscriber<Account>() {
                @Override
                public void onSuccess(Account account) {
                    navigateToNotifications();
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError: " + e.toString());
                    showError(e);
                }

            });
        }
    }

    void navigateToNotifications() {
        Intent intent = new Intent(this, NotificationPagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isDomainValid(String domain) {
        //TODO: Replace this with your own logic
        return domain.contains(".");
    }

    private boolean isUsernameValid(String email) {
        return true;
        //TODO: Replace this with your own logic
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isCertificatePasswordValid(String certificatePassword) {
        //TODO: Replace this with your own logic
        return certificatePassword.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void showError(Throwable throwable) {
        Log.d(TAG, "onError: ");
        showProgress(false);
        if (throwable instanceof KeyStoreMacInvalidException) {
            certificatePasswordView.setError("Wrong password or corrupt certificate");
            certificatePasswordView.requestFocus();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        manageIntent(intent);
    }

    private void manageIntent(Intent intent) {
        String cybozuSettingAsString = intent.getStringExtra(ExtraKeys.CYBOZU_SETTING);
        if (cybozuSettingAsString != null) {
            CybozuSetting cybozuSetting = new Gson().fromJson(cybozuSettingAsString, CybozuSetting.class);

            domainView.setText(cybozuSetting.getDomain());
            usernameView.setText(cybozuSetting.getLoginId());

            account.setDomain(cybozuSetting.getDomain());
            account.setUsername(cybozuSetting.getLoginId());

            if (cybozuSetting.hasCertificate()) {
                showCertificateView();
                account.setCertificateData(cybozuSetting.getCertificateData());
            } else {
                hideCertificateView();
            }
        } else {
            hideCertificateView();
        }
    }

    private void showCertificateView() {
        certificatePasswordView.setVisibility(View.VISIBLE);
    }

    private void hideCertificateView() {
        certificatePasswordView.setVisibility(View.GONE);
    }
}

