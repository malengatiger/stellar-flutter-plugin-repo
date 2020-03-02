package com.boha.stellarplugin;

import android.os.AsyncTask;
import android.util.Log;

import com.boha.stellarplugin.listeners.CreateAccountListener;
import com.boha.stellarplugin.listeners.GetAccountListener;
import com.boha.stellarplugin.listeners.GetPaymentsMadeListener;
import com.boha.stellarplugin.listeners.GetPaymentsReceivedListener;
import com.boha.stellarplugin.listeners.SendPaymentListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.stellar.sdk.Account;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import shadow.com.google.common.base.Optional;

class StellarOperations {
    private static final Logger LOGGER = Logger.getLogger(StellarOperations.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEV_SERVER = "https://horizon-testnet.stellar.org";
    private static final String PROD_SERVER = "https://horizon-testnet.stellar.org";
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s", LUMENS = "lumens";
    private static final int TIMEOUT_IN_SECONDS = 180;

    private static boolean isDevelopment;

    void createAccount(boolean isDevelopmentStatus, CreateAccountListener createAccountListener) {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 " +
                ".... Creating new Stellar account ..... " +
                "\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 ");
        isDevelopment = isDevelopmentStatus;
        new StellarTask(createAccountListener).execute();

    }

    void getAccount(String seed, boolean isDevelopmentStatus, GetAccountListener listener) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(seed, listener).execute();
    }

    void sendPayment(String seed, String destinationAccount,
                     String amount, String memo, boolean isDevelopmentStatus, SendPaymentListener listener) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(seed, amount, destinationAccount, memo, listener).execute();
    }


    void getPaymentsReceived(String accountId, boolean isDevelopmentStatus, GetPaymentsReceivedListener listener) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(accountId, listener).execute();
    }

    void getPaymentsMade(String accountId, boolean isDevelopmentStatus, GetPaymentsMadeListener listener) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(accountId, listener).execute();
    }

    private void savePagingToken(String pagingToken) {

    }

    private String loadLastPagingToken(String pagingToken) {

        return null;
    }

    private static final int CREATE_ACCOUNT = 1, SEND_PAYMENT = 2, GET_PAYMENTS_RECEIVED = 3,
            GET_PAYMENTS_MADE = 4, GET_ACCOUNT = 5;

    private static class StellarTask extends AsyncTask<Void, Void, Object> {
        CreateAccountListener createAccountListener;
        GetAccountListener getAccountListener;
        GetPaymentsReceivedListener getPaymentsReceivedListener;
        SendPaymentListener sendPaymentListener;
        GetPaymentsMadeListener getPaymentsMadeListener;
        String seed, amount, destinationAccount, memo, accountId;
        private Server server;
        private int requestType;

        AccountResponse accountResponse;
        List<PaymentOperationResponse> paymentOperationResponses;
        SubmitTransactionResponse submitTransactionResponse;

        private void setServer(boolean isDevelopment) {
            Log.d(TAG, "............ setServer starting ...... " +
                    "\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 " + isDevelopment);
            if (isDevelopment) {
                server = new Server(DEV_SERVER);
            } else {
                server = new Server(PROD_SERVER);
            }
            Log.d(TAG, "\uD83C\uDF4F \uD83C\uDF4F \uD83C\uDF4F \uD83C\uDF4F " +
                    "Server is " + server.toString());
        }

        StellarTask(String seed, String amount, String destinationAccount, String memo, SendPaymentListener sendPaymentListener) {
            this.seed = seed;
            this.amount = amount;
            this.destinationAccount = destinationAccount;
            this.memo = memo;
            this.sendPaymentListener = sendPaymentListener;
            requestType = SEND_PAYMENT;
        }

        StellarTask(String accountId, GetPaymentsReceivedListener getPaymentsReceivedListener) {
            this.accountId = accountId;
            this.getPaymentsReceivedListener = getPaymentsReceivedListener;
            requestType = GET_PAYMENTS_RECEIVED;

        }

        StellarTask(String accountId, GetPaymentsMadeListener getPaymentsMadeListener) {
            this.accountId = accountId;
            this.getPaymentsMadeListener = getPaymentsMadeListener;
            requestType = GET_PAYMENTS_RECEIVED;

        }

        StellarTask(String accountId, GetAccountListener getAccountListener) {
            this.accountId = accountId;
            this.getAccountListener = getAccountListener;
            requestType = GET_ACCOUNT;

        }

        StellarTask(CreateAccountListener createAccountListener) {
            this.createAccountListener = createAccountListener;
            requestType = CREATE_ACCOUNT;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            Log.d(TAG, "\uD83C\uDF4E doInBackground \uD83C\uDF4E doInBackground " +
                    "\uD83C\uDF4E doInBackground \uD83C\uDF4E ............. ");
            Object returnedObject = null;
            switch (requestType) {
                case CREATE_ACCOUNT:
                    try {
                        accountResponse = createAccount();
                    } catch (Exception e) {
                        returnedObject =  e;
                    }
                    break;
                case SEND_PAYMENT:
                    try {
                        submitTransactionResponse = sendPayment(seed, destinationAccount, amount, memo);
                    } catch (Exception e) {
                        returnedObject =  e;
                    }
                    break;
                case GET_PAYMENTS_MADE:
                    try {
                        paymentOperationResponses = getPaymentsMade(accountId);
                    } catch (Exception e) {
                        returnedObject =  e;
                    }
                    break;
                case GET_PAYMENTS_RECEIVED:
                    try {
                        paymentOperationResponses = getPaymentsReceived(accountId);
                    } catch (Exception e) {
                        returnedObject =  e;
                    }
                    break;
                case GET_ACCOUNT:
                    try {
                        accountResponse = getAccount(seed);
                    } catch (Exception e) {
                        returnedObject =  e;
                    }
                    break;
                default:
                    Log.e(TAG, "........ There is no requestType found .... not good, bro?");
                    break;
            }


            return returnedObject;
        }

        AccountResponse createAccount() throws Exception {
            LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Creating new, like Stellar account ... " +
                    "\uD83D\uDD35 about to call KeyPair.random() ...... \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08  ");
            setServer(isDevelopment);
            AccountResponse accountResponse;
            try {
                InputStream response;
                KeyPair pair = KeyPair.random();
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Secret Seed: " + new String(pair.getSecretSeed()));
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 New Account Id: " + pair.getAccountId());
                if (isDevelopment) {
                    LOGGER.info("\n\uD83D\uDC99 \uD83D\uDC99 ...... begging FriendBot for Lumens ... .........");
                    String friendBotUrl = String.format(FRIEND_BOT, pair.getAccountId());
                    response = new URL(friendBotUrl).openStream();
                    String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
                    LOGGER.info("\n\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C  \uD83C\uDF4E " +
                            "SUCCESS! begging, Yebo, Gogo!! \uD83C\uDF4E body:)\n" + body);
                    server = new Server(DEV_SERVER);
                } else {
                    LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 ...... looks like we are in PRODUCTION ..." +
                            "Toto, we are not in Kansas anymore ... ");
                    server = new Server(PROD_SERVER);
                }

                accountResponse = server.accounts().account(pair.getAccountId());
                for (AccountResponse.Balance balance : accountResponse.getBalances()) {
                    LOGGER.info(String.format(
                            "\uD83C\uDF3F Type: %s, \uD83C\uDF3F Code: %s, \uD83C\uDF3F Balance: %s",
                            balance.getAssetType(),
                            balance.getAssetCode(),
                            balance.getBalance()));
                }
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 " +
                        "Stellar account has been created Alrighttt!:  \uD83C\uDF4E \uD83C\uDF4E YEBOOOO!!!"
                        + accountResponse.getAccountId() + " \uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 ");
                LOGGER.info(G.toJson(accountResponse));
                createAccountListener.onAccountCreated(accountResponse);
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Account created OK: account: " + accountResponse.getAccountId());
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Account created OK: balance: " + accountResponse.getBalances()[0].getBalance());
                return accountResponse;
            } catch (IOException e) {
                LOGGER.severe("Failed to create account - see below ...");
                throw new Exception("\uD83D\uDD34 Unable to create Account", e);
            }

        }

        AccountResponse getAccount(String seed) throws IOException {
            setServer(isDevelopment);
            KeyPair source = KeyPair.fromSecretSeed(seed);
            return server.accounts().account(source.getAccountId());
        }

        SubmitTransactionResponse sendPayment(String seed, String destinationAccount,
                                              String amount, String memo) throws Exception {
            setServer(isDevelopment);
            Network network;
            if (isDevelopment) {
                network = Network.TESTNET;
            } else {
                network = Network.PUBLIC;
            }
            KeyPair source = KeyPair.fromSecretSeed(seed);
            KeyPair destination = KeyPair.fromAccountId(destinationAccount);

            AccountResponse destAccount = server.accounts().account(destination.getAccountId());
            AccountResponse sourceAccount = server.accounts().account(source.getAccountId());
            Account account = new Account(source.getAccountId(), sourceAccount.getIncrementedSequenceNumber());

            Transaction transaction = new Transaction.Builder(account, network)
                    .addOperation(new PaymentOperation.Builder(destAccount.getAccountId(), new AssetTypeNative(), amount).build())
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
                    .setTimeout(TIMEOUT_IN_SECONDS)
                    .build();
            try {
                transaction.sign(source);
                SubmitTransactionResponse response = server.submitTransaction(transaction);
                LOGGER.info("SubmitTransactionResponse:  \uD83D\uDC99 Success? : " + response.isSuccess() + "  \uD83D\uDC99 ");
                LOGGER.info(G.toJson(response));
                return response;
            } catch (Exception e) {
                String msg = "Failed to submit transaction: ";
                LOGGER.severe(msg + e.getMessage());
                throw new Exception(msg, e);
            }
        }

        List<PaymentOperationResponse> getPaymentsReceived(String accountId) throws Exception {
            setServer(isDevelopment);

            final KeyPair account = KeyPair.fromAccountId(accountId);
            PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account.getAccountId());
