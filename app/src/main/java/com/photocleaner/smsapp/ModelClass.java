package com.photocleaner.smsapp;

import androidx.annotation.Keep;

@Keep
public class ModelClass {
    private String id_token;
    private String statusMessage;
    private String statusCode;
    private String paymentID;
    private String bkashURL;
    private String callbackURL;
    private String successCallbackURL;
    private String cancelledCallbackURL;

    public ModelClass(String id_token, String statusMessage, String statusCode, String paymentID, String bkashURL, String callbackURL, String successCallbackURL, String cancelledCallbackURL) {
        this.id_token = id_token;
        this.statusMessage = statusMessage;
        this.statusCode = statusCode;
        this.paymentID = paymentID;
        this.bkashURL = bkashURL;
        this.callbackURL = callbackURL;
        this.successCallbackURL = successCallbackURL;
        this.cancelledCallbackURL = cancelledCallbackURL;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getBkashURL() {
        return bkashURL;
    }

    public void setBkashURL(String bkashURL) {
        this.bkashURL = bkashURL;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getSuccessCallbackURL() {
        return successCallbackURL;
    }

    public void setSuccessCallbackURL(String successCallbackURL) {
        this.successCallbackURL = successCallbackURL;
    }

    public String getCancelledCallbackURL() {
        return cancelledCallbackURL;
    }

    public void setCancelledCallbackURL(String cancelledCallbackURL) {
        this.cancelledCallbackURL = cancelledCallbackURL;
    }
}
