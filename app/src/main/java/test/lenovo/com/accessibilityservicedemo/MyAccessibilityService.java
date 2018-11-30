package test.lenovo.com.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;


public class MyAccessibilityService extends AccessibilityService implements Context {

    private final static String TAG = "MyAccessibilityService";

    private static HashMap<PageOrder, Page> pages = new HashMap<PageOrder, Page>();
    public static boolean isMyAccessibilityServiceAlive = false;
    public static boolean isGymChoosen = false;
    public static boolean isTwoHoursChoosen = true;

    public static final String page2_text_default = "游泳馆";
    public static final String page3_text_default = "游泳馆羽毛球";
    public static final String page4_text_defalut = "星期四";

    public static String page2_text = "体育馆";
    public static String page3_text = "副馆羽毛球";
    public static String page4_text = "星期四";
    public static String page5_begin_time = "19:00";
    public static String passwd = null;

    //Activity端是否有新的场地信息设置的提交
    public static boolean isNewSettingsSet = false;

    private final int MSG_WATCHDOG_TIMEOUT = 1;
    private final int TIMEOUT_WATCHDOG = 15*1000;// 500ms
    public Handler watchDogHandler;
    public  static boolean isWatchDogWorking = false;

    //用于控制AccessibilityService的打开和关闭。
    //在Activity界面若用户操作立即开抢时，enable这个Service的处理流程；若用户设置了定时开抢，当开抢时enable这个Service，未到开抢时不做enable操作
    //当预定完成时，disable这个Service。
    // 当预定过程中，用户操作手机强制退出奥体app界面，比如点了home键，disable这个Service
    public static boolean isAccessibilityServiceEnabled = false;

    //

    //保存Activity端新的场地信息设置
    public static void saveSettings(boolean isGymChoosen, String page4_text, String page5_begin_time){

        Log.w(TAG, "saveSettings() called with: isGymChoosen = [" + isGymChoosen + "], page4_text = [" + page4_text + "], page5_begin_time = [" + page5_begin_time + "]");

        OrdinaryPage page2 = (OrdinaryPage) pages.get(PageOrder.PAGE2);
        OrdinaryPage page3 = (OrdinaryPage) pages.get(PageOrder.PAGE3);
        DatePage page4 = (DatePage) pages.get(PageOrder.PAGE4);
        ChooseCourtsPage page5 = (ChooseCourtsPage)pages.get(PageOrder.PAGE5);

        if(isGymChoosen){
            page2.setmSearchTextOrId(page2_text);
            page3.setmSearchTextOrId(page3_text);
        } else {
            page2.setmSearchTextOrId(page2_text_default);
            page3.setmSearchTextOrId(page3_text_default);
        }

        page4.setmWeekDay(Utils.stringToWeekDay(page4_text));
        page5.setBeginTime(page5_begin_time);

    }


    private static Page currentPage;
    public static Page prePage;

    public MyAccessibilityService() {
        Log.w(TAG, "MyAccessibilityService: dingjq2" );
        pages.put(PageOrder.PAGE0, new SkipUpgradePage(PageOrder.PAGE0));//跳过新版本更新，使用旧版本预定，规避提交订单时的验证码
        pages.put(PageOrder.PAGE1, new HomePage("场地预订","com.citycamel.olympic:id/tv_function_title", PageOrder.PAGE1));
        pages.put(PageOrder.PAGE2, new OrdinaryPage("游泳馆", PageOrder.PAGE2));
        pages.put(PageOrder.PAGE3, new OrdinaryPage("游泳馆羽毛球", PageOrder.PAGE3));
        pages.put(PageOrder.PAGE4, new DatePage(WeekDay.星期五, PageOrder.PAGE4, this));
        pages.put(PageOrder.PAGE5, new ChooseCourtsPage(PageOrder.PAGE5, this, page5_begin_time));
        pages.put(PageOrder.PAGE6, new OrdinaryPage("com.citycamel.olympic:id/confirm_btn", PageOrder.PAGE6));
        pages.put(PageOrder.PAGE7, new OrdinaryPage("com.citycamel.olympic:id/venues_order_confirm_ok", PageOrder.PAGE7));
        pages.put(PageOrder.PAGE8, new PayOrderPage(PageOrder.PAGE8));
        pages.put(PageOrder.PAGE9, new InputPwdPage(PageOrder.PAGE9, passwd));
        pages.put(PageOrder.PAGE10, new CompletionPage(PageOrder.PAGE10));

        currentPage = pages.get(PageOrder.PAGE1);
        prePage = currentPage;
        //告诉activity，Service已被enable，可以正常提交预定相关设置，或者可以开始立即预定了
        isMyAccessibilityServiceAlive = true;


        watchDogHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    case MSG_WATCHDOG_TIMEOUT:
                        //if(isForegroundPkgViaDetectionService())
                        resetState();
                        String appNameOfCurrentWindow = getRootInActiveWindow().getPackageName().toString();

                        Log.w(TAG, "handleMessage: getRootInActiveWindow().getPackageName() = " + appNameOfCurrentWindow);
                        if(appNameOfCurrentWindow.equals("com.citycamel.olympic")){//只有当前界面仍然是奥体app的界面，才触发WatchDog，
                            Log.w(TAG, "handleMessage: WatchDog Protection is triggered");
                            Utils.startBookingImmediately(MyAccessibilityService.this, "com.citycamel.olympic");
                        } else {
                            //
                            Log.w(TAG, "handleMessage: isAccessibilityServiceEnabled = "+ isAccessibilityServiceEnabled );
                            isAccessibilityServiceEnabled = false;

                        }

                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "wqy onAccessibilityEvent called, currentPage.getPageOrder().ordinal() = "+currentPage.getPageOrder().ordinal());

