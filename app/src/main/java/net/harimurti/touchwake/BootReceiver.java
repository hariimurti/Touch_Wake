package net.harimurti.touchwake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stericson.RootShell.RootShell;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConfigManager config = new ConfigManager(context);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) &&
                Engine.isSupported() && RootShell.isAccessGiven()) {
            Engine.setDoubleTap(config.getBoolean("doubletap"));
            Engine.setSweep(config.getBoolean("sweep"));
            Engine.setFeather(config.getInteger("feather"));
            Engine.setTimeout(config.getInteger("timeout"));
            Engine.setPressure(config.getInteger("pressure"));
        }
    }
}
