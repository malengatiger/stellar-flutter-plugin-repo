package com.boha.stellarplugin.listeners;

import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import java.util.List;

public interface GetPaymentsReceivedListener {
    void onPaymentsReceived(List<PaymentOperationResponse> responses);
    void onError(String message);
}
