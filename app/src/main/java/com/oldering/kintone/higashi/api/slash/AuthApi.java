package com.oldering.kintone.higashi.api.slash;

import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.ApiResponse;
import com.oldering.kintone.higashi.util.AccountStore;
import com.oldering.kintone.higashi.util.OkHttpClientFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AuthApi {
    private AuthApi() {
        throw new AssertionError("No instances.");
    }

    public static void attemptLogin(final Account account, SingleSubscriber<Account> subscriber) throws KeyStoreMacInvalidException {
        account.clean();
        OkHttpClient okHttpClient = OkHttpClientFactory.createClient(account);
        HigashiApplication.getInstance().setOkHttpClient(okHttpClient);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(account.getBasicDomainUriBuilder().toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        final authService authService = retrofit.create(authService.class);
        Single<ApiResponse> accessTokenResponse = authService.getAccessToken();
        accessTokenResponse
                .flatMap(new Func1<ApiResponse, Single<ApiResponse>>() {
                    @Override
                    public Single<ApiResponse> call(ApiResponse apiResponse) {
                        account.setAccessToken(apiResponse
                                .getResult()
                                .getAsJsonPrimitive("token")
                                .getAsString());

                        return authService.login(new LoginInputForm(account.getUsername(), account.getPassword()));
                    }
                })
                .flatMap(new Func1<ApiResponse, Single<Account>>() {
                    @Override
                    public Single<Account> call(ApiResponse apiResponse) {
                        account.setRequestToken(apiResponse
                                .getResult()
                                .getAsJsonPrimitive("requestToken")
                                .getAsString());
                        AccountStore.saveAccount(account);
                        return Single.just(account);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
