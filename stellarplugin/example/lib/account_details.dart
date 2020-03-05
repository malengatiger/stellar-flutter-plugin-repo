import 'package:flutter/material.dart';
import 'package:stellarplugin/data_models/account_response.dart';
import 'package:stellarplugin/data_models/payment_response.dart';

class AccountDetails extends StatelessWidget {
  final List<SubmitTransactionResponse> paymentsReceived;
  final List<SubmitTransactionResponse> paymentsMade;
  final AccountResponse accountResponse;

  AccountDetails(
      {this.paymentsReceived, this.accountResponse, this.paymentsMade});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Account Details'),
      ),
      body: SingleChildScrollView(
        child: Column(),
      ),
    );
  }
}
