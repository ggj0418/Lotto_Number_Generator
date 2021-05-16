package com.mepus.productiontest.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {
    private static final String BASE_URI = "https://www.dhlottery.co.kr";

    public RetrofitAdapter() {
    }

    private static class Singleton {
        private static final RetrofitAdapter instance = new RetrofitAdapter();
    }

    public static RetrofitAdapter getInstance() {
        return Singleton.instance;
    }

    public RetrofitService getServiceApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URI)
                .build();
        return retrofit.create(RetrofitService.class);
    }
}
