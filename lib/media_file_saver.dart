import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class MediaFileSaver {
  static const MethodChannel _channel =
      const MethodChannel('com.freelancer.flutter.plugins/media_file_saver');

  static Future saveImage(Uint8List imageBytes) async {
    assert(imageBytes != null);
    final result = await _channel.invokeMethod('saveImage', imageBytes);
    return result;
  }

  static Future saveFile(String file) async {
    assert(file != null);
    final result = await _channel.invokeMethod('saveFile', file);
    return result;
  }
}
