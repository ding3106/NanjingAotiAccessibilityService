package test.lenovo.com.accessibilityservicedemo;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by wangqy5 on 2018/8/6.
 */

public class CompletionPage extends Page {
    private static final String TAG = "CompletionPage";
    public CompletionPage(PageOrder pageOrder) {
        super(pageOrder);
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        Log.w(TAG, "wqy CompletionPage.action() called");
        if (Utils.isSuccessPage(root)) {
            listener.onActionSucceed();
        } else {
            //Toast.makeText(myAccessibilityService, "支付密码设置有误，预定未完成，请尽快处理！", Toast.LENGTH_SHORT).show();
            listener.onActionFailed();
        }
    }
}
