package test.lenovo.com.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by wangqy5 on 2018/8/6.
 */

public class ChooseCourtsPage extends Page {
    private static final String TAG = "ChooseCourtsPage";
    private MyAccessibilityService mService;
    private String beginTime;


    private final static int MSG_reserve_fail_TIMEOUT = 1;
    private final static int TIMEOUT_reserve_fail = 1000;// 500ms
    private  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_reserve_fail_TIMEOUT:
                    Log.w(TAG, "handleMessage: perform BACK action");
                    mService.mListener.onActionFailed();
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    break;
                default:
                    break;
            }
        }
    };


    public ChooseCourtsPage(PageOrder pageOrder, MyAccessibilityService mService, String beginTime) {
        super(pageOrder);
        this.mService = mService;
        this.beginTime = beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        Log.d(TAG, "wqy Page5.action() called");
        mHandler.removeMessages(MSG_reserve_fail_TIMEOUT);

        if(isChooseCourtsPageReady(root)){    //场地列表成功显示出来

            if (Utils.applyVenues(root, beginTime)) {   //预定成功
                listener.onActionSucceed();
            }else {                          //没有空余场地，预定失败，设置延时操作：返回到上一界面，循环预定场地动作直到预定成功
                Log.w(TAG, "action: reserve field failed");
                //listener.onActionFailed();
                mHandler.sendEmptyMessageDelayed(MSG_reserve_fail_TIMEOUT, TIMEOUT_reserve_fail);

            }
        }

    }

    /**
     *     判断界面因为网络刷新是否稳定下来，是否已显示场地列表
     */
    private boolean isChooseCourtsPageReady(AccessibilityNodeInfo root){
        List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByViewId("com.citycamel.olympic:id/mListView");//当界面稳定下来后，mListView这个id对应的根view总是会出现，即使6点钟之后当天所有场地都被显示
        if (nodeList.size() > 0) {
            Log.w(TAG, "isChooseCourtsPageReady: nodeList.size() = "+ nodeList.size() );
            Log.w(TAG, "isChooseCourtsPageReady: true");
            return true;
        }
        Log.w(TAG, "isChooseCourtsPageReady: false");
        return false;
    }

}
