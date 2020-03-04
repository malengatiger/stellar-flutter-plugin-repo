import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:stellarplugin/data_models/account_response.dart';
import 'package:stellarplugin/data_models/account_response_bag.dart';
import 'package:stellarplugin/data_models/payment_response.dart';
import 'package:stellarplugin/stellarplugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    print('🔆 🔆 🔆 🔆 🔆 🔆 🔆 🔆  initPlatformState');
    String platformVersion;
    try {
      platformVersion = await Stellar.platformVersion;
      print(
          '🔆 🔆 🔆 🔆 $platformVersion is apparently the Android version we are on  🍎  🍎 ');
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;
    setState(() {
      widgets.add(
        Text(
          "Platform Version is $platformVersion",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      );
    });
  }

  List<AccountResponseBag> accountResponses = List();

  var _key = GlobalKey<ScaffoldState>();
  _createAccount() async {
    print('🔆 🔆 🔆 🔆 🔆 🔆 🔆 🔆  _createAccounts starting .....');
    try {
      var accountResponse =
          await Stellar.createAccount(isDevelopmentStatus: true);
      accountResponses.add(accountResponse);
      print('_MyAppState:  🥬 🥬 🥬 🥬  _createAccounts: 🥬 🥬 🥬 🥬  '
          'Account created by Stellar returned: 🍎 Accounts: ${accountResponses.length} 🍎');
      setState(() {
        widgets.add(Text(
          "🍎 🍎 🍎 🍎 Account has been created with balances: ${accountResponse.accountResponse.balances.length} 🍎"
          " Balance: ${accountResponse.accountResponse.balances.first.balance} ${accountResponse.accountResponse.balances.first.assetType} ",
          style:
              TextStyle(fontWeight: FontWeight.bold, color: Colors.indigo[500]),
        ));
      });
    } on PlatformException catch (e) {
      print('🔴 🔴 🔴 We have a Plugin problem');
      setState(() {
        widgets.add(Text(
          "🔴 We have a problem ... $e",
          style:
              TextStyle(fontWeight: FontWeight.normal, color: Colors.pink[400]),
        ));
      });
    }
  }

  _sendPayment() async {
    if (accountResponses.length < 2) {
      print(
          '🔆 🔆 🔆 🔆 Please create at least 2 accounts for this (payment tranx) to work');
      return;
    }
    var seed = accountResponses.elementAt(0).secretSeed;
    var amount = "900";
    var memo = "Tx from Flutter";
    var destinationAccount =
        accountResponses.elementAt(1).accountResponse.accountId;
    try {
      var response = await Stellar.sendPayment(
          seed: seed,
          destinationAccount: destinationAccount,
          amount: amount,
          memo: memo,
          isDevelopmentStatus: true);
      print(
          '_MyAppState: _sendPayment: 🥬 🥬 🥬 🥬  Payment executed, check if error: $response  🍎  🍎 ');
      var obj = PaymentResponse.fromJson(jsonDecode(response));
      print(
          '_MyAppState: _sendPayment: 🥬 🥬 🥬 🥬  Payment executed; json from object: ${obj.toJson()}  🍎  🍎 ');
      _getAccount();
      _getPaymentsReceived();
      _getPaymentsMade();
    } on PlatformException catch (e) {
      print('🔴 🔴 We have a Plugin problem: 🔴 $e');
    }
  }

  _getPaymentsReceived() async {
    if (accountResponses.length < 2) {
      print(
          '🔆 🔆 🔆 🔆 Please create at least 2 accounts for this (_getPaymentsReceived tranx) to work');
      return;
    }
    try {
      paymentsReceived = await Stellar.getPaymentsReceived(
          accountId: accountResponses.elementAt(1).accountResponse.accountId);
      print(
          '_MyAppState: _getPaymentsReceived: 🥬 🥬 🥬 🥬 👺  Payments received, account #2: ${paymentsReceived.length}  🍎 🍎 ');
      var paymentsReceived1 = await Stellar.getPaymentsReceived(
          accountId: accountResponses.elementAt(0).accountResponse.accountId);
      print(
          '_MyAppState: _getPaymentsReceived: 🥬 🥬 🥬 🥬 👺   Payments received, account #1: ${paymentsReceived1.length}  🍎 🍎 ');
      setState(() {});
    } on PlatformException catch (e) {
      print('We have a Plugin problem: $e');
    }
  }

  var paymentsReceived = List<PaymentResponse>();
  var paymentsMade = List<PaymentResponse>();
  _getPaymentsMade() async {
    if (accountResponses.length < 2) {
      print(
          '🔆 🔆 🔆 🔆 Please create at least 2 accounts for this (_getPaymentsMade tranx) to work');
      return;
    }
    try {
      paymentsMade = await Stellar.getPaymentsMade(
          accountId: accountResponses.first.accountResponse.accountId);
      print(
          '\n_MyAppState: _getPaymentsMade: 💙💙 💙💙 💙💙 💙💙   Payments made (account #1): ${paymentsMade.length}  🍎  🍎 💙💙 💙💙 💙💙 ');
      var paymentsMade1 = await Stellar.getPaymentsMade(
          accountId: accountResponses.elementAt(1).accountResponse.accountId);
      print(
          '_MyAppState: _getPaymentsMade: 💙💙 💙💙 💙💙 💙💙    Payments made (account #2): ${paymentsMade1.length}  🍎  🍎 💙💙 💙💙 💙💙 ');
      setState(() {});
    } on PlatformException catch (e) {
      print('We have a Plugin problem: $e');
    }
  }

  _getAccount() async {
    print(
        '_MyAppState: _getAccount: 🥬 🥬 🥬 🥬  .... getting Account from Stellar  🍎  🍎 ');
    if (accountResponses.isEmpty) {
      print('You need at least 1 account created for this to work 🔆 🔆 🔆 🔆');
    }
    try {
      var acct = await Stellar.getAccount(
          seed: accountResponses.elementAt(0).secretSeed);
      var mJson = jsonDecode(acct);
      var response = AccountResponse.fromJson(mJson);
      print(
          '_MyAppState: _getAccount: 🥬 🥬 🥬 🥬  Account retrieved: ${response.accountId}  🍎 '
          'balance: ${response.balances.first.balance} ${response.balances.first.assetType} 🍎 ');
    } on PlatformException {
      print('We have a Plugin problem');
    }
  }

  var widgets = List<Widget>();
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primaryColor: Colors.pink,
      ),
      home: Scaffold(
        key: _key,
        appBar: AppBar(
          title: const Text('Stellar Flutter Plugin'),
          backgroundColor: Colors.pink[300],
          bottom: PreferredSize(
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: <Widget>[
                    Text(
                      'Plugin to access Stellar SDK from Flutter apps. Created from official Java SDK',
                      style: TextStyle(color: Colors.white),
                    ),
                    SizedBox(
                      height: 12,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text('Stellar API\'s available'),
                        SizedBox(
                          width: 12,
                        ),
                        Text(
                          '5',
                          style: TextStyle(
                              fontSize: 30,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 8,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text('Stellar Accounts'),
                        SizedBox(
                          width: 12,
                        ),
                        Text(
                          '${accountResponses.length}',
                          style: TextStyle(
                              fontSize: 30,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 8,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text('Payments Received'),
                        SizedBox(
                          width: 12,
                        ),
                        Text(
                          '${paymentsReceived.length}',
                          style: TextStyle(
                              fontSize: 30,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 8,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text('Payments Made'),
                        SizedBox(
                          width: 12,
                        ),
                        Text(
                          '${paymentsMade.length}',
                          style: TextStyle(
                              fontSize: 30,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 8,
                    ),
                  ],
                ),
              ),
              preferredSize: Size.fromHeight(240)),
        ),
        backgroundColor: Colors.brown[100],
        body: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: <Widget>[
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 360,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.teal[700],
                    onPressed: _createAccount,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Create Account',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 360,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.teal[500],
                    onPressed: _getAccount,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Retrieve Account',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 360,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.blue[700],
                    onPressed: _sendPayment,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Send Payment',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 360,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.pink[700],
                    onPressed: _getPaymentsMade,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Get Payments Made',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 360,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.indigo[700],
                    onPressed: _getPaymentsReceived,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Get Payments Received',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 20,
                ),
                Card(
                  elevation: 2,
                  color: Colors.grey[300],
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Container(
                      width: double.infinity,
                      child: Column(
                        children: widgets,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
