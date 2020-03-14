import 'dart:async';
import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:stellarplugin/data_models/account_response.dart';
import 'package:stellarplugin/data_models/payment_response.dart';

import 'data_models/account_response_bag.dart';
import 'data_models/trabsaction_response.dart';

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
        '🔵 🔵 🔵 🔵 🔵 🔵 Stellar, the plugin, 🔵 🔵 🔵 🔵 🔵 🔵  .. createAccount did the bizniz! 🔆 🔆 🔆 ');
    return result;
  }

  static Future<SubmitTransactionResponse> sendPayment(
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
    return SubmitTransactionResponse.fromJson(mJson);
  }

  static Future<List<PaymentOperationResponse>> getPaymentsReceived(
      {@required String seed, bool isDevelopmentStatus = true}) async {
    final pString = await _channel.invokeMethod('getPaymentsReceived', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "seed": '$seed',
    });
    print('Stellar:  🔵 🔵 🔵 getPaymentsReceived: string result: $pString');
    List payments = jsonDecode(pString);
    List<PaymentOperationResponse> mList = List();
    payments.forEach((p) {
      var mm = PaymentOperationResponse.fromJson(p);
      mList.add(mm);
    });
    print(
        'Stellar:  🔵 🔵 🔵 getPaymentsReceived: payments found: 🔵 ${mList.length}');
    return mList;
  }

  static Future<List<PaymentOperationResponse>> getPaymentsMade(
      {@required String seed, isDevelopmentStatus = true}) async {
    String pString = await _channel.invokeMethod('getPaymentsMade', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "seed": '$seed',
    });
    print(
        'Stellar:   🔴  🔴  🔴  getPaymentsMade: string result ...... $pString');
    List payments = jsonDecode(pString);
    List<PaymentOperationResponse> mList = List();
    payments.forEach((p) {
      var mm = PaymentOperationResponse.fromJson(p);
      mList.add(mm);
    });
    print(
        'Stellar:  🔴  🔴  🔴  getPaymentsMade: payments found:  🔴 ${mList.length}');
    return mList;
  }

  static Future<AccountResponse> getAccount(
      {@required String seed, bool isDevelopmentStatus = true}) async {
    final pString = await _channel.invokeMethod('getAccount', {
      "isDevelopmentStatus": '$isDevelopmentStatus',
      "seed": '$seed',
    });
    var resp = AccountResponse.fromJson(jsonDecode(pString));
    print(
        'Stellar on the Flutter side, receiving account:  🔵 🔵 🔵 ${resp.accountId}');
    return resp;
  }
}
