import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:stellarplugin/data_models/account_response_bag.dart';
import 'package:stellarplugin/data_models/payment_response.dart';
import 'package:stellarplugin/stellarplugin.dart';
import 'package:stellarplugin_example/account_details.dart';
import 'package:stellarplugin_example/slide_right.dart';

import 'account_details.dart';

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  var _key = GlobalKey<ScaffoldState>();
  var isBusy = false;
  var paymentsReceived0 = List<PaymentOperationResponse>();
  var paymentsReceived1 = List<PaymentOperationResponse>();
  var paymentsMade0 = List<PaymentOperationResponse>();
  var paymentsMade1 = List<PaymentOperationResponse>();
  List<AccountResponseBag> accountResponses = List();
  Future _createAccount() async {
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

  Future _sendPayment() async {
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
    var amount = "1230.09";
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

      print(
          '_MyAppState: _getAccount: 🥬 🥬 🥬 🥬  Account retrieved: ${acct.accountId}  🍎 '
          'balance: ${acct.balances.first.balance} ${acct.balances.first.assetType} 🍎 ');
      setState(() {
        isBusy = false;
      });
      var snackBar = SnackBar(
          content: Text('Account Retrieved: accountId: ${acct.accountId} '));
      _key.currentState.showSnackBar(snackBar);
      _key.currentState.showSnackBar(snackBar);
    } on PlatformException {
      print('We have a Plugin problem');
    }
  }

  var widgets = List<Widget>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _key,
      appBar: AppBar(
        title: Text('Stellar Flutter Plugin'),
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
                            color: Colors.grey[400],
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
                      Text('Received:'),
                      SizedBox(
                        width: 4,
                      ),
                      Text(
                        'Account 1',
                        style: TextStyle(fontWeight: FontWeight.w900),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      GestureDetector(
                        onTap: () {
                          if (accountResponses.isNotEmpty) {
                            _startAccount1(context);
                          }
                        },
                        child: Text(
                          '${paymentsReceived0.length}',
                          style: TextStyle(
                              fontSize: 36,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      Text(
                        'Account 2',
                        style: TextStyle(fontWeight: FontWeight.w900),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      GestureDetector(
                        onTap: () {
                          if (accountResponses.isNotEmpty) {
                            _startAccount2(context);
                          }
                        },
                        child: Text(
                          '${paymentsReceived1.length}',
                          style: TextStyle(
                              fontSize: 36,
                              color: Colors.black,
                              fontWeight: FontWeight.w900),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(
                    height: 20,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: <Widget>[
                      Text('Made:'),
                      SizedBox(
                        width: 12,
                      ),
                      Text(
                        'Account 1',
                        style: TextStyle(fontWeight: FontWeight.w900),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      GestureDetector(
                        onTap: () {
                          _startAccount1(context);
                        },
                        child: Text(
                          '${paymentsMade0.length}',
                          style: TextStyle(
                              fontSize: 36,
                              color: Colors.white,
                              fontWeight: FontWeight.w900),
                        ),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      Text(
                        'Account 2',
                        style: TextStyle(fontWeight: FontWeight.w900),
                      ),
                      SizedBox(
                        width: 8,
                      ),
                      GestureDetector(
                        onTap: () {
                          _startAccount2(context);
                        },
                        child: Text(
                          '${paymentsMade1.length}',
                          style: TextStyle(
                              fontSize: 36,
                              color: Colors.black,
                              fontWeight: FontWeight.w900),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(
                    height: 28,
                  ),
                ],
              ),
            ),
            preferredSize: Size.fromHeight(260)),
      ),
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
    );
  }

  void _startAccount2(BuildContext context) {
    print('startAccount2 ...............');
    Navigator.push(
        context,
        SlideRightRoute(
            widget: AccountDetails(
          accountResponse: accountResponses.elementAt(1),
          paymentsMade: paymentsMade1,
          accountName: "Account #2",
          paymentsReceived: paymentsReceived1,
        )));
  }

  void _startAccount1(BuildContext context) {
    print('startAccount1 ...............');
    Navigator.push(
        context,
        SlideRightRoute(
            widget: AccountDetails(
          accountName: "Account #1",
          paymentsMade: paymentsMade0,
          paymentsReceived: paymentsReceived0,
          accountResponse: accountResponses.elementAt(0),
        )));
  }
}
