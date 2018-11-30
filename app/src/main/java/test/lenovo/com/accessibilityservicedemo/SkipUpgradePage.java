package test.lenovo.com.accessibilityservicedemo;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

public class SkipUpgradePage extends Page {
    private static final String TAG = "SkipUpgradePage";

    public SkipUpgradePage(PageOrder pageOrder) {
        super(pageOrder);
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {

        Log.w(TAG, "action: in SkipUpgradePage");

        MyAccessibilityService.isWatchDogWorking = true;//打开WatchDog
        Utils.clickByViewId(root, "com.citycamel.olympic:id/btnCancel", listener);
    }
}
