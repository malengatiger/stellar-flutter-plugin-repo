package com.boha.stellarplugin;

import org.stellar.sdk.Account;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
import org.stellar.sdk.xdr.CreateAccountResult;

import java.io.IOException;
import java.util.List;

public interface BaseOperations {
    AccountResponse createAccount( boolean isDevelopment) throws Exception;
    AccountResponse getAccount(String seed, boolean isDevelopment) throws IOException;
    SubmitTransactionResponse sendPayment(String seed, String destinationAccount, String amount, String memo, boolean isDevelopment) throws Exception;
    List<PaymentOperationResponse> getPaymentsReceived(String accountId, boolean isDevelopment) throws Exception;
    List<PaymentOperationResponse> getPaymentsMade(String accountId, boolean isDevelopment) throws Exception;


}