//            String lastToken = loadLastPagingToken("");
//            if (lastToken != null) {
//                paymentsRequest.cursor(lastToken);
//            }
            final List<PaymentOperationResponse> paymentOperationResponses = new ArrayList<>();

            try {
                paymentsRequest.stream(new EventListener<OperationResponse>() {
                    @Override
                    public void onEvent(OperationResponse payment) {
                        //savePagingToken(payment.getPagingToken());
                        // The payments stream includes both sent and received payments. We only
                        // want to process received payments here.
                        if (payment instanceof PaymentOperationResponse) {
                            if (((PaymentOperationResponse) payment).getTo().equals(account.getAccountId())) {
                                return;
                            }
                            PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse) payment;
                            paymentOperationResponses.add(paymentOperationResponse);
                            String amount = ((PaymentOperationResponse) payment).getAmount();
                            Asset asset = ((PaymentOperationResponse) payment).getAsset();
                            String assetName;
                            if (asset.equals(new AssetTypeNative())) {
                                assetName = LUMENS;
                            } else {
                                StringBuffer assetNameBuilder = new StringBuffer();
                                assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
                                assetNameBuilder.append(":");
                                assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer());
                                assetName = assetNameBuilder.toString();
                                LOGGER.info("assetName: " + assetName);
                            }

                            StringBuffer builder = new StringBuffer();
                            builder.append(amount);
                            builder.append(" ");
                            builder.append(assetName);
                            builder.append(" from ");
                            builder.append(((PaymentOperationResponse) payment).getFrom());
                            LOGGER.info(builder.toString());
                        }

                    }

                    @Override
                    public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
                        try {
                            throw new Exception("onFailure happened on getting payments");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                LOGGER.severe("Failed to get payments: " + e.getMessage());
                throw e;
            }

            return paymentOperationResponses;
        }

        List<PaymentOperationResponse> getPaymentsMade(String accountId) throws Exception {
            setServer(isDevelopment);
            final KeyPair account = KeyPair.fromAccountId(accountId);
            PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account.getAccountId());
