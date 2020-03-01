package com.boha.stellarplugin;

//import android.support.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import org.stellar.sdk.KeyPair;

/** StellarpluginPlugin */
public class StellarpluginPlugin implements FlutterPlugin, MethodCallHandler {
  @Override
  public void onAttachedToEngine( FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "stellarplugin");
    channel.setMethodCallHandler(new StellarpluginPlugin());
  }

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "stellarplugin");
    channel.setMethodCallHandler(new StellarpluginPlugin());
  }

  @Override
  public void onMethodCall( MethodCall call,  Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine( FlutterPluginBinding binding) {
  }

;
private void startStellar() {
  KeyPair pair = KeyPair.random();
  System.out.println(new String(pair.getSecretSeed()));
  System.out.println(pair.getAccountId());
}
}
