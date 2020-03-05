# stellarplugin

## Flutter Stellar Plugin

## Description

This Flutter plugin enables development of Android, iOS, macOS, web and Windows applications that need to interact with Stellar. The plugin provides access to all the operations that are exposed by the official Stellar SDK's. The initial version of the Flutter plugin is derived from the Java version of the SDK and the first release of the plugin supports Android development. iOS development will follow in due course. 

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
isDevelopmentStatus is a boolean flag to help the plugin connect to either the Testnet and the public Stellar network. This code creates a Stellar account and , if isDevelopmentStatus is true FriendBot is politely asked for 10,000 test XLM (lumens).

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