//            String lastToken = loadLastPagingToken("");
//            if (lastToken != null) {
//                paymentsRequest.cursor(lastToken);
//            }
            final List<PaymentOperationResponse> paymentOperationResponses = new ArrayList<>();

            try {
                paymentsRequest.stream(new EventListener<OperationResponse>() {
                    @Override
                    public void onEvent(OperationResponse payment) {
                        //savePagingToken(payment.getPagingToken());
                        // The payments stream includes both sent and received payments. We only
                        // want to process received payments here.
                        if (payment instanceof PaymentOperationResponse) {
                            if (!((PaymentOperationResponse) payment).getTo().equals(account.getAccountId())) {
                                return;
                            }
                            PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse) payment;
                            paymentOperationResponses.add(paymentOperationResponse);
                            String amount = ((PaymentOperationResponse) payment).getAmount();
                            Asset asset = ((PaymentOperationResponse) payment).getAsset();
                            String assetName;
                            if (asset.equals(new AssetTypeNative())) {
                                assetName = LUMENS;
                            } else {
                                StringBuffer assetNameBuilder = new StringBuffer();
                                assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
                                assetNameBuilder.append(":");
                                assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer());
                                assetName = assetNameBuilder.toString();
                                LOGGER.info("assetName: " + assetName);
                            }

                            StringBuffer builder = new StringBuffer();
                            builder.append(amount);
                            builder.append(" ");
                            builder.append(assetName);
                            builder.append(" from ");
                            builder.append(((PaymentOperationResponse) payment).getFrom());
                            LOGGER.info(builder.toString());
                        }

                    }

                    @Override
                    public void onFailure(Optional<Throwable> optional, Optional<Integer> optional1) {
                        try {
                            throw new Exception("onFailure happened on getting payments");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                LOGGER.severe("Failed to get payments: " + e.getMessage());
                throw e;
            }

            return paymentOperationResponses;
        }

        @Override
        protected void onPostExecute(Object result) {
            Log.d(TAG, " \uD83C\uDF4E  \uD83C\uDF4E  \uD83C\uDF4E  \uD83C\uDF4E " +
                    ".......... onPostExecute: StellarTask has completed ...  \uD83C\uDF4E probably OK?  \uD83C\uDF4E , maybe...... ");
            super.onPostExecute(result);
            if (result instanceof Exception) {
                switch (requestType) {
                    case CREATE_ACCOUNT:
                        String msg = "Create Account Failed";
                        Log.e(TAG, msg, (Exception) result);
                        createAccountListener.onError(msg);
                        break;
                    case SEND_PAYMENT:
                        String msg2 = "Send PaymentFailed";
                        Log.e(TAG, msg2, (Exception) result);
                        sendPaymentListener.onError(msg2);
                        break;
                    case GET_ACCOUNT:
                        String msg3 = "Get Account Failed";
                        Log.e(TAG, msg3, (Exception) result);
                        getAccountListener.onError(msg3);
                        break;
                    case GET_PAYMENTS_MADE:
                        String msg4 = "Get Payments Made Failed";
                        Log.e(TAG, msg4, (Exception) result);
                        getPaymentsMadeListener.onError(msg4);
                        break;
                    case GET_PAYMENTS_RECEIVED:
                        String msg5 = "Get Payments Received Failed";
                        Log.e(TAG, msg5, (Exception) result);
                        getPaymentsReceivedListener.onError(msg5);
                        break;
                }
            } else {
                switch (requestType) {
                    case CREATE_ACCOUNT:
                        createAccountListener.onAccountCreated(accountResponse);
                        break;
                    case SEND_PAYMENT:
                        sendPaymentListener.onPaymentSent(submitTransactionResponse);
                        break;
                    case GET_ACCOUNT:
                        getAccountListener.onAccountResponse(accountResponse);
                        break;
                    case GET_PAYMENTS_MADE:
                        getPaymentsMadeListener.onPaymentsMade(paymentOperationResponses);
                        break;
                    case GET_PAYMENTS_RECEIVED:
                        getPaymentsReceivedListener.onPaymentsReceived(paymentOperationResponses);
                        break;
                }
            }
        }
    }


    private static final String TAG = StellarOperations.class.getSimpleName();
}
