package com.oldering.kintone.higashi.api.slash;

import com.oldering.kintone.higashi.model.ApiResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Single;

interface authService {
    @POST("somestuff.json")
    Single<ApiResponse> getAccessToken();

    @POST("somestuff.json")
    Single<ApiResponse> login(@Body LoginInputForm form);
}