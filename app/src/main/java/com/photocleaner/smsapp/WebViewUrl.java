package com.photocleaner.smsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewUrl extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog progressDialog;
    String bkashUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.web);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.create();
        progressDialog.show();

        Intent intent = getIntent();
        bkashUrl = intent.getStringExtra("URL");
        String paymentId = intent.getStringExtra("paymentID");
        String token = intent.getStringExtra("token");
        System.out.println(bkashUrl);
        if (bkashUrl != null){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.loadUrl(bkashUrl);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (!url.equals(bkashUrl)){
                        //Toast.makeText(WebViewUrl.this, "work", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(WebViewUrl.this, PaymentCheck.class);
                        intent1.putExtra("paymentID", paymentId);
                        intent1.putExtra("token", token);
                        startActivity(intent1);
                        progressDialog.dismiss();
                        finish();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressDialog.dismiss();
                }
            });
        }



    }
}