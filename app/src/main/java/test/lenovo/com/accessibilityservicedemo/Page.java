package test.lenovo.com.accessibilityservicedemo;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wangqy5 on 2018/8/3.
 */

public abstract class Page implements State {

    protected PageOrder pageOrder;
    private State nextState;
    private State preState;

    public Page(PageOrder pageOrder) {
        this.pageOrder = pageOrder;
    }

    public PageOrder getPageOrder() {
        return pageOrder;
    }

    public abstract void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener);

}
