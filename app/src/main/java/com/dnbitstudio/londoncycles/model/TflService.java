package com.dnbitstudio.londoncycles.model;

import com.google.gson.JsonArray;

import com.dnbitstudio.londoncycles.BuildConfig;
import com.dnbitstudio.londoncycles.network.TflApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TflService {

    private static final String BASE_URL = "https://api.tfl.gov.uk/";
    private final TflApi mTflApi;

    public TflService(String mockedJson) {
        Retrofit.Builder builder;
        if (BuildConfig.DEBUG && !"".equals(mockedJson)) {
            builder = createDebugMockedRetrofitBuilder(mockedJson);
        } else {
            builder = new Retrofit.Builder();
        }

        Retrofit retrofit = builder.baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mTflApi = retrofit.create(TflApi.class);
    }

    public void loadBikePoints(String appId, String appKey, Callback<JsonArray> callback) {
        mTflApi.loadBikePoints(appId, appKey).enqueue(callback);
    }

    private Retrofit.Builder createDebugMockedRetrofitBuilder(String mockedJson) {
        MockedInterceptor mockedInterceptor = new MockedInterceptor(mockedJson);
        final OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .addInterceptor(mockedInterceptor)
                .build();

        return new Retrofit.Builder().client(client);
    }

    private class MockedInterceptor implements Interceptor {
        private String mMockedJson;

        MockedInterceptor(String mockedJson) {
            mMockedJson = mockedJson;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = new Response.Builder()
                    .code(200)
                    .message(mMockedJson)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"),
                            mMockedJson.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();

            return response;
        }
    }
}
