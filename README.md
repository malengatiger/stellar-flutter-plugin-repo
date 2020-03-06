# stellarplugin

## Flutter Stellar Plugin

## Description and Example Usage

This Flutter plugin enables development of Android, iOS, macOS, web and Windows applications that need to interact with 
Stellar. The plugin provides access to all the operations that are exposed by the official Stellar SDK's. 
The initial version of the Flutter plugin is derived from the Java version of the SDK and the first release of the plugin supports Android development. 
iOS, macOS, web and Windows development will follow in due course and as the Flutter ecosystem evolves to fully support a single code base for all these environments. 

~~~~
import 'package:stellarplugin/data_models/account_response_bag.dart';
import 'package:stellarplugin/data_models/payment_response.dart';
import 'package:stellarplugin/stellarplugin.dart';

AccountResponseBag accountResponseBag = await Stellar.createAccount(isDevelopmentStatus: true);

PaymentOperationResponse response = await Stellar.sendPayment(
          seed: seed,
          destinationAccount: destinationAccount,
          amount: amount,
          memo: memo,
          isDevelopmentStatus: true);

List<PaymentOperationResponse> paymentsReceived = await Stellar.getPaymentsReceived(
          seed: "secretSeed here");

List<PaymentOperationResponse> paymentsMade = await Stellar.getPaymentsMade(
          seed: "secretSeed here");

 AccountResponse acct = await Stellar.getAccount(
          seed: "secretSeed here");
~~~~
isDevelopmentStatus is a boolean flag to help the plugin connect to either the Testnet and the public Stellar network. This code creates Stellar accounts and , if isDevelopmentStatus is true FriendBot is politely asked for 10,000 test XLM (lumens). Please take a look at the code that exercises most of the Stellar transaction operations listed below. The example code lives at https://github.com/malengatiger/stellar-flutter-plugin-repo/tree/master/stellarplugin/example 

## Stellar Transaction Operations supported:

- CreateAccount 
- SendPayment 
- GetAccount
- SetOptions
- ManageBuyOffer
- ManageSellOffer
- CreatePassiveOffer
- AllowTrust
- ChangeTrust
- MergeAccounts
- BumpSequence
- ManageData

## Open Source Code
Code for the plugin and the example app can be found here: https://github.com/malengatiger/stellar-flutter-plugin-repo

## Flutter
More information about Flutter can be found here: https://flutter.dev/ 

## Screenshots from the example Flutter app
This app is merely designed to exercise all the Stellar transaction operations available in the official SDK's. To get going, start by creating at least 2 accounts. Just tap around the UI provided and the app will make the appropriate calls to the Stellar Testnet. Scroll to the bottom of the screen to see logs of the actions taken during your use of the example app.

### Screenshot 1 : Main Page of Example Flutter app
![Screenshot 1: Main Page of Example Flutter app](https://firebasestorage.googleapis.com/v0/b/dancer26983.appspot.com/o/screenshots%2Fdevice-2020-03-06-130303.png?alt=media&token=99b7b09e-7e1f-4c95-a360-40ff3ed45d96 "Main Page of Example Flutter app")

### Screenshot 2: Main Page Scrolled to bottom for logs
![Screenshot 2: Main Page Scrolled to bottom](https://firebasestorage.googleapis.com/v0/b/dancer26983.appspot.com/o/screenshots%2Fdevice-2020-03-06-130425.png?alt=media&token=6a9e8c17-e3e3-4cff-a558-3884cfb33065 "Main Page Scrolled to bottom")

### Screenshot 3: Account Page of Example Flutter app
![Screenshot 3: Account Page of Example Flutter app](https://firebasestorage.googleapis.com/v0/b/dancer26983.appspot.com/o/screenshots%2Fdevice-2020-03-06-131125.png?alt=media&token=5350300a-ce15-4d57-a645-3a4d5d9aeaed "Account Page of Example Flutter app")


