import 'account_query_response.dart';

class PaymentOperationResponse {
  String amount;
  String assetType;
  String from;
  String to;
  String createdAt;
  int id;
  Links links;
  String pagingToken;
  String sourceAccount;
  String transactionHash;
  bool transactionSuccessful;
  String type;
  int rateLimitLimit;
  int rateLimitRemaining;
  int rateLimitReset;

  PaymentOperationResponse(
      {this.amount,
      this.assetType,
      this.from,
      this.to,
      this.createdAt,
      this.id,
      this.links,
      this.pagingToken,
      this.sourceAccount,
      this.transactionHash,
      this.transactionSuccessful,
      this.type,
      this.rateLimitLimit,
      this.rateLimitRemaining,
      this.rateLimitReset});

  PaymentOperationResponse.fromJson(Map<String, dynamic> json) {
    amount = json['amount'];
    assetType = json['assetType'];
    from = json['from'];
    to = json['to'];
    createdAt = json['createdAt'];
    id = json['id'];
    links = json['links'] != null ? new Links.fromJson(json['links']) : null;
    pagingToken = json['pagingToken'];
    sourceAccount = json['sourceAccount'];
    transactionHash = json['transactionHash'];
    transactionSuccessful = json['transactionSuccessful'];
    type = json['type'];
    rateLimitLimit = json['rateLimitLimit'];
    rateLimitRemaining = json['rateLimitRemaining'];
    rateLimitReset = json['rateLimitReset'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['amount'] = this.amount;
    data['assetType'] = this.assetType;
    data['from'] = this.from;
    data['to'] = this.to;
    data['createdAt'] = this.createdAt;
    data['id'] = this.id;
    if (this.links != null) {
      data['links'] = this.links.toJson();
    }
    data['pagingToken'] = this.pagingToken;
    data['sourceAccount'] = this.sourceAccount;
    data['transactionHash'] = this.transactionHash;
    data['transactionSuccessful'] = this.transactionSuccessful;
    data['type'] = this.type;
    data['rateLimitLimit'] = this.rateLimitLimit;
    data['rateLimitRemaining'] = this.rateLimitRemaining;
    data['rateLimitReset'] = this.rateLimitReset;
    return data;
  }
}
