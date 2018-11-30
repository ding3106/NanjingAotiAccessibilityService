package test.lenovo.com.accessibilityservicedemo;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wangqy5 on 2018/8/9.
 */

public class HomePage extends Page {
    private static final String TAG = "HomePage" ;
    private String mText;
    private String mId;

    public HomePage(String text, String id, PageOrder pageOrder) {
        super(pageOrder);
        mText = text;
        mId = id;
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        Log.d(TAG, "wqy HomePage" +  ".action() called");
//        if (Utils.homePageSearch(root, mText, mId)) {
//            listener.onActionSucceed();
//        }
        //MyAccessibilityService.isWatchDogWorking = true;
        Utils.clickByViewId(root, "com.citycamel.olympic:id/main_fragment_bottom_order", listener);

    }
}
