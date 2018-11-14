package com.rolandopalermo.facturacion.ec.web.services;

import java.util.Map;


import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ApiClient {

    public static SalesApi getSaleApi(String baseURL) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseURL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(getBasicClientInterceptor(true));
        return builder.build().create(SalesApi.class);
    }

    private static OkHttpClient getBasicClientInterceptor(boolean debug) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (debug){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(logging);
        }

        return builder.build();

    }

    public interface SalesApi {

        @POST("electronic-taxes/{documentId}/{companyId}/document")
        Call<ResponseBody> updateSaleDocuementState(
            @Path("documentId") int documentId, 
            @Path("companyId") int companyId, 
            @Body Map<String, Object> body);

    }

}