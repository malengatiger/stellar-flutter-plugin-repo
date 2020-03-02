package com.boha.stellarplugin.listeners;

import org.stellar.sdk.responses.SubmitTransactionResponse;

public interface SendPaymentListener {
    void onPaymentSent(SubmitTransactionResponse response);
    void onError(String message);
}
