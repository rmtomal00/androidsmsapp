package com.photocleaner.smsapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdsFragmrnt extends Fragment {

    TextView balance;

    RewardedAd rewardedAd;
    RewardedInterstitialAd rewardedInterstitialAd;

    Button rewards, interstitial, pay;

    EditText amount;

    ProgressDialog progressDialog;

    CountDownTimer count;
    double smsBalance = 0.0;

    String total_price;


    Context context;
    Activity activity;

    public AdsFragmrnt() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ads_fragmrnt, container, false);
        balance = view.findViewById(R.id.sms_balance);
        rewards = view.findViewById(R.id.rewardAds);
        interstitial = view.findViewById(R.id.ads);
        pay = view.findViewById(R.id.pay_button);
        amount = view.findViewById(R.id.amount_SMS);

        context = getContext();
        activity = getActivity();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Data is Loading...");
        progressDialog.show();


        AsyncTask<Void, Void, Void> loadads = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                readUserData(user.getUid());

                return null;
            }
        };
        loadads.execute();
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    String aamount = charSequence.toString();
                    if (!TextUtils.isEmpty(aamount)){
                        if (Integer.parseInt(aamount) > 12000){
                            amount.setError("Maximum Value 12000");
                        }else{
                            double price = Double.parseDouble(String.valueOf(charSequence)) * 0.25;
                            total_price = String.format(Locale.getDefault(), "%.2f", price);
                            String total_price = String.format(Locale.getDefault(), "PAY NOW WITH BKASH: %.2f", price);
                            pay.setText(total_price);
                        }
                    }else {
                        String str2 = String.format(Locale.getDefault(), "PAY NOW WITH BKASH: %.1f", 0.0);
                        pay.setText(str2);
                    }

                }else {
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String check = amount.getText().toString().trim();
                if (TextUtils.isEmpty(check)){
                    amount.setError("Empty");
                    progressDialog.dismiss();
                    return;
                } else if (Integer.parseInt(check) < 4){
                    amount.setError("Minimum Value 4");
                    progressDialog.dismiss();
                    return;
                }else if (Integer.parseInt(check) > 12000){
                    amount.setError("Maximum Value 12000");
                    progressDialog.dismiss();
                    return;
                }
                Random random = new Random();
                int invoice = random.nextInt(9999);
                BkashPayment payement = new BkashPayment(total_price, String.valueOf(invoice),getContext());
                try {
                    payement.GenerateToken();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        interstitial.setOnClickListener(v->{
            if (rewardedInterstitialAd != null){
                rewardedInterstitialAd.show(getActivity(), new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        smsBalance = smsBalance + 0.1;
                        new DataSend().newBalance(String.valueOf(smsBalance));
                        System.out.println(rewardItem.getAmount());
                    }
                });
            }else {
                Toast.makeText(context, "Please wait 5 seconds to load ads", Toast.LENGTH_SHORT).show();
                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                        if (rewardedInterstitialAd == null){
                            loadAd();
                        }
                    }
                });
            }

        });
        rewards.setOnClickListener(view1 ->{
            if (rewardedAd != null){
                rewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        smsBalance = smsBalance + 0.125;
                        new DataSend().newBalance(String.valueOf(smsBalance));
                        System.out.println(rewardItem.getAmount());
                    }
                });
            }else {
                Toast.makeText(context, "Please wait 5 seconds to load ads", Toast.LENGTH_SHORT).show();
                loadRewardAds();
            }
        });


        return  view;
    }

    private void readUserData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> map = new HashMap<>();
                for (DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                    Object value = data.getValue();
                    map.put(key, value);
                }
                String balance1 = String.valueOf(map.get("balance"));
                smsBalance = Double.parseDouble(balance1);
                System.out.println("sms balance(current user): "+smsBalance);
                String str2 = String.format(Locale.getDefault(), "SMS Balance: %.3f", smsBalance);
                balance.setText(str2);
                progressDialog.dismiss();
                //Toast.makeText(context, "sms balance(current user): "+smsBalance, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });

    }

    private void loadRewardAds(){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                if (rewardedAd == null){
                    loadrewards();
                }
            }
        });
    }

    private void loadrewards() {

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, "ca-app-pub-7796006583390973/8671160185",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        //Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        loadrewards();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        Toast.makeText(context, "Rewarded ads loaded, Click now", Toast.LENGTH_SHORT).show();
                        rewardedAd = ad;
                        //Log.d(TAG, "Ad was loaded.");


                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                //Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                //Log.d(TAG, "Ad dismissed fullscreen content.");
                                rewardedAd = null;
                                loadrewards();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                //Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedAd = null;
                                loadrewards();
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                //Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                    }
                });
    }

    private void loadAd() {
        RewardedInterstitialAd.load(activity, "ca-app-pub-7796006583390973/9949432899",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        Toast.makeText(context, "Short ads loaded, Click now", Toast.LENGTH_SHORT).show();
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                //Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                //Log.d(TAG, "Ad dismissed fullscreen content.");
                                rewardedInterstitialAd = null;
                                loadAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                //Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedInterstitialAd = null;
                                loadAd();
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                //Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        //Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;

                    }
                });

    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }
}