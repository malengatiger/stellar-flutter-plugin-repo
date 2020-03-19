class AccountResponse {
  String accountId;
  List<Balances> balances;
  Flags flags;
  int lastModifiedLedger;
  Links links;
  int sequenceNumber;
  List<Signers> signers;
//  int subentryCount;
  Thresholds thresholds;
//  int rateLimitLimit;
//  int rateLimitRemaining;
//  int rateLimitReset;

  AccountResponse({
    this.accountId,
    this.balances,
    this.flags,
    this.lastModifiedLedger,
    this.links,
    this.sequenceNumber,
    this.signers,
    this.thresholds,
  });

  AccountResponse.fromJson(Map<String, dynamic> json) {
    accountId = json['accountId'];
    try {
      if (json['balances'] != null) {
        balances = List();
        json['balances'].forEach((v) {
          balances.add(Balances.fromJson(v));
        });
      }

//      flags = json['flags'] != null ?  Flags.fromJson(json['flags']) : null;
//      lastModifiedLedger = json['lastModifiedLedger'];
//      sequenceNumber = json['sequenceNumber'];
      links = json['links'] != null ? Links.fromJson(json['links']) : null;

      if (json['signers'] != null) {
        signers = List<Signers>();
        json['signers'].forEach((v) {
          signers.add(Signers.fromJson(v));
        });
      }
//      subentryCount = json['subentryCount'];
      thresholds = json['thresholds'] != null
          ? Thresholds.fromJson(json['thresholds'])
          : null;
//      rateLimitLimit = json['rateLimitLimit'];
//      rateLimitRemaining = json['rateLimitRemaining'];
//      rateLimitReset = json['rateLimitReset'];
    } catch (e) {
      print(
          'AccountResponse:fromJson: 🔴 ......... the fuckup is UP here somewhere !!! ....');
      throw Exception('AccountResponse: 🔴 Fuckup!!! 🔴 $e 🔴');
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    try {
      data['accountId'] = this.accountId;
      if (this.balances != null) {
        data['balances'] = this.balances.map((v) => v.toJson()).toList();
      }
//      if (this.flags != null) {
//        data['flags'] = this.flags.toJson();
//      }
//      data['lastModifiedLedger'] = this.lastModifiedLedger;
//      if (this.links != null) {
//        data['links'] = this.links.toJson();
//      }
//      data['sequenceNumber'] = this.sequenceNumber;
      if (this.signers != null) {
        data['signers'] = this.signers.map((v) => v.toJson()).toList();
      }
//      data['subentryCount'] = this.subentryCount;
      if (this.thresholds != null) {
        data['thresholds'] = this.thresholds.toJson();
      }
//      data['rateLimitLimit'] = this.rateLimitLimit;
//      data['rateLimitRemaining'] = this.rateLimitRemaining;
//      data['rateLimitReset'] = this.rateLimitReset;
      return data;
    } catch (e) {
      print(e);
      print(
          '🔴 AccountResponse.toJson: ......... the fuckup is up here somewhere!!! .... 🔴 ');
      throw Exception('🔴 🔴 🔴 🔴 🔴 🔴 Fuckup!!! $e 🔴 ');
    }
  }
}

class Balances {
  String assetType;
  String balance;
  String buyingLiabilities;
  String sellingLiabilities;

  Balances(
      {this.assetType,
      this.balance,
      this.buyingLiabilities,
      this.sellingLiabilities});

  Balances.fromJson(Map<String, dynamic> json) {
    assetType = json['assetType'];
    balance = json['balance'];
    buyingLiabilities = json['buyingLiabilities'];
    sellingLiabilities = json['sellingLiabilities'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['assetType'] = this.assetType;
    data['balance'] = this.balance;
    data['buyingLiabilities'] = this.buyingLiabilities;
    data['sellingLiabilities'] = this.sellingLiabilities;
    return data;
  }
}

class Flags {
  bool authImmutable;
  bool authRequired;
  bool authRevocable;

  Flags({this.authImmutable, this.authRequired, this.authRevocable});

  Flags.fromJson(Map<String, dynamic> json) {
    authImmutable = json['authImmutable'];
    authRequired = json['authRequired'];
    authRevocable = json['authRevocable'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['authImmutable'] = this.authImmutable;
    data['authRequired'] = this.authRequired;
    data['authRevocable'] = this.authRevocable;
    return data;
  }
}

class Links {
  Effects effects;
  Effects offers;
  Effects operations;
  Effects self;
  Effects transactions;

  Links(
      {this.effects,
      this.offers,
      this.operations,
      this.self,
      this.transactions});

  Links.fromJson(Map<String, dynamic> json) {
    effects =
        json['effects'] != null ? Effects.fromJson(json['effects']) : null;
    offers = json['offers'] != null ? Effects.fromJson(json['offers']) : null;
    operations = json['operations'] != null
        ? Effects.fromJson(json['operations'])
        : null;
    self = json['self'] != null ? Effects.fromJson(json['self']) : null;
    transactions = json['transactions'] != null
        ? Effects.fromJson(json['transactions'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    if (this.effects != null) {
      data['effects'] = this.effects.toJson();
    }
    if (this.offers != null) {
      data['offers'] = this.offers.toJson();
    }
    if (this.operations != null) {
      data['operations'] = this.operations.toJson();
    }
    if (this.self != null) {
      data['self'] = this.self.toJson();
    }
    if (this.transactions != null) {
      data['transactions'] = this.transactions.toJson();
    }
    return data;
  }
}

class Effects {
  String href;
  bool templated;

  Effects({this.href, this.templated});

  Effects.fromJson(Map<String, dynamic> json) {
    href = json['href'];
    templated = json['templated'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['href'] = this.href;
    data['templated'] = this.templated;
    return data;
  }
}

class Signers {
  String key;
  String type;
  int weight;

  Signers({this.key, this.type, this.weight});

  Signers.fromJson(Map<String, dynamic> json) {
    key = json['key'];
    type = json['type'];
    weight = json['weight'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['key'] = this.key;
    data['type'] = this.type;
    data['weight'] = this.weight;
    return data;
  }
}

class Thresholds {
  int highThreshold;
  int lowThreshold;
  int medThreshold;

  Thresholds({this.highThreshold, this.lowThreshold, this.medThreshold});

  Thresholds.fromJson(Map<String, dynamic> json) {
    highThreshold = json['highThreshold'];
    lowThreshold = json['lowThreshold'];
    medThreshold = json['medThreshold'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['highThreshold'] = this.highThreshold;
    data['lowThreshold'] = this.lowThreshold;
    data['medThreshold'] = this.medThreshold;
    return data;
  }
}
//PublicKeyCredential : GCFGP35BZLFOWAZYUA275S7A5DFVUDIO5DZPWBIRXKHQMKRP2E3ZMBS7
//secret : SASDXSNOHO5CRZPNUEQMPJSRNF6DGLEPKXP2CCRNRNPUISJM2F3DEZND

//PublicKeyCredential : GBNUALXEBAGP6NINA4WD24LSPZMQMDVUBL7IJLBDHWHQD5HGLPETNQ4Q
//secret : SC63NHQJOPJ56VJN5P6M4P3PZ23QOA67OT7HNIIYZRJ4F7XD4VRGXYPB
//Johnny Muller 078 786 9796

//PublicKeyCredential : GCFGP35BZLFOWAZYUA275S7A5DFVUDIO5DZPWBIRXKHQMKRP2E3ZMBS7
//secret : SASDXSNOHO5CRZPNUEQMPJSRNF6DGLEPKXP2CCRNRNPUISJM2F3DEZND

//PublicKeyCredential : GBNUALXEBAGP6NINA4WD24LSPZMQMDVUBL7IJLBDHWHQD5HGLPETNQ4Q
//secret : SC63NHQJOPJ56VJN5P6M4P3PZ23QOA67OT7HNIIYZRJ4F7XD4VRGXYPB
//Johnny Muller 078 786 9796
