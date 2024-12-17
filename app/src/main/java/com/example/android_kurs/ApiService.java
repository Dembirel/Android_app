package com.example.android_kurs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("/api/signal-data")
    Call<Void> sendSignalData(@Body SignalData signalData);
}
