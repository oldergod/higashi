package com.oldering.kintone.higashi.ui.viewmodel;

import android.support.annotation.NonNull;

import com.oldering.kintone.higashi.datamodel.INotificationModel;
import com.oldering.kintone.higashi.model.NotificationContainer;
import com.oldering.kintone.higashi.model.NotificationsWrapper;

import rx.Single;

public class NotificationViewModel {
    @NonNull
    private final INotificationModel notificationModel;

    public NotificationViewModel(@NonNull INotificationModel dataModel) {
        notificationModel = dataModel;
    }

    @NonNull
    public Single<NotificationContainer> getNotification(long notificationId) {
        return notificationModel.getNotification(notificationId);
    }

    @NonNull
    public Single<NotificationsWrapper> getAllNotifications() {
        return notificationModel.getAllNotifications();
    }

    @NonNull
    public Single<NotificationsWrapper> getFlagNotifications() {
        return notificationModel.getFlagNotifications();
    }

    @NonNull
    public Single<NotificationsWrapper> getMentionNotifications() {
        return notificationModel.getMentionsNotifications();
    }
}
