import 'dart:html';

import 'package:flutter/material.dart';
import 'package:stellarplugin/data_models/account_response.dart';

class AccountDetails extends StatelessWidget {
  final List<PaymentResponse> paymentsReceived;
  final List<PaymentResponse> paymentsMade;
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
