package com.photocleaner.smsapp;

import androidx.annotation.Keep;

@Keep
public class PaymentDataModel {
    private String mode, payerReference, callbackURL, amount, currency, intent, merchantInvoiceNumber;

    public PaymentDataModel(String mode, String payerReference, String callbackURL,
                            String amount, String currency, String intent, String merchantInvoiceNumber) {
        this.mode = mode;
        this.payerReference = payerReference;
        this.callbackURL = callbackURL;
        this.amount = amount;
        this.currency = currency;
        this.intent = intent;
        this.merchantInvoiceNumber = merchantInvoiceNumber;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPayerReference() {
        return payerReference;
    }

    public void setPayerReference(String payerReference) {
        this.payerReference = payerReference;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getMerchantInvoiceNumber() {
        return merchantInvoiceNumber;
    }

    public void setMerchantInvoiceNumber(String merchantInvoiceNumber) {
        this.merchantInvoiceNumber = merchantInvoiceNumber;
    }
}
