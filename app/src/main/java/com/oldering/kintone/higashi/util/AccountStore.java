package com.oldering.kintone.higashi.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.model.Account;

/**
 * @overview
 */

public class AccountStore {
    private static final String ACCOUNT_STORE = "com.oldering.kintone.higashi.ACCOUNT_STORE";
    private static final String ACCOUNT_KEY = "com.oldering.kintone.higashi.ACCOUNT_KEY";
    private static final Gson gson = new Gson();

    private AccountStore() {
    }

    public static void saveAccount(Account account) {
        Context context = HigashiApplication.getInstance().getApplicationContext();
        SharedPreferences.Editor editor = context.getSharedPreferences(ACCOUNT_STORE, Context.MODE_PRIVATE).edit();
        editor.putString(ACCOUNT_KEY, gson.toJson(account));
        editor.apply();
    }

    public static Account loadAccount() {
        Context context = HigashiApplication.getInstance().getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(ACCOUNT_STORE, Context.MODE_PRIVATE);
        String accountAsString = sharedPref.getString(ACCOUNT_KEY, null);
        if (accountAsString == null) {
            return null;
        }
        return gson.fromJson(accountAsString, Account.class);
    }
}
