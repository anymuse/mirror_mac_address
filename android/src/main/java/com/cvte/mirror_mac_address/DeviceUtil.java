/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */

package com.cvte.mirror_mac_address;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author Rodson
 * @description Utils for getting information of device.
 * @date 12/29/14
 * @since 1.0
 */
public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    public static final String NETWORK_ETHERNET = "eth0";
    public static final String NETWORK_WIFI = "wlan0";

    private static final String DEVICE_MAC = "deviceMac";

    private DeviceUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Get the MAC address.
     *
     * @param context The context of the application
     * @return Return device's mac address
     */
    public static String getDeviceMac(Context context) {
        String deviceMac = initMac(context);
        return TextUtils.isEmpty(deviceMac) ? "" : deviceMac;
    }

    private static String initMac(Context context) {
        String result;

        // Use wifi manager to get mac address
        result = getMACFromWifiManager(context);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }

        // Get ethernet mac address
        result = getMACAddress(NETWORK_ETHERNET);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }

        // Get wifi mac address
        result = getMACAddress(NETWORK_WIFI);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }

        // Get other mac address
        result = getMACAddress(null);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }

        return "";
    }

    private static String getMACFromWifiManager(Context context) {
        android.net.wifi.WifiManager wifi =
                (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getMacAddress();
    }


    private static boolean isMacAddress(String mac) {
        if (mac == null) {
            return false;
        } else {
            String rex = "([0-9a-fA-F]{2}[:-]){5}([0-9a-fA-F]{2})";
            return mac.matches(rex);
        }
    }

    /**
     * Get device id(if null, use mac. If mac is null either, use android id)
     *
     * @param context The context of the application
     * @return Return device's id
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = "";

        try {
            if (telephonyManager != null && checkPermissionGranted(context, Manifest.permission.READ_PHONE_STATE)) {
                deviceId = telephonyManager.getDeviceId();
            }
        } catch (Exception e) {
            Log.d(TAG, "NO DEVICE ID", e);
        }

        if (TextUtils.isEmpty(deviceId)) {
            deviceId = getDeviceMac(context);
        }

        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    private static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null && !interfaceName.equalsIgnoreCase(intf.getName())) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "get Mac Address error.", e);
        }
        return "";
    }

    private static boolean checkPermissionGranted(Context context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
}

