import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:stellarplugin/data_models/account_response_bag.dart';
import 'package:stellarplugin/data_models/payment_response.dart';
import 'package:stellarplugin/stellarplugin.dart';
import 'package:stellarplugin_example/home.dart';

class AccountDetails extends StatefulWidget {
  final List<PaymentOperationResponse> paymentsReceived;
  final List<PaymentOperationResponse> paymentsMade;
  final AccountResponseBag accountResponse;
  final String accountName;

  AccountDetails(
      {this.paymentsReceived,
      this.accountResponse,
      this.paymentsMade,
      this.accountName});

  @override
  _AccountDetailsState createState() => _AccountDetailsState();
}

class _AccountDetailsState extends State<AccountDetails> {
  var isBusy = false;
  @override
  void initState() {
    super.initState();
    _getAccount();
  }

  void _getAccount() async {
    setState(() {
      isBusy = true;
    });
    var acct =
        await Stellar.getAccount(seed: widget.accountResponse.secretSeed);
    setState(() {
      widget.accountResponse.accountResponse = acct;
      isBusy = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Account: ${widget.accountName}'),
        backgroundColor: Colors.indigo[300],
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: _getAccount,
          ),
        ],
        bottom: PreferredSize(
            child: Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                children: <Widget>[
                  Text(
                    'Details of ${widget.accountName} possibly after a couple of payment transations or the like',
                    style: TextStyle(color: Colors.white),
                  ),
                  SizedBox(
                    height: 28,
                  )
                ],
              ),
            ),
            preferredSize: Size.fromHeight(120)),
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
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: Column(
                          children: <Widget>[
                            SizedBox(
                              height: 20,
                            ),
                            Row(
                              children: <Widget>[
                                Container(
                                  width: 300,
                                  child: Text(
                                    widget.accountResponse.accountResponse
                                        .accountId,
                                    style:
                                        TextStyle(fontWeight: FontWeight.bold),
                                  ),
                                ),
                              ],
                            ),
                            SizedBox(
                              height: 20,
                            ),
                            Row(
                              children: <Widget>[
                                Text(
                                  'Balance',
                                  style: TextStyle(
                                      color: Colors.grey,
                                      fontWeight: FontWeight.w900,
                                      fontSize: 20),
                                ),
                                SizedBox(
                                  width: 12,
                                ),
                                Text(
                                  getFormattedAmount(widget
                                      .accountResponse.accountResponse.balances
                                      .elementAt(0)
                                      .balance, context),
                                  style: TextStyle(
                                      color: Colors.black,
                                      fontWeight: FontWeight.w900,
                                      fontSize: 24),
                                ),
                              ],
                            ),
                            SizedBox(
                              height: 20,
                            ),
                          ],
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 12,
                    ),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: Column(
                          children: <Widget>[
                            SizedBox(
                              height: 20,
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: <Widget>[
                                Text(
                                  'Payments Received',
                                  style: TextStyle(
                                      color: Colors.grey,
                                      fontWeight: FontWeight.w900,
                                      fontSize: 20),
                                ),
                                SizedBox(
                                  width: 12,
                                ),
                                Text(
                                  '${widget.paymentsReceived.length}',
                                  style: TextStyle(
                                      color: Colors.teal[700],
                                      fontWeight: FontWeight.w900,
                                      fontSize: 28),
                                ),
                                SizedBox(
                                  width: 20,
                                ),
                              ],
                            ),
                            SizedBox(
                              height: 28,
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: <Widget>[
                                Text(
                                  'Payments Made',
                                  style: TextStyle(
                                      color: Colors.grey,
                                      fontWeight: FontWeight.w900,
                                      fontSize: 20),
                                ),
                                SizedBox(
                                  width: 12,
                                ),
                                Text(
                                  '${widget.paymentsMade.length}',
                                  style: TextStyle(
                                      color: Colors.pink[700],
                                      fontWeight: FontWeight.w900,
                                      fontSize: 28),
                                ),
                                SizedBox(
                                  width: 20,
                                ),
                              ],
                            ),
                            SizedBox(
                              height: 20,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}
