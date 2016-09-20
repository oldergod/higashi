package com.oldering.kintone.higashi.model;

import android.content.Context;
import android.net.Uri;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.exception.DomainNotFoundException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {
//    private static volatile Account sInstance;
//
//    public static Account currentAccount() {
//        if (sInstance == null) {
//            sInstance = new Account();
//        }
//        return sInstance;
//    }
//
//    public static void setCurrentAccout(Account account) {
//        sInstance = account;
//    }

    private String domain;
    private String username;
    private String password;
    private String accessToken;
    private String requestToken;
    private byte[] certificateData;
    private String certificatePassword;
    private String sessionIdValue;
    // not to be serialized
    private transient PersistentCookieJar cookieJar;

//    @Deprecated
//    /**
//     * should not be used now, testing stuff
//     */
//    public static Account createAccount(String domain, String login, String password) {
//        sInstance = new Account(domain, login, password);
//        return sInstance;
//    }

    public Account() {
    }

    public Uri.Builder getBasicDomainUriBuilder() {
        if (this.domain == null) {
            throw new DomainNotFoundException();
        }

        return Uri.parse(this.domain)
                .buildUpon()
                .scheme("https")
                .path("")
                .clearQuery()
                .query("");
    }

    public PersistentCookieJar getCookieJar() {
        Context context = HigashiApplication.getInstance().getApplicationContext();
        if (this.cookieJar == null) {
            this.cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        }
        return this.cookieJar;
    }

    public boolean hasCertificate() {
        return this.certificateData != null;
    }

    public boolean isAuthenticated() {
        return requestToken != null;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public void clean() {
        getCookieJar().clear();
        this.accessToken = null;
        this.requestToken = null;
    }

    public Uri createUri(String afterHost) {
        String domain = getBasicDomainUriBuilder().build().toString();
        if (domain.endsWith("/")) {
            afterHost = afterHost.substring(1);
        }
        return Uri.parse(domain + afterHost);
    }
}
