package com.boha.stellarplugin;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.stellar.sdk.AccountMergeOperation;
import org.stellar.sdk.AllowTrustOperation;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.BumpSequenceOperation;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.CreatePassiveSellOfferOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageBuyOfferOperation;
import org.stellar.sdk.ManageDataOperation;
import org.stellar.sdk.ManageSellOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;
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

import io.flutter.plugin.common.MethodChannel;

class StellarOps {

    private static final Logger LOGGER = Logger.getLogger(StellarOps.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEV_SERVER = "https://horizon-testnet.stellar.org";
    private static final String PROD_SERVER = "https://horizon.stellar.org'";
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s",
            LUMENS = "lumens";
    private static final String TAG = StellarOps.class.getSimpleName();
    private static final int TIMEOUT_IN_SECONDS = 180;
    private static boolean isDevelopment;
    private Server server;
    private Network network;

    //api methods
    void sendPayment(final String seed, final String destinationAccount,
                     final String amount, final String memo, boolean isDevelopmentStatus, final MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        Log.d(TAG,"\uD83D\uDC99 Payment about to be sent, amount: " + amount + " seed: " + seed
                + " \uD83D\uDC99 destination account: " + destinationAccount + " \uD83D\uDC99 memo: " + memo);
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doPayment(seed, destinationAccount, amount, memo);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account creation failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void createAccount(final boolean isDevelopmentStatus, final MethodChannel.Result result) {

        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    AccountResponseBag bag = doAccount();
                    mainThreadResult.success(G.toJson(bag));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account creation failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void getAccount(final String seed, final boolean isDevelopmentStatus, final MethodChannel.Result result) {

        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    AccountResponse accountResponse = getAccountFromStellar(seed);
                    mainThreadResult.success(G.toJson(accountResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void manageBuyOffer(final String seed, String sellingAssetJson,
                        String buyingAssetJson, final String amount, final String price, final Long offerId,
                        final MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final Asset selling = G.fromJson(sellingAssetJson, Asset.class);
        final Asset buying = G.fromJson(buyingAssetJson, Asset.class);
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse accountResponse = doManageBuyOffer(seed,selling,buying,amount,price,offerId);
                    mainThreadResult.success(G.toJson(accountResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void manageSellOffer(final String seed, String sellingAssetJson, String buyingAssetJson, final String amount,
                         final String price, final Long offerId,
                         MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final Asset selling = G.fromJson(sellingAssetJson, Asset.class);
        final Asset buying = G.fromJson(buyingAssetJson, Asset.class);
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doManageSellOffer(seed,selling,buying,amount,price,offerId);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void allowTrust(final String seed, final String trustor, final String assetCode, final boolean authorize, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doAllowTrustOperation(seed,trustor,assetCode,authorize);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void changeTrust( final String seed, String assetJson, final String limit, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final Asset asset = G.fromJson(assetJson, Asset.class);
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doChangeTrustOperation(seed,asset,limit);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void mergeAccounts(final String seed, final String destinationAccount, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doMergeAccounts(seed,destinationAccount);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void setOptions( final String seed, final int clearFlags, final int highThreshold, final int lowThreshold,
                     final String inflationDestination, final int masterKeyWeight, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doSetOptions(
                            seed,clearFlags,highThreshold,lowThreshold,inflationDestination,masterKeyWeight);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void bumpSequence(final String seed, final Long bumpTo, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doBumpSequence(seed,bumpTo);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void createPassiveOffer(final String seed, String sellingAssetJson, String buyingAssetJson,
                            final String amount, final String price, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final Asset selling = G.fromJson(sellingAssetJson, Asset.class);
        final Asset buying = G.fromJson(buyingAssetJson, Asset.class);
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doCreatePassiveSellOffer(seed,selling,buying,amount,price);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void manageData(final String seed, final String name, final String value, MethodChannel.Result result, boolean isDevelopmentStatus) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SubmitTransactionResponse transactionResponse = doManageData(seed,name,value);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("Account retrieval failed",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }
    void getPaymentsReceived(final String seed, boolean isDevelopmentStatus, MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<PaymentOperationResponse> transactionResponse = doGetPaymentsReceived(seed);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("getPaymentsReceived",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }

    void getPaymentsMade(final String seed, boolean isDevelopmentStatus, MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        final MainThreadResult mainThreadResult = new MainThreadResult(result);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<PaymentOperationResponse> transactionResponse = doGetPaymentsMade(seed);
                    mainThreadResult.success(G.toJson(transactionResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadResult.error("getPaymentsMade",
                            "Something went wrong","You say, Jose?");
                }
            }
        };
        new Thread(runnable).start();
    }


    //implementation methods
    private AccountResponseBag doAccount() throws Exception {
        setServerAndNetwork();
        AccountResponse accountResponse;
        try {
            InputStream response;
            KeyPair pair = KeyPair.random();
            String secret = new String(pair.getSecretSeed());

            if (isDevelopment) {
                String friendBotUrl = String.format(FRIEND_BOT, pair.getAccountId());
                response = new URL(friendBotUrl).openStream();
                String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
                LOGGER.info("\uD83E\uDD6C " +
                        "FriendBot largess: 10K Lumens. Yebo, Gogo!! )");
                server = new Server(DEV_SERVER);
            } else {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 ...... looks like we are in PRODUCTION ..." +
                        "Toto, we are not in Kansas anymore ... ");
                server = new Server(PROD_SERVER);
            }

            accountResponse = server.accounts().account(pair.getAccountId());
            LOGGER.info("\uD83D\uDC99  " +
                    "Stellar account has been created Kool!: \uD83C\uDF4E \uD83C\uDF4E YEBOOOO!!!");
            AccountResponseBag bag = new AccountResponseBag(secret, accountResponse);
            return bag;
        } catch (IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }
    private AccountResponse getAccountFromStellar(String seed) throws IOException {
        setServerAndNetwork();
        KeyPair source = KeyPair.fromSecretSeed(seed);
        String accountId = source.getAccountId();
        Log.d(TAG, "getAccount: AccountID from seed, accountId derived: " + accountId);
        AccountResponse resp = server.accounts().account(accountId);
        Log.d(TAG, resp == null ? "Account not found" : "Account found: " + resp.getAccountId());
        return resp;
    }
    private SubmitTransactionResponse doPayment(String seed, String destinationAccount,
                                                String amount, String memo) throws Exception {
        setServerAndNetwork();
        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
        KeyPair destinationKeyPair = KeyPair.fromAccountId(destinationAccount);

        AccountResponse destAccount = server.accounts().account(destinationKeyPair.getAccountId());
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(new PaymentOperation.Builder(destAccount.getAccountId(),
                        new AssetTypeNative(), amount).build())
                .addMemo(Memo.text(memo == null ? "N/A" : memo))
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(sourceKeyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            Log.d(TAG, response.isSuccess() ? "\uD83C\uDF4E Payment transaction is SUCCESSFUL \uD83C\uDF4E " + amount :
                    "\uD83D\uDC7F Payment transaction failed \uD83D\uDC7F ");
            return response;
        } catch (Exception e) {
            String msg = "Failed to submit transaction: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }
    private List<PaymentOperationResponse> doGetPaymentsReceived(final String seed) throws Exception {
        setServerAndNetwork();

        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(keyPair.getAccountId());
        final List<PaymentOperationResponse> paymentOperationResponses = new ArrayList<>();
        try {
            Page<OperationResponse> responsePage = paymentsRequest
                    .limit(PAGE_LIMIT)
                    .execute();
            List<OperationResponse> responses = responsePage.getRecords();
            for (OperationResponse operationResponse : responses) {
                if (operationResponse instanceof PaymentOperationResponse) {
                    if (((PaymentOperationResponse) operationResponse).getTo().equals(keyPair.getAccountId())) {
                        PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse) operationResponse;
                        paymentOperationResponses.add(paymentOperationResponse);
                        Log.d(TAG, "Payment made: " + paymentOperationResponse.getAmount()
                                + " for keyPair: " + keyPair.getAccountId());
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.severe("getPaymentsReceived:Failed to get payments: " + e.getMessage());
            throw e;
        }

        Log.d(TAG, "getPaymentsReceived: \uD83E\uDD6C found " + paymentOperationResponses.size()
                + " payments received by " + keyPair.getAccountId());
        return paymentOperationResponses;
    }

    private static final int PAGE_LIMIT = 200;
    private List<PaymentOperationResponse> doGetPaymentsMade(final String seed) throws Exception {
        setServerAndNetwork();
        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(keyPair.getAccountId());

        final List<PaymentOperationResponse> paymentOperationResponses = new ArrayList<>();
        try {
            Page<OperationResponse> responsePage = paymentsRequest
                    .limit(PAGE_LIMIT)
                    .execute();
            List<OperationResponse> responses = responsePage.getRecords();
            for (OperationResponse operationResponse : responses) {
                if (operationResponse instanceof PaymentOperationResponse) {
                    if (((PaymentOperationResponse) operationResponse).getFrom().equals(keyPair.getAccountId())) {
                        PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse) operationResponse;
                        paymentOperationResponses.add(paymentOperationResponse);
                        Log.d(TAG, "Payment received: " + paymentOperationResponse.getAmount()
                                + " for keyPair: " + keyPair.getAccountId());
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to get payments received: " + e.getMessage());
            throw e;
        }

        Log.d(TAG, "getPaymentsMade: \uD83E\uDD6C found " + paymentOperationResponses.size()
                + " payments made by " + keyPair.getAccountId());
        return paymentOperationResponses;
    }

    private SubmitTransactionResponse doManageSellOffer(final String seed, Asset selling, Asset buying, String amount, String price, Long offerId) throws Exception {
        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        ManageSellOfferOperation operation = new ManageSellOfferOperation.Builder(selling, buying, amount, price)
                .setOfferId(offerId)
                .setSourceAccount(keyPair.getAccountId())
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("manageSellOffer: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "ManageSellOfferOperation transaction is SUCCESSFUL" : "ManageSellOfferOperation transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to manageSellOffer: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }

    }

    private SubmitTransactionResponse doManageBuyOffer(final String seed, Asset selling, Asset buying, String amount, String price, Long offerId) throws Exception {
        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        ManageBuyOfferOperation operation = new ManageBuyOfferOperation.Builder(selling, buying, amount, price)
                .setOfferId(offerId)
                .setSourceAccount(keyPair.getAccountId())
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("manageBuyOffer: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "ManageBuyOfferOperation transaction is SUCCESSFUL" : "ManageBuyOfferOperation transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to manageBuyOffer: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doCreatePassiveSellOffer(final String seed, Asset selling, Asset buying, String amount, String price) throws Exception {

        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        CreatePassiveSellOfferOperation operation = new CreatePassiveSellOfferOperation.Builder(selling, buying, amount, price)
                .setSourceAccount(keyPair.getAccountId())
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("createPassiveSellOffer: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "CreatePassiveSellOfferOperation transaction is SUCCESSFUL" : "CreatePassiveSellOfferOperation transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to createPassiveSellOffer: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doSetOptions(final String seed, int clearFlags, int highThreshold, int lowThreshold,
                                         String inflationDestination, int masterKeyWeight) throws Exception {

        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        SetOptionsOperation operation = new SetOptionsOperation.Builder()
                .setClearFlags(clearFlags)
                .setHighThreshold(highThreshold)
                .setLowThreshold(lowThreshold)
                .setInflationDestination(inflationDestination)
                .setSourceAccount(keyPair.getAccountId())
                .setMasterKeyWeight(masterKeyWeight)
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("setOptions: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            Log.d(TAG, response.isSuccess() ? "setOptions transaction is SUCCESSFUL" : "setOptions transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to setOptions: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doAllowTrustOperation(final String seed, String trustor, String assetCode, boolean authorize) throws Exception {
        setServerAndNetwork();

        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        AllowTrustOperation operation = new AllowTrustOperation.Builder(trustor, assetCode, authorize)
                .setSourceAccount(keyPair.getAccountId())
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("allowTrustOperation: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            Log.d(TAG, response.isSuccess() ? "allowTrustOperation transaction is SUCCESSFUL" : "allowTrustOperation transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to allowTrustOperation: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doChangeTrustOperation(final String seed, Asset asset, String limit) throws Exception {
        setServerAndNetwork();

        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        ChangeTrustOperation operation = new ChangeTrustOperation.Builder(asset, limit)
                .setSourceAccount(keyPair.getAccountId())
                .build();

        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("allowTrustOperation: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "allowTrustOperation transaction is SUCCESSFUL" : "allowTrustOperation transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to allowTrustOperation: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doMergeAccounts(final String seed, String destinationAccount) throws Exception {
        setServerAndNetwork();

        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        AccountMergeOperation operation = new AccountMergeOperation.Builder(destinationAccount)
                .setSourceAccount(keyPair.getAccountId())
                .build();
        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("mergeAccounts: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "mergeAccounts transaction is SUCCESSFUL" : "mergeAccounts transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to mergeAccounts: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doManageData(final String seed, String name, String value) throws Exception {
        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());

        ManageDataOperation operation = new ManageDataOperation.Builder(name, value.getBytes())
                .setSourceAccount(keyPair.getAccountId())
                .build();
        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("manageData: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "manageData transaction is SUCCESSFUL" : "manageData transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to manageData: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    private SubmitTransactionResponse doBumpSequence(final String seed, Long bumpTo) throws Exception {
        setServerAndNetwork();
        KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());

        BumpSequenceOperation operation = new BumpSequenceOperation.Builder(bumpTo)
                .setSourceAccount(keyPair.getAccountId())
                .build();
        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("bumpSequence: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
            LOGGER.info(G.toJson(response));
            Log.d(TAG, response.isSuccess() ? "bumpSequence transaction is SUCCESSFUL" : "bumpSequence transaction failed");
            return response;
        } catch (Exception e) {
            String msg = "Failed to bumpSequence ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }


    private void setServerAndNetwork() {

        if (isDevelopment) {
            Log.d(TAG, "\uD83C\uDF4F \uD83C\uDF4F Setting up Stellar Testnet Server ...");
            server = new Server(DEV_SERVER);
            network = Network.TESTNET;
        } else {
            Log.d(TAG, "\uD83C\uDF4F \uD83C\uDF4F Setting up Stellar Public Server ...");
            server = new Server(PROD_SERVER);
            network = Network.PUBLIC;
        }

    }

    private static class MainThreadResult implements MethodChannel.Result {
        private MethodChannel.Result result;
        private Handler handler;

        MainThreadResult(MethodChannel.Result result) {
            this.result = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object object) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                           result.success(object);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            result.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            result.notImplemented();
                        }
                    });
        }
    }
}
