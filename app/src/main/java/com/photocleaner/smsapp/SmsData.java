package com.photocleaner.smsapp;

import androidx.annotation.Keep;

@Keep
public class SmsData {
    String phone, text, time, status;

    public SmsData() {
    }

    public SmsData(String phone, String text, String time, String status) {
        this.phone = phone;
        this.text = text;
        this.time = time;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
