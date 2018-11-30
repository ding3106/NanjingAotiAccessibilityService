package test.lenovo.com.accessibilityservicedemo;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import test.lenovo.com.accessibilityservicedemo.TimerReceiver.TimerReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by wangqy5 on 2018/7/25.
 */

public class Utils {
    private static final String TAG = "Utils";

    public static void clickByText(AccessibilityNodeInfo root, String searchText, ActionListener listener) {
        if (root == null || searchText == null) return;
        Log.d(TAG, "wqy clickByText called, root="+root.getClassName()+" : " + root.getText() + " count=" + root.getChildCount());
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(searchText);
        if (nodeList.size() > 0) {
            Log.d(TAG, "wqy found text:"+searchText);
            nodeList.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            listener.onActionSucceed();
        } else {
            Log.d(TAG, "wqy unable to find text:"+searchText);
            listener.onActionFailed();
        }
    }

    public static void clickByViewId(AccessibilityNodeInfo root, String strId, ActionListener listener) {
        if (root == null || strId == null) return;
        Log.d(TAG, "wqy clickText called, root="+root.getClassName()+" : " + root.getText() + " count=" + root.getChildCount());
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(strId);
        if (nodeList.size() > 0) {
            Log.d(TAG, "wqy find id:"+strId+ "then click");
            nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            listener.onActionSucceed();
        } else {
            Log.d(TAG, "wqy unable to find view id:"+strId);
            listener.onActionFailed();
        }
    }

    public static boolean homePageSearch(AccessibilityNodeInfo root, String text, String id) {
        Log.d(TAG, "wqy homePageSearch() called,");
        if (root == null || text == null || id == null) return false;

        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId(id);
        if (nodeList.size() > 0) {
            Log.d(TAG, "wqy find  id:" + id);
            for(AccessibilityNodeInfo node : nodeList) {
                Log.w(TAG, "homePageSearch: text = "+ text + ", node.getText() = "+ node.getText() );
                if (text.equals(node.getText())) {
                    Log.d(TAG, "wqy find text:" + text + " click to next step");
                        if (node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Log.d(TAG, "wqy performAction succeed");
                        } else {
                            Log.d(TAG, "wqy performAction failed");
                        }

                        return true;
                }
            }
        }
        return false;
    }

