
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterScanPlugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_scan_plugin');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
