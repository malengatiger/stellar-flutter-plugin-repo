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
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
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
    private StellarOperations operations = new StellarOperations();
    private Result result;
    private MethodCall call;

    @Override
    public void onMethodCall(MethodCall call, @NotNull Result result) {
        LOGGER.info("\uD83D\uDD35 .... \uD83D\uDC99 \uD83D\uDC99  " +
                "onMethodCall started inside plugin... \uD83D\uDC99 \uD83D\uDC99 ");
        String msg =  context == null? "NO CONTEXT here":"WE have a fucking CONTEXT!!";
        LOGGER.warning("Do we have a handle to context? ".concat(msg));
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("s", 0);

        }
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
        Log.d(TAG, "\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 createAccount ...");
        try {
            operations.createAccount(isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34" +
                    "  Create account all fucked!", e);
            result.error("100", "Create Account has failed spectacularly", "\uD83D\uDD34");

        }
    }


    private boolean getDevelopmentFlag(MethodCall call) {
        Log.d(TAG, "\uD83D\uDD35 arguments in getDevelopmentFlag: " + call.arguments + " \uD83D\uDD35");

        Object param = call.argument("isDevelopmentStatus");
        if (param == null) {
            LOGGER.warning("\uD83C\uDF4E \uD83C\uDF4E Param isDevelopmentStatus is NULL, return true");
            return true;
        }
        String isDev = (String) param;
        isDevelopment = isDev != null && isDev.equalsIgnoreCase("true");

        LOGGER.info("\uD83D\uDD35 getDevelopmentFlag : ....\uD83D\uDD35 " +
                "...... returning isDevelopment: "
                + isDevelopment + " \uD83D\uDD35");
        return isDevelopment;
    }

    private void getAccount() {
        String accountId = call.argument("seed");
        try {
            operations.getAccount(accountId, isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 getAccount failed", e);
            result.error("103", "GetAccount has failed", "\uD83D\uDD34");
        }
    }

    private void getPaymentsReceived() {
        String accountId = call.argument("accountId");
        try {
            operations.getPaymentsReceived(accountId, isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 getPaymentsReceived failed", e);
            result.error("102", "GetPaymentsReceived has failed", "\uD83D\uDD34");
        }
    }

    private void getPaymentsMade() {
        String accountId = call.argument("accountId");
        try {
            operations.getPaymentsMade(accountId, isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 getPaymentsMade failed", e);
            result.error("102", "GetPaymentsMade has failed", "\uD83D\uDD34");
        }
    }

    private void sendPayment() {
        String seed = call.argument("seed");
        String destAccount = call.argument("destinationAccount");
        String amount = call.argument("amount");
        String memo = call.argument("memo");
        try {
            operations.sendPayment(seed, destAccount, amount, memo, isDevelopment, result);
        } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDD34 sendPayment failed", e);
            result.error("100", "Create Account has failed", "\uD83D\uDD34");
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
    }


}
