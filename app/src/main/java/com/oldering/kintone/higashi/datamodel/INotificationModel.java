package com.oldering.kintone.higashi.datamodel;

import com.oldering.kintone.higashi.model.NotificationContainer;
import com.oldering.kintone.higashi.model.NotificationsWrapper;

import rx.Single;

public interface INotificationModel {
    Single<NotificationContainer> getNotification(long notificationId);

    Single<NotificationsWrapper> getMentionsNotifications();

    Single<NotificationsWrapper> getAllNotifications();

    Single<NotificationsWrapper> getFlagNotifications();
}
