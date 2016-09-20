package com.oldering.kintone.higashi.util;


import android.content.Context;
import android.widget.Toast;

import com.oldering.kintone.higashi.BuildConfig;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientFactory {
    private OkHttpClientFactory() {
    }

    private static OkHttpClient wrapBuilder(Context context, OkHttpClient.Builder builder, final Account account) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
            builder.addNetworkInterceptor(logging);
        }
        builder
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json");
                        if (account.isAuthenticated()) {
                            requestBuilder.addHeader("X-Cybozu-RequestToken", account.getRequestToken());
                        } else if (account.hasAccessToken()) {
                            requestBuilder.addHeader("X-Cybozu-RequestToken", account.getAccessToken());
                        }
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .cookieJar(account.getCookieJar());
        return builder.build();
    }

    public static OkHttpClient createClient(Account account) throws KeyStoreMacInvalidException {
        Context context = HigashiApplication.getInstance().getApplicationContext();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (account == null || !account.hasCertificate()) {
            return wrapBuilder(context, builder, account);
        }

        KeyManager[] kmArray;
        SSLContext sslContext;
        TrustManager[] trustManagers;
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance("PKCS12");
            InputStream is = new ByteArrayInputStream(account.getCertificateData());

            String certificatePassword = account.getCertificatePassword();
            ks.load(is, certificatePassword.toCharArray());
            kmf.init(ks, certificatePassword.toCharArray());
            kmArray = kmf.getKeyManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmArray, null, null);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            trustManagers = tmf.getTrustManagers();
        } catch (IOException ioException) {
            Toast.makeText(context, "certificate password invalid or corrupted file", Toast.LENGTH_LONG).show();
            throw new KeyStoreMacInvalidException();
        } catch (CertificateException |
                NoSuchAlgorithmException |
                UnrecoverableKeyException |
                KeyManagementException |
                KeyStoreException e) {
            // TODO(benoit) clean around here...
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        // found some dirty? way...
        builder.sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0]);
        return wrapBuilder(context, builder, account);
    }
}
