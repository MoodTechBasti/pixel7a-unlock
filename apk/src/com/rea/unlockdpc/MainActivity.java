package com.rea.unlockdpc;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Shows the current state and re-triggers adb enabling. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        LinearLayout log = new LinearLayout(this);
        log.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        log.setPadding(pad, pad, pad, pad);
        scroll.addView(log);
        setContentView(scroll);

        DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean owner = dpm != null && dpm.isDeviceOwnerApp(getPackageName());

        say(log, "REA Unlock DPC");
        say(log, "deviceOwner=" + owner);
        say(log, "adb_enabled=" + Settings.Global.getInt(
                getContentResolver(), Settings.Global.ADB_ENABLED, -1));
        say(log, "adb_wifi_enabled=" + Settings.Global.getInt(
                getContentResolver(), "adb_wifi_enabled", -1));
        say(log, "flash.locked=" + prop("ro.boot.flash.locked"));

        if (owner) {
            AdminReceiver.enableAdbWhenOwner(getApplicationContext(), 0);
            say(log, "ADB erneut angefordert.");
        } else {
            say(log, "Noch kein Device Owner.");
        }
    }

    private void say(LinearLayout log, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextIsSelectable(true);
        log.addView(tv);
    }

    private static String prop(String key) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Object v = c.getMethod("get", String.class).invoke(null, key);
            return String.valueOf(v);
        } catch (Throwable t) {
            return "?";
        }
    }
}