    public static boolean findDate(AccessibilityNodeInfo root, WeekDay weekDay) {
        if (root == null || weekDay == null) return false;
        Log.d(TAG, "wqy findDate called, root="+root.getClassName()+" : " + root.getText() + " count=" + root.getChildCount());
        List<AccessibilityNodeInfo> dateList = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/item_view_date");
        Log.d(TAG, "wqy, find id:item_view_data, size=" + dateList.size());
        if (dateList.size() == 3) {// should be 3 days available
            for(AccessibilityNodeInfo date : dateList) {
                List<AccessibilityNodeInfo> nodeList = date.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/item_title");
                if (nodeList.size() > 0) {
                    Log.d(TAG, "wqy find id:item_title and text:" + nodeList.get(0).getText());
                    if (weekDay.toString().equals(nodeList.get(0).getText())) {
                        Log.d(TAG, "wqy find the " + weekDay.toString() + " successfully, click to enter next page");
                        nodeList.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean applyVenues(AccessibilityNodeInfo root, String beginTime)  {
        Log.d(TAG, "wqy applyVenues called");
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/mListView");
        int reverseOrderInList = timeStringToReverseOrder(beginTime);
        Log.w(TAG, "applyVenues: nodeList.size() = "+ nodeList.size());
        if (nodeList.size() > 0) {
            Log.d(TAG, "applyVenues found mListView");
            AccessibilityNodeInfo listView = nodeList.get(0);
            Log.d(TAG, "applyVenues: There are " + listView.getChildCount() + " venues");
            //for (int i = 0; i < listView.getChildCount(); i++) {
            for (int i = listView.getChildCount()-1; i >= 0 ; i--) {
                AccessibilityNodeInfo row = listView.getChild(i);
                Log.d(TAG, "applyVenues: row.classNmae=" + row.getClassName());
                nodeList = row.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/venues_list");
                if (nodeList.size() > 0) {
                    Log.d(TAG, "applyVenues: found id:venues_list");
                    AccessibilityNodeInfo gridView = nodeList.get(0);
                    Log.d(TAG, "applyVenues: there are " + gridView.getChildCount() + " time slot");

                    if(gridView.getChildCount()< reverseOrderInList){
                        Log.w(TAG, "applyVenues: time is illegal,  no available Sport Ground. Your reservation action is too late" );
                        break;
                    }

                    if(MainActivity.isRbTwoHoursChosen){
                        //预定两片场地，根据activity中的保护，预定两片场地时不会传递"20:00"，reverseOrderInList最小值为2
                        List<AccessibilityNodeInfo> foundNodes_1 = gridView.getChild(gridView.getChildCount() - reverseOrderInList).findAccessibilityNodeInfosByText("5");//19:00-20:00
                        List<AccessibilityNodeInfo> foundNodes_2 = gridView.getChild(gridView.getChildCount() - reverseOrderInList+1).findAccessibilityNodeInfosByText("5");//20:00-21:00

                        Log.w(TAG, "applyVenues: foundNodes_1.size = "+foundNodes_1.size()+", foundNodes_2.size = "+foundNodes_2.size() );

                        if (foundNodes_1.size() > 0 && foundNodes_2.size() > 0) {
                            Log.d(TAG, "applyVenues: 2 free venues were found!");
                            foundNodes_1.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            foundNodes_2.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            List<AccessibilityNodeInfo> totalNumber = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/total_number");
                            if (totalNumber.size() > 0) {
                                String strPrice = totalNumber.get(0).getText().toString();//$40
                                Log.d(TAG, "applyVenues: total price is:" + strPrice);
                                int price = Integer.valueOf(strPrice.substring(2));
                                Log.d(TAG, "applyVenues: price:" + price);
                                if (price > 0) { //场地预定好之后场地总价应该大于0，具体价格取决于具体的时间段，周末和工作日貌似都不同
                                    List<AccessibilityNodeInfo> submit = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/btn_submit");
                                    if (submit.size() > 0) {
                                        Log.d(TAG, "applyVenues: 2 venues ready for submit");
                                        submit.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        return true;
                                    }
                                }
                            }

                        } else {
                            Log.w(TAG, "applyVenues: failed, try again");
                        }

                    } else {
                        ///预定一片场地
                        List<AccessibilityNodeInfo> foundNodes_1 = gridView.getChild(gridView.getChildCount() - reverseOrderInList).findAccessibilityNodeInfosByText("5");//19:00-20:00

                        Log.w(TAG, "applyVenues: foundNodes.size = "+foundNodes_1.size());

                        if (foundNodes_1.size() > 0 ) {
                            Log.d(TAG, "applyVenues: 1 free venues were found!");
                            foundNodes_1.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            List<AccessibilityNodeInfo> totalNumber = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/total_number");
                            if (totalNumber.size() > 0) {
                                String strPrice = totalNumber.get(0).getText().toString();//$40
                                Log.d(TAG, "applyVenues: total price is:" + strPrice);
                                int price = Integer.valueOf(strPrice.substring(2));
                                Log.d(TAG, "applyVenues: price:" + price);
                                if (price > 0) { //场地预定好之后场地总价应该大于0，具体价格取决于具体的时间段，周末和工作日貌似都不同
                                    List<AccessibilityNodeInfo> submit = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/btn_submit");
                                    if (submit.size() > 0) {
                                        Log.d(TAG, "applyVenues: 2 venues ready for submit");
                                        submit.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        return true;
                                    }
                                }
                            }

                        } else {
                            Log.w(TAG, "applyVenues: failed, try again");
                        }

                    }

                }
            }
        }

        return false;
    }

    public static void payOrder(AccessibilityNodeInfo root, ActionListener listener) {
        List<AccessibilityNodeInfo> payType = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/iv_pay_type_select");
        if (payType.size() > 0) {
            Log.d(TAG, "wqy, pay type found. payType.size() = " + payType.size());
            if(payType.size() == 2){
                //有两张卡情况下，使用第二张卡付款
                payType.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                payType.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            List<AccessibilityNodeInfo> immediatePay = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/iv_immediate_payment");
            if (immediatePay.size() > 0) {
                Log.d(TAG, "wqy, immediate pay button found! click..");
                immediatePay.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                listener.onActionSucceed();
            }
        }
    }

    public static boolean inputPassword(AccessibilityNodeInfo root, String strPassword) {
        List<AccessibilityNodeInfo> numbers = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/btn_keys");
        if (numbers.size() > 0) {
            Log.d(TAG, "wqy number keyboard is showing, so we can input password now.");
            char[] pwd = strPassword.toCharArray();
            for (int i = 0; i < pwd.length; i++) {
                String numberStr = pwd[i] + "";
                for (AccessibilityNodeInfo node : numbers) {
                    if (numberStr.equals(node.getText())) {
                        Log.d(TAG, "wqy find number:"+numberStr+" to input.");
                        node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
            //after input password, submit for confirm
            List<AccessibilityNodeInfo> payConfirm = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/iv_venues_pay_confirm");
            if (payConfirm.size() > 0) {
                Log.d(TAG, "wqy found pay confirm button. click to finish last step.");
                payConfirm.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }
        }
        return false;
    }

    public static boolean isSuccessPage(AccessibilityNodeInfo root) {
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText("支付成功");
        if (nodeList.size() > 0) {
            Log.d(TAG, "wqy congratulation! enter success page");
            return true;
        }
        return false;
    }

    public static void startBookingImmediately(final Context context, final String packageName) {


        if(!isAppInstalled(context, packageName) ){
            Toast.makeText(context, "奥体app未安装！\n请安装后重试！", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "dingjq1 startBookingImmediately called, intent="+intent.getPackage());
        context.startActivity(intent);
    }

    private static boolean isAppInstalled(final Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        boolean installed =false;
        try{
            pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            installed =true;
        }catch(PackageManager.NameNotFoundException e){
            installed =false;
        }
        return installed;
    }


    public static void cancelReservationToStartApp(final Context context, final String packageName){

        Log.w(TAG, "cancelReservationToStartApp: called" );

        if(!isAppInstalled(context, packageName) ){
            Toast.makeText(context, "奥体app未安装！\n请安装后重试！", Toast.LENGTH_LONG).show();
            return;
        }

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);//获取AlarmManager实例

        Intent intent2 = new Intent(context, TimerReceiver.class);
        intent2.setAction("BroadcastReceiver_to_Start_Reservation");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent2, 0);

        Log.w(TAG, "startAppAt: start TimerReceiver to handle");
        manager.cancel(pi);//取消之前提交的预定

    }

    public static void startAppAt(final Context context, final String packageName, String strDate, String format, long period) {
        Log.d(TAG,"wqy startAppAt() called()");
        if(strDate == null) return;

        if(!isAppInstalled(context, packageName) ){
            Toast.makeText(context, "奥体app未安装！\n请安装后重试！", Toast.LENGTH_LONG).show();
            return;
        }
/*        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(strDate);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d(TAG, "wqy startActivity(intent) called, intent="+intent.getPackage());
                    wakeUpAndUnlock(context);
                    context.startActivity(intent);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, date, period);
            //timer.schedule(task, 5*60*1000, period);
            //timer.schedule(task, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);//获取AlarmManager实例

        Intent intent2 = new Intent(context, TimerReceiver.class);
        intent2.setAction("BroadcastReceiver_to_Start_Reservation");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent2, 0);

        Log.w(TAG, "startAppAt: start TimerReceiver to handle");
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, MainActivity.timeToStartReservation.getTimeInMillis(), pi);//开启提醒

    }

    public static void wakeUpAndUnlock(Context context) {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean screenOn = pm.isInteractive();// Same function as PowerManager.isScreenOn() which will be deprecated
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            Log.d(TAG, "wqy wakeup screen");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        //if (keyguardManager.isKeyguardLocked()) {
            KeyguardManager.KeyguardLock kl = keyguardManager.newKeyguardLock("unlock");
            Log.d(TAG, "wqy disableKeyguard()called");
            kl.reenableKeyguard();
            kl.disableKeyguard();
        //}

    }

    public static boolean isText(String s) {
        if (s.contains("com.citycamel.olympic:id")) return false;
        return true;
    }


    public static WeekDay stringToWeekDay(String weekDay_string){
        WeekDay weekDay = WeekDay.星期四;

        switch (weekDay_string){
            case "星期日":
                weekDay = WeekDay.星期日;
                break;
            case "星期一":
                weekDay = WeekDay.星期一;
                break;
            case "星期二":
                weekDay = WeekDay.星期二;
                break;
            case "星期三":
                weekDay = WeekDay.星期三;
                break;
            case "星期四":
                weekDay = WeekDay.星期四;
                break;
            case "星期五":
                weekDay = WeekDay.星期五;
                break;
            case "星期六":
                weekDay = WeekDay.星期六;
                break;
            default:
                weekDay = WeekDay.星期四;
                break;

        }
        return weekDay;

    }

    //将activity中用户选定的时间值字符串转换成该时间点在List中的逆序号，例如，如果预定20:00的场地，逆序号就是1
    public static int timeStringToReverseOrder(String time){
        int order = 2;
        switch (time){
            case "20:00":
                order = 1;
                break;
            case "19:00":
                order = 2;
                break;
            case "18:00":
                order = 3;
                break;
            case "17:00":
                order = 4;
                break;
            case "16:00":
                order = 5;
                break;
            case "15:00":
                order = 6;
                break;
            case "14:00":
                order = 7;
                break;
            case "13:00":
                order = 8;
                break;
            case "12:00":
                order = 9;
                break;
            case "11:00":
                order = 10;
                break;
            case "10:00":
                order = 11;
                break;
            case "09:00":
                order = 12;
                break;
            default:
                order = 2;
                break;
        }

        return order;
    }

}
