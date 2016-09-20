package com.oldering.kintone.higashi.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.oldering.kintone.higashi.constant.ExtraKeys;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.CybozuSetting;
import com.oldering.kintone.higashi.util.AccountStore;

public class EntryPointActivity extends AppCompatActivity {
    private static final String TAG = "EntryPointActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean isToNavigate = manageIntent(intent);
        if (isToNavigate) {
            return;
        }

        // load account
        Account account = AccountStore.loadAccount();
        if (account == null) {
            navigateToLogin();
            return;
        }

        if (!account.isAuthenticated()) {
            navigateToLogin();
            return;
        }

        navigateToNotifications();
    }

    private void navigateToLogin() {
        navigateToLogin(null);
    }

    private void navigateToLogin(CybozuSetting cybozuSetting) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (cybozuSetting != null) {
            String cybozuSettingAsString = new Gson().toJson(cybozuSetting);
            intent.putExtra(ExtraKeys.CYBOZU_SETTING, cybozuSettingAsString);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToNotifications() {
        Intent intent = new Intent(getApplicationContext(), NotificationPagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * @return isToNavigate
     */
    private boolean manageIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            // TODO(benoit) there is not necessarly a certificate
            // might be a certificate, lets check ?
            CybozuSetting cybozuSetting = CybozuSetting.parseUri(this, uri);
            if (cybozuSetting != null) {
                navigateToLogin(cybozuSetting);
                return true;
            } else {
                Log.i(TAG, "manageIntent: cybozusetting is null. " + uri.toString());
            }
        }
        Log.d(TAG, "manageIntent: intent's uri is null");
        return false;
    }
}
