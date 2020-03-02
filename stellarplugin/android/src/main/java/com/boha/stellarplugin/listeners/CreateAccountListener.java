package com.boha.stellarplugin.listeners;

import org.stellar.sdk.responses.AccountResponse;

public interface CreateAccountListener {
    void onAccountCreated(AccountResponse accountResponse);
    void onError(String message);
}
