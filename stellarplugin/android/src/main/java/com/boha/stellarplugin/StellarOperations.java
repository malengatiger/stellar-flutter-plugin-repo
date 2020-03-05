package com.boha.stellarplugin;

import android.os.AsyncTask;
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
import org.stellar.sdk.xdr.AccountMergeResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import io.flutter.plugin.common.MethodChannel;

class StellarOperations {
    private static final Logger LOGGER = Logger.getLogger(StellarOperations.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEV_SERVER = "https://horizon-testnet.stellar.org";
    private static final String PROD_SERVER = "https://horizon.stellar.org'";
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s",
            LUMENS = "lumens";
    private static final int TIMEOUT_IN_SECONDS = 180;
    private static boolean isDevelopment;

    void createAccount(boolean isDevelopmentStatus, MethodChannel.Result result) {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 " +
                ".... Creating new Stellar account ..... " +
                "\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 ");
        isDevelopment = isDevelopmentStatus;
        new StellarTask(result).execute();

    }

    void getAccount(String seed, boolean isDevelopmentStatus, MethodChannel.Result result) {
        Log.d(TAG, "getAccount starting, isDevelopmentStatus:  "
                + isDevelopmentStatus + " seed: " + seed);
        isDevelopment = isDevelopmentStatus;
        new StellarTask(seed, result, false).execute();
    }

    void sendPayment(String seed, String destinationAccount,
                     String amount, String memo, boolean isDevelopmentStatus, MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(seed, amount, destinationAccount, memo, result).execute();
    }


    void getPaymentsReceived(String accountId, boolean isDevelopmentStatus, MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(accountId, result, true, true).execute();
    }

    void getPaymentsMade(String accountId, boolean isDevelopmentStatus, MethodChannel.Result result) {
        isDevelopment = isDevelopmentStatus;
        new StellarTask(accountId, result, "").execute();
    }

    private void savePagingToken(String pagingToken) {
    }

    private String loadLastPagingToken(String pagingToken) {

        return null;
    }

    private static final int PAGE_LIMIT = 200;

    private static class StellarTask extends AsyncTask<Void, Void, Object> {

        private String seed, amount, destinationAccount, memo, accountId, price,
                inflationDestination, trustor, assetCode, limit, name, value;
        private boolean authorize;
        private Server server;
        private Network network;
        private int requestType, clearFlags, lowThreshold, highThreshold, masterKeyWeight;
        private Asset selling, buying, asset;
        Long offerId, bumpTo;

        AccountResponse accountResponse;
        AccountResponseBag accountResponseBag;
        List<PaymentOperationResponse> paymentOperationResponses;
        SubmitTransactionResponse submitTransactionResponse;
        MethodChannel.Result methodResult;

