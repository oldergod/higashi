package com.oldering.kintone.higashi.api.kintone;

import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationFlagInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationGetInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationLikeInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationMarkInputForm;
import com.oldering.kintone.higashi.api.kintone.inputForm.NotificationsGetInputForm;
import com.oldering.kintone.higashi.model.ApiResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Single;

/**
 * @overview
 */

public interface NotificationService {
    @POST("somestuff.json")
    Single<ApiResponse> getNotifications(@Body NotificationsGetInputForm params);

    @POST("somestuff.json")
    Single<ApiResponse> getNotification(@Body NotificationGetInputForm params);

    @POST("somestuff.json")
    Single<ApiResponse> likeNotification(@Body NotificationLikeInputForm params);

    @POST("somestuff.json")
    Single<ApiResponse> flagNotification(@Body NotificationFlagInputForm params);

    @POST("somestuff.json")
    Single<ApiResponse> markNotification(@Body NotificationMarkInputForm params);
}
