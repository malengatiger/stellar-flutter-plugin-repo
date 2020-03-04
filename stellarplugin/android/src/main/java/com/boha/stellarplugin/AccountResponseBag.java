package com.boha.stellarplugin;

import org.stellar.sdk.responses.AccountResponse;

public class AccountResponseBag {
    final String secretSeed;
    final AccountResponse accountResponse;

    public AccountResponseBag(String secretSeed, AccountResponse accountResponse) {
        this.secretSeed = secretSeed;
        this.accountResponse = accountResponse;
    }

    public String getSecretSeed() {
        return secretSeed;
    }

    public AccountResponse getAccountResponse() {
        return accountResponse;
    }
}