        private void setServerAndNetwork(boolean isDevelopment) {
            Log.d(TAG, "............ setServer starting ...... " +
                    "\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 " + isDevelopment);
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

        StellarTask(String seed, String amount, String destinationAccount, String memo, MethodChannel.Result methodResult) {
            this.seed = seed;
            this.amount = amount;
            this.destinationAccount = destinationAccount;
            this.memo = memo;
            this.methodResult = methodResult;
            requestType = SEND_PAYMENT;
        }

        StellarTask(String accountId, MethodChannel.Result methodResult, boolean dummy, boolean dummy2) {
            this.accountId = accountId;
            this.methodResult = methodResult;
            requestType = GET_PAYMENTS_RECEIVED;

        }

        StellarTask(String accountId, MethodChannel.Result methodResult, String dummy) {
            this.accountId = accountId;
            requestType = GET_PAYMENTS_MADE;
            this.methodResult = methodResult;

        }

        StellarTask(String seed, MethodChannel.Result methodResult, boolean dummy) {
            this.seed = seed;
            this.methodResult = methodResult;
            requestType = GET_ACCOUNT;

        }

        StellarTask(MethodChannel.Result methodResult) {
            requestType = CREATE_ACCOUNT;
            this.methodResult = methodResult;
        }

        StellarTask( String seed, Asset selling, Asset buying, String amount, String price, Long offerId, boolean isBuyOffer) {
            this.seed = seed;
            this.selling = selling;
            this.buying = buying;
            this.amount = amount;
            this.price = price;
            this.offerId = offerId;
            requestType = isBuyOffer? MANAGE_BUY_OFFER: MANAGE_SELL_OFFER;
        }

        StellarTask(String seed, Asset selling, Asset buying, String amount, String price) {
            this.seed = seed;
            this.selling = selling;
            this.buying = buying;
            this.amount = amount;
            this.price = price;
            requestType = CREATE_PASSIVE_OFFER;
        }
        StellarTask(final String seed, int clearFlags, int highThreshold, int lowThreshold,
                    String inflationDestination, int masterKeyWeight) {
            this.seed = seed;
            this.clearFlags = clearFlags;
            this.highThreshold = highThreshold;
            this.lowThreshold = lowThreshold;
            this.inflationDestination = inflationDestination;
            this.masterKeyWeight = masterKeyWeight;
            requestType = SET_OPTIONS;
        }
        StellarTask(String seed, String trustor, String assetCode, boolean authorize) {
            this.seed = seed;
            this.trustor = trustor;
            this.assetCode = assetCode;
            this.authorize = authorize;
            requestType = ALLOW_TRUST_OPERATION;
        }
        StellarTask(String seed, Asset asset, String limit) {
            this.seed = seed;
            this.asset = asset;
            this.limit = limit;
            requestType = CHANGE_TRUST_OPERATION;
        }

        StellarTask(String seed, String destinationAccount) {
            this.seed = seed;
            this.destinationAccount = destinationAccount;
            requestType = MERGE_ACCOUNTS;
        }
        StellarTask(String seed, String name, String value) {
            this.seed = seed;
            this.name = name;
            this.value = value;
            requestType = MANAGE_DATA;
        }
        StellarTask(String seed, Long bumpTo) {
            this.seed = seed;
            this.bumpTo = bumpTo;
            requestType = BUMP_SEQUENCE;
        }


        @Override
        protected Object doInBackground(Void... voids) {
            Log.d(TAG, "\uD83C\uDF4E doInBackground \uD83C\uDF4E doInBackground " +
                    "\uD83C\uDF4E doInBackground \uD83C\uDF4E ............. ");
            Object returnedObject = null;
            try {
                switch (requestType) {
                    case CREATE_ACCOUNT:
                        accountResponseBag = createAccount();
                        break;
                    case SEND_PAYMENT:
                        submitTransactionResponse = sendPayment(seed, destinationAccount, amount, memo);
                        break;
                    case GET_PAYMENTS_MADE:
                        paymentOperationResponses = getPaymentsMade(accountId);
                        break;
                    case GET_PAYMENTS_RECEIVED:
                        paymentOperationResponses = getPaymentsReceived(accountId);
                        break;
                    case GET_ACCOUNT:
                        accountResponse = getAccount(seed);
                        break;
                    case MANAGE_SELL_OFFER:
                        submitTransactionResponse = manageSellOffer(seed, selling, buying, amount, price, offerId);
                        break;
                    case MANAGE_BUY_OFFER:
                        submitTransactionResponse = manageBuyOffer(seed, selling, buying, amount, price, offerId);
                        break;
                    case SET_OPTIONS:
                        submitTransactionResponse = setOptions(seed, clearFlags, highThreshold,
                                lowThreshold, inflationDestination, masterKeyWeight);
                        break;
                    case ALLOW_TRUST_OPERATION:
                        submitTransactionResponse = allowTrustOperation(seed, trustor, assetCode, authorize);
                        break;
                    case CHANGE_TRUST_OPERATION:
                        submitTransactionResponse = changeTrustOperation(seed, asset, limit);
                        break;
                    case MERGE_ACCOUNTS:
                        submitTransactionResponse = mergeAccounts(seed, destinationAccount);
                        break;
                    case MANAGE_DATA:
                        submitTransactionResponse = manageData(seed, name, value);
                        break;
                    case BUMP_SEQUENCE:
                        submitTransactionResponse = bumpSequence(seed, bumpTo);
                        break;
                    case CREATE_PASSIVE_OFFER:
                        submitTransactionResponse = createPassiveSellOffer(seed,selling,buying,amount,price);
                        break;
                    default:
                        Log.e(TAG, "........ There is no requestType found .... not good, bro?");
                        break;
                }
            } catch (Exception e) {
                returnedObject = e;
            }


            return returnedObject;
        }

        private static final int CREATE_ACCOUNT = 1, SEND_PAYMENT = 2, GET_PAYMENTS_RECEIVED = 3,
                GET_PAYMENTS_MADE = 4, GET_ACCOUNT = 5, MANAGE_SELL_OFFER = 6, MANAGE_BUY_OFFER = 7,
                SET_OPTIONS = 8, ALLOW_TRUST_OPERATION = 9, CHANGE_TRUST_OPERATION = 10, MERGE_ACCOUNTS = 11,
                MANAGE_DATA = 12, BUMP_SEQUENCE = 13, CREATE_PASSIVE_OFFER = 14;

        AccountResponseBag createAccount() throws Exception {
            LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C Creating new, like Stellar account ... " +
                    "\uD83D\uDD35 about to call KeyPair.random() ...... \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08  ");
            setServerAndNetwork(isDevelopment);
            AccountResponse accountResponse;
            try {
                InputStream response;
                KeyPair pair = KeyPair.random();
                String secret = new String(pair.getSecretSeed());
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Secret Seed: " + secret);
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 New Account Id: " + pair.getAccountId());
                if (isDevelopment) {
                    LOGGER.info("\n\uD83D\uDC99 \uD83D\uDC99 looks like we are in DEV: " +
                            "\uD83D\uDC99 ...... begging FriendBot for Lumens ... .........");
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
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Account created OK: account: " + accountResponse.getAccountId());
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 Account created OK: balance: " + accountResponse.getBalances()[0].getBalance());
                AccountResponseBag bag = new AccountResponseBag(secret, accountResponse);
                Log.d(TAG, "\uD83D\uDC99 bag sent back to Flutter: \uD83D\uDC99" + G.toJson(bag));
                return bag;
            } catch (IOException e) {
                LOGGER.severe("Failed to create account - see below ...");
                throw new Exception("\uD83D\uDD34 Unable to create Account", e);
            }
        }

        AccountResponse getAccount(String seed) throws IOException {
            setServerAndNetwork(isDevelopment);
            KeyPair source = KeyPair.fromSecretSeed(seed);
            String accountId = source.getAccountId();
            Log.d(TAG, "getAccount: AccountID from seed, accountId derived: " + accountId);
            AccountResponse resp = server.accounts().account(accountId);
            Log.d(TAG, resp == null ? "Account not found" : "Account found: " + resp.getAccountId());
            return resp;
        }

        SubmitTransactionResponse sendPayment(String seed, String destinationAccount,
                                              String amount, String memo) throws Exception {
            setServerAndNetwork(isDevelopment);
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

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new PaymentOperation.Builder(destAccount.getAccountId(),
                            new AssetTypeNative(), amount).build())
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
                    .setTimeout(TIMEOUT_IN_SECONDS)
                    .setOperationFee(100)
                    .build();
            try {
                transaction.sign(source);
                SubmitTransactionResponse response = server.submitTransaction(transaction);
                LOGGER.info("SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
                LOGGER.info(G.toJson(response));
                Log.d(TAG, response.isSuccess() ? "Payment transaction is SUCCESSFUL" : "Payment transaction failed");
                return response;
            } catch (Exception e) {
                String msg = "Failed to submit transaction: ";
                LOGGER.severe(msg + e.getMessage());
                throw new Exception(msg, e);
            }
        }

        List<PaymentOperationResponse> getPaymentsReceived(final String seed) throws Exception {
            setServerAndNetwork(isDevelopment);

            final KeyPair account = KeyPair.fromSecretSeed(seed);
            PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account.getAccountId());
            final List<PaymentOperationResponse> paymentOperationResponses = new ArrayList<>();
            try {
                Page<OperationResponse> responsePage = paymentsRequest
                        .limit(PAGE_LIMIT)
                        .execute();
                List<OperationResponse> responses = responsePage.getRecords();
                for (OperationResponse operationResponse : responses) {
                    if (operationResponse instanceof PaymentOperationResponse) {
                        if (((PaymentOperationResponse) operationResponse).getTo().equals(account.getAccountId())) {
                            PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse) operationResponse;
                            paymentOperationResponses.add(paymentOperationResponse);
                            Log.d(TAG, "Payment made: " + paymentOperationResponse.getAmount()
                                    + " for account: " + accountId);
                        }

                    }
                }
            } catch (Exception e) {
                LOGGER.severe("getPaymentsReceived:Failed to get payments: " + e.getMessage());
                throw e;
            }