        Log.w(TAG, "onAccessibilityEvent: isAccessibilityServiceEnabled = " + isAccessibilityServiceEnabled );
        if(!isAccessibilityServiceEnabled){
            Log.w(TAG, "onAccessibilityEvent: no reservation task, return directly" );
            return;
        };

        //当用户在Activity中做了新的设置，加载相关设置，每次有新的设置，只被执行一次
        if(isNewSettingsSet){

            //加载新的设置时，将Service的当前State进行reset
            resetState();
            saveSettings(MainActivity.isGymChosen, MainActivity.dayOfWeekChosen, MainActivity.beginTimeChosen);
            isNewSettingsSet = false;
        }


        try {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            Log.w(TAG, "onAccessibilityEvent: getRootInActiveWindow().getPackageName() = " + root.getPackageName());

            if (root == null) {
                Log.d(TAG, "wqy root == null, return");
                return;
            }

            watchDogHandler.removeMessages(MSG_WATCHDOG_TIMEOUT);//取消WatchDog
            currentPage.action(this, root, mListener);
            if(isWatchDogWorking){
                watchDogHandler.sendEmptyMessageDelayed(MSG_WATCHDOG_TIMEOUT, TIMEOUT_WATCHDOG);//开启WatchDog
            }

        } catch (Exception e) {
            Log.d(TAG, "wqy Exception occurred:" + e.getMessage());
        }

    }

    public ActionListener mListener = new ActionListener() {
        @Override
        public void onActionSucceed() {
            Log.w(TAG, "onActionSucceed: ");

            if (currentPage.getPageOrder().ordinal() == PageOrder.PAGE6.ordinal()){
                isWatchDogWorking = false;//不再启动WatchDog

            }

            if (currentPage.getPageOrder().ordinal() == PageOrder.PAGE10.ordinal()) {
                //has been the last page, can send successful message now
                Log.d(TAG, "wqy congratulation... book 2 courts successfully!");
                resetState();

                Log.w(TAG, "onActionSucceed currentPage = Page10 : isAccessibilityServiceEnabled = "+ isAccessibilityServiceEnabled );
                isAccessibilityServiceEnabled = false;
                return;
            }
            jumpToNextState();
        }

        @Override
        public void onActionFailed() {
            Log.w(TAG, "onActionFailed: currentPage.getPageOrder().ordinal() = "+currentPage.getPageOrder().ordinal() );
            if ((currentPage.getPageOrder().ordinal() == PageOrder.PAGE4.ordinal() && prePage.getPageOrder().ordinal() == PageOrder.PAGE3.ordinal())
                    || ( currentPage.getPageOrder().ordinal() == PageOrder.PAGE5.ordinal() && prePage.getPageOrder().ordinal() == PageOrder.PAGE4.ordinal())) {
                Log.w(TAG, "onActionFailed: start to jumpToPreviousState");
                jumpToPreviousState();
            }

            if(currentPage.getPageOrder().ordinal() == PageOrder.PAGE8.ordinal()){//当提交订单时遇到被捷足先登的情况时，重启app进行再次预定
                Log.w(TAG, "onActionFailed: 被捷足先登，重启app重新预定");
                resetState();
                Utils.startBookingImmediately(MyAccessibilityService.this, "com.citycamel.olympic");
            }

        }
    };

    public ActionListener getListener() {
        return mListener;
    }

    public static void resetState() {
        currentPage = pages.get(PageOrder.PAGE0);
        prePage = currentPage;
        isWatchDogWorking = false;//disable WatchDog
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "wqy onInterrupt() called");
    }

    @Override
    public void jumpToNextState() {
        Log.w(TAG, "wqy jumpToNextState() called,current state:" + currentPage.getPageOrder().toString());

        PageOrder nextOrder = PageOrder.values()[currentPage.getPageOrder().ordinal() + 1];
        prePage = currentPage;
        currentPage = pages.get(nextOrder);
    }

    @Override
    public void jumpToPreviousState() {
        Log.w(TAG, "jumpToPreviousState called,current state:" + currentPage.getPageOrder().toString());
        PageOrder preOrder = PageOrder.values()[currentPage.getPageOrder().ordinal() - 1];
        prePage = currentPage;
        currentPage = pages.get(preOrder);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "wqy onUnbind() called");
        return true;
    }

    @Override
    public void onDestroy() {
        isMyAccessibilityServiceAlive = false;
        Log.d(TAG, "wqy onDestroy() called");
    }

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "wqy MyAccessibilityService connected. start to schedule timer task... ");
//        long period = 7 * 24 * 60 * 60 * 1000;
//        Utils.startAppAt(this, "com.citycamel.olympic", /*"2018-08-14 16:49:00"*/MainActivity.reservationTime, "yyyy-MM-dd HH:mm:ss", period);
    }



}
