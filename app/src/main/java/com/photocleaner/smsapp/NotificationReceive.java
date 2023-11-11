package com.photocleaner.smsapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationReceive extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title");
        String text = sbn.getNotification().extras.getString("android.text");

        System.out.println("app : " +packageName + " title : "+ title + " text : "+ text);
    }
}