            Log.d(TAG, "getPaymentsReceived:  \uD83E\uDD6C found " + paymentOperationResponses.size()
                    + " payments received by " + accountId);
            return paymentOperationResponses;
        }

        List<PaymentOperationResponse> getPaymentsMade(final String seed) throws Exception {
            setServerAndNetwork(isDevelopment);
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
                                    + " for keyPair: " + accountId);
                        }

                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Failed to get payments received: " + e.getMessage());
                throw e;
            }

            Log.d(TAG, "getPaymentsMade: \uD83E\uDD6C found " + paymentOperationResponses.size()
                    + " payments made by " + accountId);
            return paymentOperationResponses;
        }

        SubmitTransactionResponse manageSellOffer(final String seed, Asset selling, Asset buying, String amount, String price, Long offerId) throws Exception {
            setServerAndNetwork(isDevelopment);
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            ManageSellOfferOperation operation = new ManageSellOfferOperation.Builder(selling, buying, amount, price)
                    .setOfferId(offerId)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse manageBuyOffer(final String seed, Asset selling, Asset buying, String amount, String price, Long offerId) throws Exception {
            setServerAndNetwork(isDevelopment);
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            ManageBuyOfferOperation operation = new ManageBuyOfferOperation.Builder(selling, buying, amount, price)
                    .setOfferId(offerId)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse createPassiveSellOffer(final String seed, Asset selling, Asset buying, String amount, String price) throws Exception {

            setServerAndNetwork(isDevelopment);
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            CreatePassiveSellOfferOperation operation = new CreatePassiveSellOfferOperation.Builder(selling, buying, amount, price)
                    .setSourceAccount(accountId)
                    .build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse setOptions(final String seed, int clearFlags, int highThreshold, int lowThreshold,
                                             String inflationDestination, int masterKeyWeight) throws Exception {

            setServerAndNetwork(isDevelopment);
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
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
                    .setTimeout(TIMEOUT_IN_SECONDS)
                    .setOperationFee(100)
                    .build();
            try {
                transaction.sign(keyPair);
                SubmitTransactionResponse response = server.submitTransaction(transaction);
                LOGGER.info("setOptions: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess() + " \uD83D\uDC99 ");
                LOGGER.info(G.toJson(response));
                Log.d(TAG, response.isSuccess() ? "setOptions transaction is SUCCESSFUL" : "setOptions transaction failed");
                return response;
            } catch (Exception e) {
                String msg = "Failed to setOptions: ";
                LOGGER.severe(msg + e.getMessage());
                throw new Exception(msg, e);
            }
        }

        SubmitTransactionResponse allowTrustOperation(final String seed, String trustor, String assetCode, boolean authorize) throws Exception {
            setServerAndNetwork(isDevelopment);

            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            AllowTrustOperation operation = new AllowTrustOperation.Builder(trustor, assetCode, authorize)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse changeTrustOperation(final String seed, Asset asset, String limit) throws Exception {
            setServerAndNetwork(isDevelopment);

            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            ChangeTrustOperation operation = new ChangeTrustOperation.Builder(asset, limit)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse mergeAccounts(final String seed, String destinationAccount) throws Exception {
            setServerAndNetwork(isDevelopment);

            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
            AccountMergeOperation operation = new AccountMergeOperation.Builder(destinationAccount)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();
            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse manageData(final String seed, String name, String value) throws Exception {
            setServerAndNetwork(isDevelopment);
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());

            ManageDataOperation operation = new ManageDataOperation.Builder(name, value.getBytes())
                    .setSourceAccount(keyPair.getAccountId())
                    .build();
            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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

        SubmitTransactionResponse bumpSequence(final String seed, Long bumpTo) throws Exception {
            setServerAndNetwork(isDevelopment);
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());

            BumpSequenceOperation operation = new BumpSequenceOperation.Builder(bumpTo)
                    .setSourceAccount(keyPair.getAccountId())
                    .build();
            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(operation)
                    .addMemo(Memo.text(memo == null ? "N/A" : memo))
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
                String msg = "Failed to bumpSequence: ";
                LOGGER.severe(msg + e.getMessage());
                throw new Exception(msg, e);
            }
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
                        returnError("101", msg, "", methodResult);
                        break;
                    case SEND_PAYMENT:
                        String msg2 = "Send PaymentFailed";
                        Log.e(TAG, msg2, (Exception) result);
                        returnError("102", msg2, "", methodResult);
                        break;
                    case GET_ACCOUNT:
                        String msg3 = "Get Account Failed";
                        Log.e(TAG, msg3, (Exception) result);
                        returnError("103", msg3, "", methodResult);
                        break;
                    case GET_PAYMENTS_MADE:
                        String msg4 = "Get Payments Made Failed";
                        Log.e(TAG, msg4, (Exception) result);
                        returnError("104", msg4, "", methodResult);
                        break;
                    case GET_PAYMENTS_RECEIVED:
                        String msg5 = "Get Payments Received Failed";
                        Log.e(TAG, msg5, (Exception) result);
                        returnError("105", msg5, "", methodResult);
                        break;
                }
            } else {
                switch (requestType) {
                    case CREATE_ACCOUNT:
                        returnAccountResponseBag(accountResponseBag, methodResult);
                        break;
                    case GET_ACCOUNT:
                        returnAccountResponse(accountResponse, methodResult);
                        break;
                    case SEND_PAYMENT:
                        returnPaymentResponse(submitTransactionResponse, methodResult);
                        break;
                    case GET_PAYMENTS_MADE:
                    case GET_PAYMENTS_RECEIVED:
                        returnPayments(paymentOperationResponses, methodResult);
                        break;
                }
            }
        }
    }

    static private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    static private void returnAccountResponseBag(final AccountResponseBag accountResponseBag,
                                                 final MethodChannel.Result result) {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(G.toJson(accountResponseBag));
            }
        });
    }

    static private void returnAccountResponse(final AccountResponse accountResponse,
                                              final MethodChannel.Result result) {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(G.toJson(accountResponse));
            }
        });
    }

    static private void returnPaymentResponse(final SubmitTransactionResponse transactionResponse, final MethodChannel.Result result) {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(G.toJson(transactionResponse));
            }
        });
    }

    static private void returnPayments(final List<PaymentOperationResponse> accountResponse, final MethodChannel.Result result) {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(G.toJson(accountResponse));
            }
        });
    }

    static private void returnError(final String code, final String message, final String reason, final MethodChannel.Result result) {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                result.error(code, message, reason);
            }
        });
    }

    private static final String TAG = StellarOperations.class.getSimpleName();
}
