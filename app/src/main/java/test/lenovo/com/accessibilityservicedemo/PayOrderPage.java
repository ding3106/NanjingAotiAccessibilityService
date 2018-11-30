package test.lenovo.com.accessibilityservicedemo;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wangqy5 on 2018/8/6.
 */

public class PayOrderPage extends Page {


    public PayOrderPage(PageOrder pageOrder) {
        super(pageOrder);
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        Utils.payOrder(root, listener);
    }


}
