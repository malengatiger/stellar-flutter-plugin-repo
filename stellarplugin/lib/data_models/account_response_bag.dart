import 'package:stellarplugin/data_models/account_response.dart';

class AccountResponseBag {
  String secretSeed;
  AccountResponse accountResponse;

  AccountResponseBag({this.secretSeed, this.accountResponse});

  AccountResponseBag.fromJson(Map<String, dynamic> json) {
    secretSeed = json['secretSeed'];
    accountResponse = AccountResponse.fromJson(json['accountResponse']);
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['secretSeed'] = this.secretSeed;
    data['accountResponse'] = this.accountResponse.toJson();
    return data;
  }
}
