# stellarplugin

## Flutter Stellar Plugin

## Description

This Flutter plugin enables development of Android, iOS, macOS, web and Windows applications that need to interact with Stellar. The plugin provides access to all the operations that are exposed by the official Stellar SDK's.

~~~~

	var accountResponse =
              await Stellar.createAccount(isDevelopmentStatus: true);
~~~~
isDevelopmentStatus is a boolean flag to help the plugin connect to either the Testnet and the public Stellar network. This code creates a Stellar account and , if isDevelopmentStatus is true FriendBot is politely asked for 10,000 test XLM (lumens).
