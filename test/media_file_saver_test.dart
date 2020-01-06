import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:media_file_saver/media_file_saver.dart';

void main() {
  const MethodChannel channel = MethodChannel('media_file_saver');

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
    expect(await MediaFileSaver.platformVersion, '42');
  });
}
