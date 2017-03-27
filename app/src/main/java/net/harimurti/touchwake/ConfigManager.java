package net.harimurti.touchwake;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    private SharedPreferences PrefSwitch;
    private SharedPreferences PrefValue;
    private SharedPreferences PrefTemp;

    public ConfigManager(Context context) {
        PrefSwitch = context.getSharedPreferences("Switch", Context.MODE_PRIVATE);
        PrefValue = context.getSharedPreferences("Value", Context.MODE_PRIVATE);
        PrefTemp = context.getSharedPreferences("TempValue", Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String key) {
        return PrefSwitch.getBoolean(key, false);
    }

    public int getInteger(String key) {
        int retValue = PrefValue.getInt(key, 0);
        if (retValue == 0) {
            if (key.toLowerCase().contains("feather"))
                retValue = 20;
            else if (key.toLowerCase().contains("timeout"))
                retValue = 50;
            else if (key.toLowerCase().contains("pressure"))
                retValue = 40;

            setInteger(key, retValue);
        }

        return retValue;
    }

    public int getDefault(String key) {
        int retValue = 0;

        if (key.toLowerCase().contains("feather"))
            retValue = 20;
        else if (key.toLowerCase().contains("timeout"))
            retValue = 50;
        else if (key.toLowerCase().contains("pressure"))
            retValue = 40;

        return retValue;
    }

    public int getTemp(String key) {
        return PrefTemp.getInt(key, 0);
    }

    public void setBoolean(String key, boolean value) {
        PrefSwitch.edit().putBoolean(key, value).apply();
    }

    public void setInteger(String key, int value) {
        PrefValue.edit().putInt(key, value).apply();
    }

    public void setTemp(String key, int value) {
        PrefTemp.edit().putInt(key, value).apply();
    }
}
