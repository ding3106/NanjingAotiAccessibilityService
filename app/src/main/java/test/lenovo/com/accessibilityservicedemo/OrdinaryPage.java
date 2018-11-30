package test.lenovo.com.accessibilityservicedemo;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by wangqy5 on 2018/8/3.
 */

public class OrdinaryPage extends Page {
    private static final String TAG = "OrdinaryPage" ;
    private String mSearchTextOrId;

    public void setmSearchTextOrId(String mSearchTextOrId) {
        this.mSearchTextOrId = mSearchTextOrId;
    }

    public OrdinaryPage(String searchTextOrId, PageOrder pageOrder) {
        super(pageOrder);
        mSearchTextOrId = searchTextOrId;
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        Log.d(TAG, "wqy "+ pageOrder+ ".action() called");
        if (Utils.isText(mSearchTextOrId)) {
            Utils.clickByText(root, mSearchTextOrId, listener);
        } else {
            Utils.clickByViewId(root, mSearchTextOrId, listener);
        }
    }
}
