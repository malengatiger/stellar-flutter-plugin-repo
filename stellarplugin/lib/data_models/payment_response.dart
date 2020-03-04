class PaymentResponse {
  Links links;
  String hash;
  int ledger;
  String envelopeXdr;
  String resultXdr;
  String resultMetaXdr;

  PaymentResponse(
      {this.links,
      this.hash,
      this.ledger,
      this.envelopeXdr,
      this.resultXdr,
      this.resultMetaXdr});

  PaymentResponse.fromJson(Map<String, dynamic> json) {
    links = json['_links'] != null ? new Links.fromJson(json['_links']) : null;
    hash = json['hash'];
    ledger = json['ledger'];
    envelopeXdr = json['envelope_xdr'];
    resultXdr = json['result_xdr'];
    resultMetaXdr = json['result_meta_xdr'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.links != null) {
      data['_links'] = this.links.toJson();
    }
    data['hash'] = this.hash;
    data['ledger'] = this.ledger;
    data['envelope_xdr'] = this.envelopeXdr;
    data['result_xdr'] = this.resultXdr;
    data['result_meta_xdr'] = this.resultMetaXdr;
    return data;
  }
}

class Links {
  Transaction transaction;
  Links({this.transaction});

  Links.fromJson(Map<String, dynamic> json) {
    transaction = json['transaction'] != null
        ? new Transaction.fromJson(json['transaction'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.transaction != null) {
      data['transaction'] = this.transaction.toJson();
    }
    return data;
  }
}

class Transaction {
  String href;
  Transaction({this.href});

  Transaction.fromJson(Map<String, dynamic> json) {
    href = json['href'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['href'] = this.href;
    return data;
  }
}
