package test.lenovo.com.accessibilityservicedemo;

import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wangqy5 on 2018/8/6.
 */

public class InputPwdPage extends Page {
    private String pwd;


    public InputPwdPage(PageOrder pageOrder, String pwd) {
        super(pageOrder);
        this.pwd = pwd;
    }

    @Override
    public void action(MyAccessibilityService myAccessibilityService, AccessibilityNodeInfo root, ActionListener listener) {
        //String pwd = "016218";
        SharedPreferences preference = myAccessibilityService.getSharedPreferences("passwd_sharedPref", MODE_PRIVATE);
        String pwd = preference.getString("passwd", "");

        if(pwd.length() != 6){
            Toast.makeText(myAccessibilityService, "支付密码未设置，预定无法完成，请及时处理！", Toast.LENGTH_SHORT).show();
            listener.onActionFailed();
            return;
        }

        if (Utils.inputPassword(root, pwd)) {
            listener.onActionSucceed();
        } else {
            listener.onActionFailed();
        }
    }
}
