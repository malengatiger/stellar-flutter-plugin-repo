import 'dart:async';
import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

import 'data_models/account_response_bag.dart';

class Stellar {
  static const MethodChannel _channel = const MethodChannel('stellarplugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<AccountResponseBag> createAccount(
      {bool isDevelopmentStatus = true}) async {
    final accountResponse = await _channel.invokeMethod(
        'createAccount', {"isDevelopmentStatus": '$isDevelopmentStatus'});
    var mJson = json.decode(accountResponse);
    var result = AccountResponseBag.fromJson(mJson);
    print(
        'Stellar, the plugin connector: 🔵 🔵 🔵 createAccount did the biz;: accountId: ' +
            result.accountResponse.accountId +
            " seed: " +
            result.secretSeed);
    return result;
  }

  static Future sendPayment(
      {@required String seed,
      @required String destinationAccount,
      @required String amount,
      @required String memo,
      bool isDevelopmentStatus = true}) async {
    final paymentResponse = await _channel.invokeMethod('sendPayment', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "seed": '$seed',
      'destinationAccount': destinationAccount,
      'amount': amount,
      'memo': memo
    });
    print('Stellar, Mz Connector, returned:  🔵 🔵 🔵 paymentResponse: ' +
        paymentResponse);
    var mJson = jsonDecode(paymentResponse);
    try {
      if (mJson["extras"]["resultCodes"]["transactionResultCode"] ==
          "tx_bad_seq") {
        print(
            '🔴 🔴 Bad moon rising ... 🔴 bad sequence number 🔴 🔴 🔴 🔴 🔴 throwing a hissy fit! (aka PlatformException)...');
        throw PlatformException(code: "Payment failed. 🔴 Bad sequence number");
      }
    } catch (e) {
      print(e);
    }
    return paymentResponse;
  }

  static Future getPaymentsReceived(
      {@required String accountId, bool isDevelopmentStatus = true}) async {
    final payments = await _channel.invokeMethod('getPaymentsReceived', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "accountId": '$accountId',
    });
    print('Stellar:  🔵 🔵 🔵 getPaymentsReceived: ' + payments);
    return payments;
  }

  static Future getPaymentsMade(
      {@required String accountId, isDevelopmentStatus = true}) async {
    final payments = await _channel.invokeMethod('getPaymentsMade', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "accountId": '$accountId',
    });
    print('Stellar:  🔵 🔵 🔵 getPaymentsMade: ' + payments);
    return payments;
  }

  static Future getAccount(
      {@required String seed, bool isDevelopmentStatus = true}) async {
    final accountResponse = await _channel.invokeMethod('getAccount', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "seed": '$seed',
    });
    print('Stellar:  🔵 🔵 🔵 getAccount: ' + accountResponse);
    return accountResponse;
  }
}
