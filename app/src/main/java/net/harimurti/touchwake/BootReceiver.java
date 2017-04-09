package net.harimurti.touchwake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConfigManager config = new ConfigManager(context);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) &&
                Engine.isSupported() && RootShell.isAccessGiven()) {

            boolean dt2w = config.getBoolean("doubletap");
            if (dt2w != Engine.getDoubleTap())
                showToast(context, R.string.toast_dtw, dt2w);
            Engine.setDoubleTap(dt2w);

            boolean s2w = config.getBoolean("sweep");
            if (s2w != Engine.getSweep())
                showToast(context, R.string.toast_stw, s2w);
            Engine.setSweep(s2w);

            Engine.setFeather(config.getInteger("feather"));
            Engine.setTimeout(config.getInteger("timeout"));
            Engine.setPressure(config.getInteger("pressure"));
        }
    }

    private static void showToast(Context context, int stringId, boolean value) {
        Toast.makeText(context,
                String.format(context.getString(stringId), value ? "On" : "Off"),
                Toast.LENGTH_SHORT)
                .show();
    }
}
