package com.oldering.kintone.higashi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.util.AccountStore;
import com.oldering.kintone.higashi.util.OkHttpClientFactory;

import okhttp3.OkHttpClient;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // initGlobalVariables
        Account account = AccountStore.loadAccount();
        if (account == null) {
            navigateToLogin();
            return;
        }
        OkHttpClient okHttpClient = HigashiApplication.getInstance().getOkHttpClient();
        if (okHttpClient == null) {
            try {
                okHttpClient = OkHttpClientFactory.createClient(account);
                HigashiApplication.getInstance().setOkHttpClient(okHttpClient);
            } catch (KeyStoreMacInvalidException e) {
                e.printStackTrace();
                navigateToLogin();
                return;
            }
        }

        fillViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        verifyCredentials();
    }

    private void verifyCredentials() {
        // TODO(benoit) we should not need to do that everytime though.
        // I'd rather have an auth error on some real tries and then react.
    }

    abstract void inflateViews();

    abstract void fillViews();

    void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
