package com.oldering.kintone.higashi.api.kintone;

import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationFlagInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationMarkInputForm;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.ApiResponse;
import com.oldering.kintone.higashi.model.NotificationM;
import com.oldering.kintone.higashi.util.AccountStore;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class KintoneApi {
    public static Single<ApiResponse> flagNotification(NotificationM notification) {
        NotificationFlagInputForm inputForm = new NotificationFlagInputForm();
        inputForm.setFlagged(!notification.isFlagged());
        inputForm.setGroupKey(notification.getGroupKey());

        Retrofit retrofit = getRefrofit();

        NotificationService notificationService = retrofit.create(NotificationService.class);
        return notificationService
                .flagNotification(inputForm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Single<ApiResponse> markNotification(NotificationM notification) {
        NotificationMarkInputForm inputForm = new NotificationMarkInputForm();
        inputForm.addMessage(notification.getId(), notification.getGroupKey(), !notification.isRead());

        Retrofit retrofit = getRefrofit();

        NotificationService notificationService = retrofit.create(NotificationService.class);
        return notificationService
                .markNotification(inputForm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private static Retrofit getRefrofit() {
        OkHttpClient okHttpClient = HigashiApplication.getInstance().getOkHttpClient();
        Account account = AccountStore.loadAccount();

        assert account != null;
        return new Retrofit.Builder()
                .baseUrl(account.getBasicDomainUriBuilder().toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
