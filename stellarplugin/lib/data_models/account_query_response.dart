class TransactionQueryResponse {
  LinksQ lLinks;
  Embedded eEmbedded;

  TransactionQueryResponse({this.lLinks, this.eEmbedded});

  TransactionQueryResponse.fromJson(Map<String, dynamic> json) {
    lLinks =
        json['_links'] != null ? new LinksQ.fromJson(json['_links']) : null;
    eEmbedded = json['_embedded'] != null
        ? new Embedded.fromJson(json['_embedded'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.lLinks != null) {
      data['_links'] = this.lLinks.toJson();
    }
    if (this.eEmbedded != null) {
      data['_embedded'] = this.eEmbedded.toJson();
    }
    return data;
  }
}

class LinksQ {
  Self self;
  Self next;
  Self prev;

  LinksQ({this.self, this.next, this.prev});

  LinksQ.fromJson(Map<String, dynamic> json) {
    self = json['self'] != null ? new Self.fromJson(json['self']) : null;
    next = json['next'] != null ? new Self.fromJson(json['next']) : null;
    prev = json['prev'] != null ? new Self.fromJson(json['prev']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.self != null) {
      data['self'] = this.self.toJson();
    }
    if (this.next != null) {
      data['next'] = this.next.toJson();
    }
    if (this.prev != null) {
      data['prev'] = this.prev.toJson();
    }
    return data;
  }
}

class Self {
  String href;

  Self({this.href});

  Self.fromJson(Map<String, dynamic> json) {
    href = json['href'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['href'] = this.href;
    return data;
  }
}

class Embedded {
  List<Records> records;

  Embedded({this.records});

  Embedded.fromJson(Map<String, dynamic> json) {
    if (json['records'] != null) {
      records = new List<Records>();
      json['records'].forEach((v) {
        records.add(new Records.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.records != null) {
      data['records'] = this.records.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Records {
  LinksQ lLinks;
  String id;
  String pagingToken;
  bool successful;
  String hash;
  int ledger;
  String createdAt;
  String sourceAccount;
  String sourceAccountSequence;
  int feeCharged;
  int maxFee;
  int operationCount;
  String envelopeXdr;
  String resultXdr;
  String resultMetaXdr;
  String feeMetaXdr;
  String memoType;
  List<String> signatures;
  String validAfter;
  String memo;

  Records(
      {this.lLinks,
      this.id,
      this.pagingToken,
      this.successful,
      this.hash,
      this.ledger,
      this.createdAt,
      this.sourceAccount,
      this.sourceAccountSequence,
      this.feeCharged,
      this.maxFee,
      this.operationCount,
      this.envelopeXdr,
      this.resultXdr,
      this.resultMetaXdr,
      this.feeMetaXdr,
      this.memoType,
      this.signatures,
      this.validAfter,
      this.memo});

  Records.fromJson(Map<String, dynamic> json) {
    lLinks =
        json['_links'] != null ? new LinksQ.fromJson(json['_links']) : null;
    id = json['id'];
    pagingToken = json['paging_token'];
    successful = json['successful'];
    hash = json['hash'];
    ledger = json['ledger'];
    createdAt = json['created_at'];
    sourceAccount = json['source_account'];
    sourceAccountSequence = json['source_account_sequence'];
    feeCharged = json['fee_charged'];
    maxFee = json['max_fee'];
    operationCount = json['operation_count'];
    envelopeXdr = json['envelope_xdr'];
    resultXdr = json['result_xdr'];
    resultMetaXdr = json['result_meta_xdr'];
    feeMetaXdr = json['fee_meta_xdr'];
    memoType = json['memo_type'];
    signatures = json['signatures'].cast<String>();
    validAfter = json['valid_after'];
    memo = json['memo'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.lLinks != null) {
      data['_links'] = this.lLinks.toJson();
    }
    data['id'] = this.id;
    data['paging_token'] = this.pagingToken;
    data['successful'] = this.successful;
    data['hash'] = this.hash;
    data['ledger'] = this.ledger;
    data['created_at'] = this.createdAt;
    data['source_account'] = this.sourceAccount;
    data['source_account_sequence'] = this.sourceAccountSequence;
    data['fee_charged'] = this.feeCharged;
    data['max_fee'] = this.maxFee;
    data['operation_count'] = this.operationCount;
    data['envelope_xdr'] = this.envelopeXdr;
    data['result_xdr'] = this.resultXdr;
    data['result_meta_xdr'] = this.resultMetaXdr;
    data['fee_meta_xdr'] = this.feeMetaXdr;
    data['memo_type'] = this.memoType;
    data['signatures'] = this.signatures;
    data['valid_after'] = this.validAfter;
    data['memo'] = this.memo;
    return data;
  }
}

class Links {
  Self self;
  Self account;
  Self ledger;
  Operations operations;
  Operations effects;
  Self precedes;
  Self succeeds;

  Links(
      {this.self,
      this.account,
      this.ledger,
      this.operations,
      this.effects,
      this.precedes,
      this.succeeds});

  Links.fromJson(Map<String, dynamic> json) {
    self = json['self'] != null ? new Self.fromJson(json['self']) : null;
    account =
        json['account'] != null ? new Self.fromJson(json['account']) : null;
    ledger = json['ledger'] != null ? new Self.fromJson(json['ledger']) : null;
    operations = json['operations'] != null
        ? new Operations.fromJson(json['operations'])
        : null;
    effects = json['effects'] != null
        ? new Operations.fromJson(json['effects'])
        : null;
    precedes =
        json['precedes'] != null ? new Self.fromJson(json['precedes']) : null;
    succeeds =
        json['succeeds'] != null ? new Self.fromJson(json['succeeds']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.self != null) {
      data['self'] = this.self.toJson();
    }
    if (this.account != null) {
      data['account'] = this.account.toJson();
    }
    if (this.ledger != null) {
      data['ledger'] = this.ledger.toJson();
    }
    if (this.operations != null) {
      data['operations'] = this.operations.toJson();
    }
    if (this.effects != null) {
      data['effects'] = this.effects.toJson();
    }
    if (this.precedes != null) {
      data['precedes'] = this.precedes.toJson();
    }
    if (this.succeeds != null) {
      data['succeeds'] = this.succeeds.toJson();
    }
    return data;
  }
}

class Operations {
  String href;
  bool templated;

  Operations({this.href, this.templated});

  Operations.fromJson(Map<String, dynamic> json) {
    href = json['href'];
    templated = json['templated'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['href'] = this.href;
    data['templated'] = this.templated;
    return data;
  }
}
