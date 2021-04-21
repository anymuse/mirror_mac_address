import 'dart:async';

import 'package:device_info/device_info.dart';
import 'package:flutter/services.dart';

class MirrorMacAddress {
  static const MethodChannel _channel =
      const MethodChannel('mirror_mac_address');

  static const _mirror_model = 'm43c01';

  String _mac;

  /// Get MAC address from Mirror.
  ///
  /// Return an empty [String] if the device is not a Mirror.
  Future<String> get() async => _mac ??= await _get();

  Future<String> _get() async {
    try {
      final deviceInfo = DeviceInfoPlugin();
      final androidInfo = await deviceInfo.androidInfo;
      if (androidInfo.model.toLowerCase() != _mirror_model) return '';

      return await _channel.invokeMethod('get');
    } catch (e) {
      return e.toString();
    }
  }
}
