package test.lenovo.com.accessibilityservicedemo;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wangqy5 on 2018/7/25.
 */

public interface State {
    void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener);
}
