package com.photocleaner.smsapp;

import androidx.annotation.Keep;

@Keep
public class SmsReportData {
    private boolean error;
    private String error_message, message_id, message;
    private int error_code;

    public SmsReportData(boolean error, String error_message, String message_id, String message, int error_code) {
        this.error = error;
        this.error_message = error_message;
        this.message_id = message_id;
        this.message = message;
        this.error_code = error_code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
