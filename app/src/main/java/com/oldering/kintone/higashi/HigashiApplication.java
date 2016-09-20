package com.oldering.kintone.higashi;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.oldering.kintone.higashi.datamodel.INotificationModel;
import com.oldering.kintone.higashi.datamodel.NotificationModel;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

public class HigashiApplication extends Application {
    private static volatile HigashiApplication instance;

    @Getter
    @Setter
    private OkHttpClient okHttpClient;
    private NotificationModel notificationModel;

    @Override
    public void onCreate() {
        super.onCreate();
        // java.time.* package.for java 6/7
        AndroidThreeTen.init(this);

        instance = this;

        // TODO(benoit) how about loggin in here ?
        // but what about the activity that would wait for the loggin in to finish?
        // should add something on all Activity's resume or create ?

        notificationModel = new NotificationModel();
    }

    public static HigashiApplication getInstance() {
        return instance;
    }

    public INotificationModel getNotificationModel() {
        return notificationModel;
    }
}
