package net.harimurti.touchwake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class CustomDialog {
    static int seekMax = 100;
    static int seekMin = 0;

    public static void setValue(final Context context, String title, final String key) {
        final ConfigManager config = new ConfigManager(context);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_set);
        dialog.setTitle(R.string.label_advanced);

        TextView textSet = (TextView) dialog.findViewById(R.id.label_set);
        textSet.setText(title);

        final TextView textValue = (TextView) dialog.findViewById(R.id.label_value);
        final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);

        int configValue = config.getInteger(key);
        config.setTemp(key, configValue);

        if (key.contains("feather")) {
            seekMax = 100;
            seekMin = 5;
        } else if (key.contains("timeout")) {
            seekMax = 200;
            seekMin = 10;
        } else {
            seekMax = 100;
            seekMin = 0;
        }

        seekBar.setMax(seekMax - seekMin);
        seekBar.setProgress(configValue - seekMin);
        textValue.setText(Integer.toString(configValue));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = seekMin + progress;
                textValue.setText(Integer.toString(progress));
                config.setTemp(key, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Button dialogReset = (Button) dialog.findViewById(R.id.btnReset);
        dialogReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setValue = config.getDefault(key);
                config.setInteger(key, setValue);

                setEngine(key, setValue);

                sendBroadcast(context, key, Integer.toString(setValue));
                dialog.dismiss();
            }
        });

        Button dialogSave = (Button) dialog.findViewById(R.id.btnSave);
        dialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setValue = config.getTemp(key);
                config.setInteger(key, setValue);

                setEngine(key, setValue);

                sendBroadcast(context, key, Integer.toString(setValue));
                dialog.dismiss();
            }
        });

        Button dialogCancel = (Button) dialog.findViewById(R.id.btnCancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialogMaxWindow(dialog);
    }

    public static void showAbout(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setTitle("About");

        TextView labelApp = (TextView) dialog.findViewById(R.id.label_about);
        try {
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            labelApp.setText(context.getString(R.string.app_name) + " v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Button dialogVisit = (Button) dialog.findViewById(R.id.btnVisit);
        Button dialogCancel = (Button) dialog.findViewById(R.id.btnClose);

        dialogVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWebsite = new Intent(Intent.ACTION_VIEW);
                openWebsite.setData(Uri.parse("http://harimurti.net"));
                context.startActivity(openWebsite);
                dialog.dismiss();
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialogMaxWindow(dialog);
    }

    private static void dialogMaxWindow(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private static void setEngine(String key, int value) {
        if (key.contains("feather"))
            Engine.setFeather(value);
        else if (key.contains("timeout"))
            Engine.setTimeout(value);
        else if (key.contains("pressure"))
            Engine.setPressure(value);
    }

    private static void sendBroadcast(Context context, String key, String value){
        Intent intent = new Intent("CustomDialog");
        intent.putExtra(key, value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
