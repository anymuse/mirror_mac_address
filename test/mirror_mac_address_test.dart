import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
// import 'package:mirror_mac_address/mirror_mac_address.dart';

void main() {
  const MethodChannel channel = MethodChannel('mirror_mac_address');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  // test('getPlatformVersion', () async {
  //   expect(await MirrorMacAddress.platformVersion, '42');
  // });
}
