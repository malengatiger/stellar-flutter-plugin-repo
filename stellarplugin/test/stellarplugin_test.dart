import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:stellarplugin/stellarplugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('stellarplugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Stellarplugin.platformVersion, '42');
  });
}
