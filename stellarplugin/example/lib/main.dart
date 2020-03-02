import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
      platformVersion = await Stellarplugin.platformVersion;
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

  var _key = GlobalKey<ScaffoldState>();
  _createAccounts() async {
    print('🔆 🔆 🔆 🔆 🔆 🔆 🔆 🔆  _createAccounts .....');
    try {
      var acct = await Stellarplugin.createAccount(true);
      print(
          '_MyAppState:  🥬 🥬 🥬 🥬  _createAccounts: 🥬 🥬 🥬 🥬  Account created: '
          '$acct  🍎  🍎 ');
      setState(() {
        widgets.add(Text(
          "Account has been created $acct",
          style:
              TextStyle(fontWeight: FontWeight.bold, color: Colors.indigo[400]),
        ));
      });
    } on PlatformException catch (e) {
      print('🔴🔴🔴 We have a Plugin problem');
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
    try {
      var acct = await Stellarplugin.createAccount;
      print(
          '_MyAppState: _createAccounts: 🥬 🥬 🥬 🥬  Account created: $acct  🍎  🍎 ');
    } on PlatformException {
      print('We have a Plugin problem');
    }
  }

  _getPaymentsReceived() async {
    try {
      var acct = await Stellarplugin.createAccount;
      print(
          '_MyAppState: _createAccounts: 🥬 🥬 🥬 🥬  Account created: $acct  🍎  🍎 ');
    } on PlatformException {
      print('We have a Plugin problem');
    }
  }

  _getPaymentsMade() async {
    try {
      var acct = await Stellarplugin.createAccount;
      print(
          '_MyAppState: _createAccounts: 🥬 🥬 🥬 🥬  Account created: $acct  🍎  🍎 ');
    } on PlatformException {
      print('We have a Plugin problem');
    }
  }

  _getAccount() async {
    try {
      var acct = await Stellarplugin.createAccount;
      print(
          '_MyAppState: _createAccounts: 🥬 🥬 🥬 🥬  Account created: $acct  🍎  🍎 ');
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
                      height: 4,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text('Stellar API\'s available'),
                        SizedBox(
                          width: 12,
                        ),
                        Text(
                          '3',
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
              preferredSize: Size.fromHeight(160)),
        ),
        backgroundColor: Colors.brown[100],
        body: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              children: <Widget>[
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 400,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.teal[700],
                    onPressed: _createAccounts,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Create Accounts',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
                SizedBox(
                  height: 16,
                ),
                Container(
                  width: 400,
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
                  width: 400,
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
                  width: 400,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.indigo[700],
                    onPressed: _sendPayment,
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
                  height: 16,
                ),
                Container(
                  width: 400,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.blueGrey[700],
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
                  height: 16,
                ),
                Container(
                  width: 400,
                  child: RaisedButton(
                    elevation: 4,
                    color: Colors.teal[300],
                    onPressed: _getAccount,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'Get Account',
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
