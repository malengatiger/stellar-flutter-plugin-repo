package com.boha.stellarplugin.listeners;

import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import java.util.List;

public interface GetPaymentsMadeListener {
    void onPaymentsMade(List<PaymentOperationResponse> responses);
    void onError(String message);
}
