package com.photocleaner.smsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentCheck extends AppCompatActivity {

    TextView textView;
    String s = "payment error",k;

    private String apiUrl = BuildConfig.apiUrl;

    private ProgressDialog progressDialog;

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    double smsBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_check);
        textView = findViewById(R.id.listenerPayment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.create();
        progressDialog.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = getIntent();
        String paymentID = intent.getStringExtra("paymentID");
        System.out.println(paymentID);
        String token = intent.getStringExtra("token");
        System.out.println(token);

        JSONObject json = new JSONObject();
        try {
            json.put("paymentID", paymentID);
            json.put("idToken", token);
            json.put("uid", uid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(String.valueOf(json), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .post(body)
                .url(apiUrl+"/bkashApi/execute")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(s);
                        textView.setTextColor(Color.GREEN);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);

                JSONObject object;
                try {
                    object = new JSONObject(res);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    s = object.get("statusMessage").toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(s);
                        textView.setTextColor(Color.GREEN);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}

