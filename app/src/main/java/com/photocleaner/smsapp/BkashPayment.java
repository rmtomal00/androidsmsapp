package com.photocleaner.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BkashPayment {

    Gson gson = new Gson();

    private String amount, invoice;

    private Context context;

    public BkashPayment(String totalPrice, String invoice, Context context) {
        this.amount = totalPrice;
        this.invoice = invoice;
        this.context = context;
    }

    public void GenerateToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://tokenized.pay.bka.sh/v1.2.0-beta/tokenized/checkout/token/grant")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
        //System.out.printf(response.body().string());


        //System.out.printf(modelClass.getId_token());

    }

    private void createPayment(String idToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        PaymentDataModel paymentDataModel = new PaymentDataModel("0011",
                "Pay Now",
                "https://www.google.com",
                amount,
                "BDT",
                "sale",
                invoice);
        String paymentData = gson.toJson(paymentDataModel);
        System.out.println(paymentData);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, paymentData);
        Request request = new Request.Builder()
                .url("https://tokenized.pay.bka.sh/v1.2.0-beta/tokenized/checkout/create")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //System.out.println(response.body().string());
                ModelClass modelClass = gson.fromJson(response.body().string(), ModelClass.class);

                System.out.println(modelClass.getBkashURL());
                System.out.println(modelClass.getPaymentID());
                if (modelClass.getBkashURL() != null){
                    Intent intent = new Intent(context, WebViewUrl.class);
                    intent.putExtra("URL", modelClass.getBkashURL());
                    intent.putExtra("paymentID", modelClass.getPaymentID());
                    intent.putExtra("token", idToken);
                    context.startActivity(intent);
                }
            }
        });

    }
}
