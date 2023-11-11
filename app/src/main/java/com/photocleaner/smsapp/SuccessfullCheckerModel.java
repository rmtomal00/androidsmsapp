package com.photocleaner.smsapp;

import androidx.annotation.Keep;

@Keep
public class SuccessfullCheckerModel {
    private String statusCode, statusMessage, paymentID, payerReference, customerMsisdn, trxID,
            amount, transactionStatus, paymentExecuteTime, currency, intent, merchantInvoiceNumber;

    public SuccessfullCheckerModel(String statusCode, String statusMessage, String paymentID,
                                   String payerReference, String customerMsisdn, String trxID,
                                   String amount, String transactionStatus, String paymentExecuteTime,
                                   String currency, String intent, String merchantInvoiceNumber) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.paymentID = paymentID;
        this.payerReference = payerReference;
        this.customerMsisdn = customerMsisdn;
        this.trxID = trxID;
        this.amount = amount;
        this.transactionStatus = transactionStatus;
        this.paymentExecuteTime = paymentExecuteTime;
        this.currency = currency;
        this.intent = intent;
        this.merchantInvoiceNumber = merchantInvoiceNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public String getPayerReference() {
        return payerReference;
    }

    public String getCustomerMsisdn() {
        return customerMsisdn;
    }

    public String getTrxID() {
        return trxID;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public String getPaymentExecuteTime() {
        return paymentExecuteTime;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIntent() {
        return intent;
    }

    public String getMerchantInvoiceNumber() {
        return merchantInvoiceNumber;
    }
}
