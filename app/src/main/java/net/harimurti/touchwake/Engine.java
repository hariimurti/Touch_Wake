package net.harimurti.touchwake;

import android.util.Log;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeoutException;

public class Engine {
    private static String main_path = "/sys/android_touch/";
    private static String doubletap = "doubletap_wake";
    private static String sweep = "sweep_wake";
    private static String feather = "doubletap_feather";
    private static String timeout = "doubletap_timeout";
    private static String pressure = "doubletap_pressure";

    public static boolean isSupported() {
        File pDtw = new File(main_path + doubletap);
        File pStw = new File(main_path + sweep);
        File pFeather = new File(main_path + feather);
        File pTimeout = new File(main_path + timeout);
        File pPressure = new File(main_path + pressure);

        return pDtw.exists() && pStw.exists() && pFeather.exists() && pTimeout.exists() && pPressure.exists();
    }

    public static boolean getDoubleTap() {
        String value = getFromFile(doubletap);
        if (value != null)
            return value.contains("1");
        else
            return false;
    }

    public static boolean getSweep() {
        String value = getFromFile(sweep);
        if (value != null)
            return value.contains("1");
        else
            return false;
    }

    public static int getFeather() {
        return Integer.parseInt(getFromFile(feather));
    }

    public static int getTimeout() {
        return Integer.parseInt(getFromFile(timeout));
    }

    public static int getPressure() {
        return Integer.parseInt(getFromFile(pressure));
    }

    public static void setDoubleTap(boolean value) {
        setToFile(value ? "1" : "0", doubletap);
    }

    public static void setSweep(boolean value) {
        setToFile(value ? "1" : "0", sweep);
    }

    public static void setFeather(int value) {
        setToFile(Integer.toString(value), feather);
    }

    public static void setTimeout(int value) {
        setToFile(Integer.toString(value), timeout);
    }

    public static void setPressure(int value) {
        setToFile(Integer.toString(value), pressure);
    }

    private static String getFromFile(String file) {
        String pathFile = main_path + file;
        String retValue = "0";
        try {
            Process suProcess = Runtime.getRuntime().exec("su");

            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(suProcess.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));

            os.write("cat " + pathFile + "\n");
            os.flush();

            String line = reader.readLine();
            if (null != line) {
                retValue = line;
            }

            os.write("exit\n");
            os.flush();
            os.close();
        }
        catch (Exception e) {
            Log.e("RootShell", "Error: " + e.getMessage());
        }

        return retValue;
    }

    private static void setToFile(String value, String file) {
        String pathFile = main_path + file;
        Command command = new Command(0, "echo " + value + " > " + pathFile);
        try {
            RootShell.getShell(true).add(command);
        } catch (TimeoutException | RootDeniedException | IOException e) {
            Log.e("RootShell", "Error: " + e.getMessage());
        }
    }

    public static String getKernelInfo() {
        String retval = null;

        try {
            FileInputStream inputStream = new FileInputStream(new File("/proc/version"));

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String receiveString;

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            retval = stringBuilder.toString();
        }
        catch (FileNotFoundException e) {
            Log.e("KernelInfo", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("KernelInfo", "Can't read file: " + e.toString());
        }

        return retval;
    }
}
