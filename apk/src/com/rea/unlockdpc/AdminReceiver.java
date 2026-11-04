package com.rea.unlockdpc;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

/**
 * Enables USB/Wi-Fi adb as soon as this package is device owner.
 * setGlobalSetting() needs device owner, which is only set after onEnabled(),
 * so retry in the background until it succeeds.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "ReaUnlockDpc";
    private static final int MAX_TRIES = 120;
    private static final long DELAY_MS = 500L;

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.i(TAG, "onEnabled");
        enableAdbWhenOwner(context.getApplicationContext(), 0);
    }

    static void enableAdbWhenOwner(final Context ctx, final int attempt) {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final ComponentName admin = new ComponentName(ctx, AdminReceiver.class);

        if (dpm != null && dpm.isDeviceOwnerApp(ctx.getPackageName())) {
            try {
                dpm.setGlobalSetting(admin, Settings.Global.ADB_ENABLED, "1");
                Log.i(TAG, "ADB_ENABLED set");
            } catch (Throwable t) {
                Log.e(TAG, "ADB_ENABLED failed", t);
            }
            try {
                dpm.setGlobalSetting(admin, "adb_wifi_enabled", "1");
                Log.i(TAG, "ADB_WIFI_ENABLED set");
            } catch (Throwable t) {
                Log.e(TAG, "ADB_WIFI failed", t);
            }
            try {
                IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
                filter.addCategory(Intent.CATEGORY_HOME);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                dpm.addPersistentPreferredActivity(admin, filter,
                        new ComponentName(ctx, MainActivity.class));
                Log.i(TAG, "home activity pinned");
            } catch (Throwable t) {
                Log.e(TAG, "pin home failed", t);
            }
            return;
        }
        if (attempt >= MAX_TRIES) {
            Log.w(TAG, "gave up waiting for device owner");
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> enableAdbWhenOwner(ctx, attempt + 1), DELAY_MS);
    }
}
