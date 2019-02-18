package com.laomei.embedded;

import okhttp3.*;

import java.io.IOException;

/**
 * @author laomei on 2018/10/24 15:02
 */
public class HttpUtil {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static void doPost(String url, String body) throws IOException {

        RequestBody requestBody = RequestBody.create(DEFAULT_MEDIA_TYPE, body);

        //create header
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("Content-Type", "application/json");
        Headers headers = headersBuilder.build();

        //create request
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        //exec request
        Response response = CLIENT.newCall(request).execute();
        response.close();
    }
}
