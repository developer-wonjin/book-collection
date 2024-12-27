package com.collection.book.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class HttpUtil {
    private static final OkHttpClient client = new OkHttpClient();

    // GET 요청 메서드
    public static String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        // 헤더 추가
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // POST 요청 메서드 (JSON Body)
    public static String post(String url, String jsonBody, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 헤더 추가
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // POST 요청 메서드 (Form Data)
    public static String postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();

        // Form Data 추가
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody body = formBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 헤더 추가
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}