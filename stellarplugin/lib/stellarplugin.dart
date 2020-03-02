import 'dart:async';

import 'package:flutter/services.dart';

class Stellarplugin {
  static const MethodChannel _channel = const MethodChannel('stellarplugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future createAccount(bool isDevelopmentStatus) async {
    final version = await _channel.invokeMethod(
        'createAccount', {"isDevelopmentStatus": '$isDevelopmentStatus'});
    print(version);
    return version;
  }
}
