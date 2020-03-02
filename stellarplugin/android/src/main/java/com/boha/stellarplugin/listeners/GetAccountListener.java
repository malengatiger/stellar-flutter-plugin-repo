package com.boha.stellarplugin.listeners;

import org.stellar.sdk.responses.AccountResponse;

public interface GetAccountListener {
    void onAccountResponse(AccountResponse response);
    void onError(String message);
}
