package test.lenovo.com.accessibilityservicedemo.TimerReceiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import test.lenovo.com.accessibilityservicedemo.MainActivity;
import test.lenovo.com.accessibilityservicedemo.MyAccessibilityService;
import test.lenovo.com.accessibilityservicedemo.MyApplication;
import test.lenovo.com.accessibilityservicedemo.Utils;

public class TimerReceiver extends BroadcastReceiver {
    private static final String TAG = "TimerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Intent intent_to_start_NanjingOlympicApp = context.getPackageManager().getLaunchIntentForPackage("com.citycamel.olympic");
        intent_to_start_NanjingOlympicApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.d(TAG, "dingjq1 TimerReceiver called, intent_to_start_NanjingOlympicApp = "+intent_to_start_NanjingOlympicApp);
        Utils.wakeUpAndUnlock(context);

        Log.w(TAG, "onReceive1: MyAccessibilityService.isAccessibilityServiceEnabled = "+MyAccessibilityService.isAccessibilityServiceEnabled  );
        MyAccessibilityService.isAccessibilityServiceEnabled = true;

        Log.w(TAG, "onReceive2: MyAccessibilityService.isAccessibilityServiceEnabled = "+MyAccessibilityService.isAccessibilityServiceEnabled  );
        context.startActivity(intent_to_start_NanjingOlympicApp);


    }




}
