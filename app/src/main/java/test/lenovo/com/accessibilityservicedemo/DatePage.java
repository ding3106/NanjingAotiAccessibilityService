package test.lenovo.com.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import android.os.Handler;

import java.util.List;


/**
 * Created by wangqy5 on 2018/8/9.
 */

public class DatePage extends Page {
    private static final String TAG = "DatePage";
    private WeekDay mWeekDay;
    private final static int MSG_FIND_DATE_TIMEOUT = 1;
    private final static int TIMEOUT_FIND_DATE_MS = 1000;// 500ms


    private MyAccessibilityService mService;

    public DatePage(PageOrder pageOrder, MyAccessibilityService service) {
        this(WeekDay.星期四, pageOrder, service);
    }

    public void setmWeekDay(WeekDay mWeekDay) {
        this.mWeekDay = mWeekDay;
    }

    public DatePage(WeekDay weekDay, PageOrder pageOrder, MyAccessibilityService service) {
        super(pageOrder);
        mWeekDay = weekDay;
        mService = service;
    }


    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {

        mHandler.removeMessages(MSG_FIND_DATE_TIMEOUT);

        if(isDatePageReady(root)){

            if(Utils.findDate(root, mWeekDay)){
                listener.onActionSucceed();
            } else {
                if(myAccessibilityService.prePage.getPageOrder().ordinal() != PageOrder.PAGE5.ordinal()){
                    Log.w(TAG, "action: send delay Message to go back to previous Page 3");
                    mHandler.sendEmptyMessageDelayed(MSG_FIND_DATE_TIMEOUT, TIMEOUT_FIND_DATE_MS);
                }
            }

        }


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_FIND_DATE_TIMEOUT:
                    backToPrePage();
            }
        }
    };

    private void backToPrePage() {
        Log.d(TAG, "wqy could not find Date:"+ mWeekDay.toString()+" in 500ms, so back to prePage for another try");
        mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        mService.getListener().onActionFailed();
    }

    private boolean isDatePageReady(AccessibilityNodeInfo root){

        if (root == null || mWeekDay == null) {
            Log.w(TAG, "isDatePageReady: root = "+ root +", mWeekDay = "+mWeekDay );
            return false;
        }
        Log.d(TAG, "isDatePageReady: root="+root.getClassName()+" : " + root.getText() + " count=" + root.getChildCount());
        List<AccessibilityNodeInfo> dateList = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/item_view_date");
        Log.d(TAG, "isDatePageReady: size=" + dateList.size());
        if (dateList.size() > 0) {// should be 3 days available
            Log.w(TAG, "isDatePageReady: true");
            return true;

        }
        Log.w(TAG, "isDatePageReady: false");
        return false;
    }
}
