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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentCheck extends AppCompatActivity {

    TextView textView;
    String s,k;

    private ProgressDialog progressDialog;
    double smsBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_check);
        textView = findViewById(R.id.listenerPayment);
        k = "Xo6qI5MycKaXAcjjhVH7Ay2ptc";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.create();
        progressDialog.show();

        Intent intent = getIntent();
        String paymentID = intent.getStringExtra("paymentID");
        System.out.println(paymentID);
        String token = intent.getStringExtra("token");
        System.out.println(token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        readUserData(user.getUid());

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{paymentID:" + paymentID + "}");
        Request request = new Request.Builder()
                .url("https://tokenized.pay.bka.sh/v1.2.0-beta/tokenized/checkout/execute")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //System.out.println(response.body().string());
                Gson g = new Gson();
                SuccessfullCheckerModel model = g.fromJson(response.body().string(), SuccessfullCheckerModel.class);
                if (model.getStatusCode().equals("0000")){

                    double newBalance =  smsBalance + (Double.parseDouble(model.getAmount()) / 0.25);
                    System.out.println(newBalance);
                    new DataSend().newBalance(String.valueOf(newBalance));
                     s = "Payment Status: " + model.getStatusMessage() + "\n"
                                + "Payment ID: " + model.getPaymentID() + "\n"
                                + "Customer Bkash: " + model.getCustomerMsisdn() + "\n"
                                + "TrxID : " + model.getTrxID() + "\n"
                                + "Payment Amount: " + model.getAmount() +" " + model.getCurrency() + "\n"
                                + "Transaction Status: " + model.getTransactionStatus() + "\n"
                                + "Payment Time: " + model.getPaymentExecuteTime() + "\n"
                                + "Payment Invoice: " + model.getMerchantInvoiceNumber() + "\n";

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             textView.setText(s);
                             textView.setTextColor(Color.GREEN);
                             progressDialog.dismiss();
                         }
                     });

                }else {
                    s = "Payment Status: " + model.getStatusMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(s);
                            textView.setTextColor(Color.RED);
                            progressDialog.dismiss();
                        }
                    });
                }
            }

        });



    }

    private void readUserData(String uid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> map = new HashMap<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String key = data.getKey();
                    Object value = data.getValue();
                    map.put(key, value);
                }
                String balance1 = String.valueOf(map.get("balance"));
                smsBalance = Double.parseDouble(balance1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }
}

