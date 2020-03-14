package com.boha.stellarplugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * StellarpluginPlugin
 */
public class StellarpluginPlugin implements FlutterPlugin, MethodCallHandler {
    private static final Logger LOGGER = Logger.getLogger(StellarpluginPlugin.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private Context context;

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "stellarplugin");
        channel.setMethodCallHandler(new StellarpluginPlugin());
        context = flutterPluginBinding.getApplicationContext();
        LOGGER.info("\uD83D\uDD35 onAttachedToEngine completed ... methodChannel: " + channel.toString());
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "stellarplugin");
        channel.setMethodCallHandler(new StellarpluginPlugin());
        LOGGER.info("\uD83D\uDD35 registerWith completed... ");

    }

    private boolean isDevelopment;
    private StellarOps ops = new StellarOps();
    private Result result;
    private MethodCall call;

    @Override
    public void onMethodCall(MethodCall call, @NotNull Result result) {

        this.result = result;
        this.call = call;
        String callMethod = call.method;
        isDevelopment = getDevelopmentFlag(call);
        switch (callMethod) {
            case "createAccount":
                createAccount();
                break;
            case "getAccount":
                getAccount();
                break;
            case "getPaymentsReceived":
                getPaymentsReceived();
                break;
            case "sendPayment":
                sendPayment();
                break;
            case "getPaymentsMade":
                getPaymentsMade();
                break;
            case "manageBuyOffer":
                manageBuyOffer();
                break;
            case "manageSellOffer":
                manageSellOffer();
                break;
            case "allowTrust":
                allowTrust();
                break;
            case "changeTrust":
                changeTrust();
                break;
            case "mergeAccounts":
                mergeAccounts();
                break;
            case "bumpSequence":
                bumpSequence();
                break;
            case "setOptions":
                setOptions();
                break;
            case "createPassiveOffer":
                createPassiveOffer();
                break;
            case "manageData":
                manageData();
                break;
            case "getPlatformVersion":
                LOGGER.info("\uD83D\uDD35 onMethodCall : getPlatformVersion: " + android.os.Build.VERSION.RELEASE);
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            default:
                result.notImplemented();
                LOGGER.info("\uD83D\uDD35 onMethodCall : notImplemented");
                break;
        }
    }

    private static final String TAG = StellarpluginPlugin.class.getSimpleName();

    private void createAccount() {
        try {
            ops.createAccount(isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34" +
                    "  Create account all fucked!", e);
            result.error("100", "Create Account has failed spectacularly", "\uD83D\uDD34");

        }
    }

    private void getAccount() {
        String seed = call.argument("seed");
        ops.getAccount(seed, isDevelopment, result);

    }


    private boolean getDevelopmentFlag(MethodCall call) {

        Object param = call.argument("isDevelopmentStatus");
        if (param == null) {
            LOGGER.warning("\uD83C\uDF4E \uD83C\uDF4E Param isDevelopmentStatus is NULL, return true");
            return true;
        }
        String isDev = (String) param;
        isDevelopment = isDev != null && isDev.equalsIgnoreCase("true");
        return isDevelopment;
    }

    private void manageBuyOffer() {
        String seed = call.argument("seed");
        String sellJson = call.argument("selling");
        String buyingJson = call.argument("buying");
        String amount = call.argument("amount");
        String price = call.argument("price");
        Long offerId = call.argument("offerId");
        ops.manageBuyOffer(seed, sellJson, buyingJson, amount, price, offerId, result, isDevelopment);

    }

    private void manageSellOffer() {
        String seed = call.argument("seed");
        String sellJson = call.argument("selling");
        String buyingJson = call.argument("buying");
        String amount = call.argument("amount");
        String price = call.argument("price");
        Long offerId = call.argument("offerId");
        ops.manageSellOffer(seed, sellJson, buyingJson, amount, price, offerId, result, isDevelopment);

    }

    private void createPassiveOffer() {
        String seed = call.argument("seed");
        String sellJson = call.argument("selling");
        String buyingJson = call.argument("buying");
        String amount = call.argument("amount");
        String price = call.argument("price");
        ops.createPassiveOffer(seed, sellJson, buyingJson, amount, price, result, isDevelopment);

    }
    private void manageData() {
        String seed = call.argument("seed");
        String name = call.argument("name");
        String value = call.argument("value");

        ops.manageData(seed,name,value,result,isDevelopment);

    }

    private void allowTrust() {
        String seed = call.argument("seed");
        String trustor = call.argument("trustor");
        String assetCode = call.argument("assetCode");
        boolean auth = call.argument("authorized");
        ops.allowTrust(seed, trustor, assetCode, auth, result, isDevelopment);
    }

    private void changeTrust() {
        String seed = call.argument("seed");
        String assetJson = call.argument("asset");
        String limit = call.argument("limit");
        boolean auth = call.argument("authorized");
        ops.changeTrust(seed, assetJson, limit, result, isDevelopment);
    }

    private void mergeAccounts() {
        String seed = call.argument("seed");
        String destinationAccount = call.argument("destinationAccount");
        ops.mergeAccounts(seed, destinationAccount, result, isDevelopment);
    }

    private void bumpSequence() {
        String seed = call.argument("seed");
        Long bumpTo = call.argument("bumpTo");
        ops.bumpSequence(seed, bumpTo, result, isDevelopment);
    }

    private void setOptions() {
        String seed = call.argument("seed");
        int clearFlags = call.argument("clearFlags");
        int highThreshold = call.argument("highThreshold");
        int lowThreshold = call.argument("lowThreshold");
        int masterKeyWeight = call.argument("masterKeyWeight");
        String inflationDestination = call.argument("inflationDestination");

        ops.setOptions(seed, clearFlags, highThreshold, lowThreshold,
                inflationDestination, masterKeyWeight, result, isDevelopment);

    }


    private void getPaymentsReceived() {
        String seed = call.argument("seed");
        ops.getPaymentsReceived(seed, isDevelopment, result);

    }

    private void getPaymentsMade() {
        String seed = call.argument("seed");
        ops.getPaymentsMade(seed, isDevelopment, result);

    }

    private void sendPayment() {
        String seed = call.argument("seed");
        String destAccount = call.argument("destinationAccount");
        String amount = call.argument("amount");
        String memo = call.argument("memo");
        ops.sendPayment(seed, destAccount, amount, memo, isDevelopment, result);

    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
    }


}
