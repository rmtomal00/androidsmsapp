package com.photocleaner.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BkashPayment {

    Gson gson = new Gson();
    private final String apiUrl = BuildConfig.apiUrl;

    private String amount, invoice;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private Context context;

    public BkashPayment(String totalPrice, String invoice, Context context) {
        this.amount = totalPrice;
        this.invoice = invoice;
        this.context = context;
    }

    public void GenerateToken() {
        Request request = new Request.Builder()
                .url(apiUrl+"/bkashApi/generateToken")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.getMessage());
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);
                String token;
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(res);
                    System.out.println(jsonObject.get("idToken"));
                    token = jsonObject.get("idToken").toString();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                createPayment(token);

            }
        });
        //System.out.printf(response.body().string());


        //System.out.printf(modelClass.getId_token());

    }

    private void createPayment(String idToken) throws IOException {

        String jsonString = "{\"idToken\":\""+idToken+"\", \"amount\":\""+amount+"\", \"invoice\":\""+invoice+"\"}";

        RequestBody requestBody = RequestBody.create(jsonString, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(apiUrl+"/bkashApi/createPayment")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);
                String bkashURL = null, paymentID = null, token = null;
                try {
                    JSONObject object = new JSONObject(res);
                    if (object.get("statusCode").equals("0000")){
                        bkashURL = object.get("bkashURL").toString();
                        paymentID = object.get("paymentID").toString();
                        token = object.get("idToken").toString();
                    }else {
                        System.out.println("api data error " + object.get("message"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (bkashURL != null){
                    Intent intent = new Intent(context, WebViewUrl.class);
                    intent.putExtra("URL", bkashURL);
                    intent.putExtra("paymentID", paymentID);
                    intent.putExtra("token", token);
                    context.startActivity(intent);
                }

            }
        });

    }
}
