package com.oldering.kintone.higashi.datamodel;

import com.google.gson.Gson;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.api.kintone.NotificationService;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationGetInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationsGetInputForm;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.ApiResponse;
import com.oldering.kintone.higashi.model.NotificationContainer;
import com.oldering.kintone.higashi.model.NotificationsWrapper;
import com.oldering.kintone.higashi.util.AccountStore;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NotificationModel implements INotificationModel {
    @Override
    public Single<NotificationContainer> getNotification(long notificationId) {
        NotificationGetInputForm inputForm = new NotificationGetInputForm();
        inputForm.setId(notificationId);

        OkHttpClient okHttpClient = HigashiApplication.getInstance().getOkHttpClient();
        Account account = AccountStore.loadAccount();

        assert account != null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(account.getBasicDomainUriBuilder().toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        NotificationService notificationService = retrofit.create(NotificationService.class);
        return notificationService
                .getNotification(inputForm)
                .flatMap(new Func1<ApiResponse, Single<NotificationContainer>>() {
                    @Override
                    public Single<NotificationContainer> call(ApiResponse apiResponse) {
                        return Single.just(new Gson().fromJson(apiResponse.getResult(), NotificationContainer.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<NotificationsWrapper> getMentionsNotifications() {
        NotificationsGetInputForm inputForm = new NotificationsGetInputForm();
        inputForm.setCheckIgnoreMention(true);
        inputForm.setCheckNew(true);
        inputForm.setMentioned(true);
        return getNotifications(inputForm);
    }

    @Override
    public Single<NotificationsWrapper> getAllNotifications() {
        NotificationsGetInputForm inputForm = new NotificationsGetInputForm();
        inputForm.setCheckIgnoreMention(true);
        inputForm.setCheckNew(true);
        return getNotifications(inputForm);
    }

    @Override
    public Single<NotificationsWrapper> getFlagNotifications() {
        NotificationsGetInputForm inputForm = new NotificationsGetInputForm();
        inputForm.setCheckIgnoreMention(true);
        inputForm.setCheckNew(true);
        inputForm.setFlagged(true);
        return getNotifications(inputForm);
    }

    private Single<NotificationsWrapper> getNotifications(NotificationsGetInputForm inputForm) {
        OkHttpClient okHttpClient = HigashiApplication.getInstance().getOkHttpClient();
        Account account = AccountStore.loadAccount();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(account.getBasicDomainUriBuilder().toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        NotificationService notificationService = retrofit.create(NotificationService.class);
        return notificationService
                .getNotifications(inputForm)
                .flatMap(new Func1<ApiResponse, Single<NotificationsWrapper>>() {
                    @Override
                    public Single<NotificationsWrapper> call(ApiResponse apiResponse) {
                        return Single.just(new Gson().fromJson(apiResponse.getResult(), NotificationsWrapper.class));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
