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
      setState(() {
        isBusy = true;
      });
      var accountResponse =
          await Stellar.createAccount(isDevelopmentStatus: true);
      accountResponses.add(accountResponse);
      print('_MyAppState:  🥬 🥬 🥬 🥬  _createAccounts: 🥬 🥬 🥬 🥬  '
          'Account created by Stellar returned: 🍎 Accounts: ${accountResponses.length} 🍎');
      setState(() {
        isBusy = false;
        widgets.add(Text(
          "🍎 🍎 🍎 🍎 Account has been created with balances: ${accountResponse.accountResponse.balances.length} 🍎"
          " Balance: ${accountResponse.accountResponse.balances.first.balance} ${accountResponse.accountResponse.balances.first.assetType} ",
          style:
              TextStyle(fontWeight: FontWeight.bold, color: Colors.indigo[500]),
        ));
      });
      var snackBar = SnackBar(
        content: Text(
          'Account has been created',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.black,
      );
      _key.currentState.showSnackBar(snackBar);
    } on PlatformException catch (e) {
      print('🔴 🔴 🔴 We have a Plugin problem');
      setState(() {
        isBusy = false;
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
      var snackBar = SnackBar(
        content: Text(
          'Please create at least 2 accounts',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.red[700],
      );
      _key.currentState.showSnackBar(snackBar);
      return;
    }
    var seed = accountResponses.elementAt(0).secretSeed;
    var amount = "50.08";
    var memo = "Tx from Flutter";
    var destinationAccount =
        accountResponses.elementAt(1).accountResponse.accountId;
    try {
      setState(() {
        isBusy = true;
      });
      var response = await Stellar.sendPayment(
          seed: seed,
          destinationAccount: destinationAccount,
          amount: amount,
          memo: memo,
          isDevelopmentStatus: true);
      print(
          '_MyAppState: _sendPayment: 🥬 🥬 🥬 🥬  Payment executed; json from object: ${response.toJson()}  🍎  🍎 ');
      await _getAccount();
      await _getPaymentsReceived();
      await _getPaymentsMade();
      setState(() {
        isBusy = false;
      });
      var snackBar = SnackBar(
        content: Text(
          'Payment has been sent',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.indigo[700],
      );
      _key.currentState.showSnackBar(snackBar);
    } on PlatformException catch (e) {
      print('🔴 🔴 We have a Plugin problem: 🔴 $e');
    }
  }

  bool isBusy = false;
  Future _getPaymentsReceived() async {
    if (accountResponses.length < 2) {
      print(
          '🔆 🔆 🔆 🔆 Please create at least 2 accounts for this (_getPaymentsReceived tranx) to work');
      var snackBar = SnackBar(
        content: Text(
          'Please create at least 2 accounts',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.red[700],
      );
      _key.currentState.showSnackBar(snackBar);
      return;
    }
    try {
      setState(() {
        isBusy = true;
      });

      paymentsReceived0 = await Stellar.getPaymentsReceived(
          seed: accountResponses.elementAt(0).secretSeed);
      print(
          '_MyAppState: _getPaymentsReceived: 🥬 🥬 🥬 🥬 👺   Payments received, account #1: ${paymentsReceived0.length}  🍎 🍎 ');
      paymentsReceived1 = await Stellar.getPaymentsReceived(
          seed: accountResponses.elementAt(1).secretSeed);
      print(
          '_MyAppState: _getPaymentsReceived: 🥬 🥬 🥬 🥬 👺  Payments received, account #2: ${paymentsReceived1.length}  🍎 🍎 ');
      setState(() {
        isBusy = false;
      });
      var snackBar = SnackBar(
        content: Text(
          'Payments Received: Acct1: ${paymentsReceived0.length} Acct2: ${paymentsReceived1.length}',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.teal[700],
      );
      _key.currentState.showSnackBar(snackBar);
    } on PlatformException catch (e) {
      print('We have a Plugin problem: $e');
    }
  }

  var paymentsReceived0 = List<PaymentOperationResponse>();
  var paymentsReceived1 = List<PaymentOperationResponse>();
  var paymentsMade0 = List<PaymentOperationResponse>();
  var paymentsMade1 = List<PaymentOperationResponse>();
  Future _getPaymentsMade() async {
    if (accountResponses.length < 2) {
      print(
          '🔆 🔆 🔆 🔆 Please create at least 2 accounts for this (_getPaymentsMade tranx) to work');
      var snackBar = SnackBar(
        content: Text(
          'Please create at least 2 accounts',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.red[700],
      );
      _key.currentState.showSnackBar(snackBar);
      return;
    }
    try {
      setState(() {
        isBusy = true;
      });
      paymentsMade0 = await Stellar.getPaymentsMade(
          seed: accountResponses.first.secretSeed);
      print(
          '\n_MyAppState: _getPaymentsMade: 💙💙 💙💙 💙💙 💙💙   Payments made (account #1): ${paymentsMade0.length}  🍎  🍎 💙💙 💙💙 💙💙 ');
      paymentsMade1 = await Stellar.getPaymentsMade(
          seed: accountResponses.elementAt(1).secretSeed);
      print(
          '_MyAppState: _getPaymentsMade: 💙💙 💙💙 💙💙 💙💙    Payments made (account #2): ${paymentsMade1.length}  🍎  🍎 💙💙 💙💙 💙💙 ');
      setState(() {
        isBusy = false;
      });
      var snackBar = SnackBar(
        content: Text(
          'Payments Made: Acct1: ${paymentsMade0.length} Acct2: ${paymentsMade1.length}',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.pink[700],
      );
      _key.currentState.showSnackBar(snackBar);
    } on PlatformException catch (e) {
      print('We have a Plugin problem: $e');
    }
  }

  Future _getAccount() async {
    print(
        '_MyAppState: _getAccount: 🥬 🥬 🥬 🥬  .... getting Account from Stellar  🍎  🍎 ');
    if (accountResponses.isEmpty) {
      print('You need at least 1 account created for this to work 🔆 🔆 🔆 🔆');
      var snackBar = SnackBar(
        content: Text(
          'You need at least 1 account created for this to work',
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: Colors.red[700],
      );
      _key.currentState.showSnackBar(snackBar);
    }
    try {
      setState(() {
        isBusy = true;
      });
      var acct = await Stellar.getAccount(
          seed: accountResponses.elementAt(0).secretSeed);
      var mJson = jsonDecode(acct);
      var response = AccountResponse.fromJson(mJson);
      print(
          '_MyAppState: _getAccount: 🥬 🥬 🥬 🥬  Account retrieved: ${response.accountId}  🍎 '
          'balance: ${response.balances.first.balance} ${response.balances.first.assetType} 🍎 ');
      setState(() {
        isBusy = false;
      });
      var snackBar = SnackBar(
          content:
              Text('Account Retrieved: accountId: ${response.accountId} '));
      _key.currentState.showSnackBar(snackBar);
      _key.currentState.showSnackBar(snackBar);
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
          actions: <Widget>[],
          bottom: PreferredSize(
              child: Padding(
                padding: const EdgeInsets.all(12.0),
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
                          '13',
                          style: TextStyle(
                              fontSize: 24,
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
                              fontSize: 24,
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
                          width: 4,
                        ),
                        Text('Account 1'),
                        SizedBox(
                          width: 8,
                        ),
                        Text(
                          '${paymentsReceived0.length}',
                          style: TextStyle(
                              fontSize: 24,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                        SizedBox(
                          width: 8,
                        ),
                        Text('Account 2'),
                        SizedBox(
                          width: 8,
                        ),
                        Text(
                          '${paymentsReceived1.length}',
                          style: TextStyle(
                              fontSize: 24,
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
                        Text('Account 1'),
                        SizedBox(
                          width: 8,
                        ),
                        Text(
                          '${paymentsMade0.length}',
                          style: TextStyle(
                              fontSize: 24,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                        SizedBox(
                          width: 8,
                        ),
                        Text('Account 2'),
                        SizedBox(
                          width: 8,
                        ),
                        Text(
                          '${paymentsMade1.length}',
                          style: TextStyle(
                              fontSize: 24,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 28,
                    ),
                  ],
                ),
              ),
              preferredSize: Size.fromHeight(240)),
        ),
        backgroundColor: Colors.brown[100],
        body: isBusy
            ? Center(
                child: CircularProgressIndicator(
                  strokeWidth: 4,
                ),
              )
            : SingleChildScrollView(
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
