package test.lenovo.com.accessibilityservicedemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import test.lenovo.com.accessibilityservicedemo.passwd.PasswdActivity;
import test.lenovo.com.accessibilityservicedemo.util.HttpUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";


    private ImageView bingPicImg;
    private Button bt_setting_submit, bt_cancel_setting, bt_action_immediately;


    private RadioGroup rg_Venue, rg_timeLength;
    private RadioButton rb_Swimming, rb_Gym, rb_oneHour, rb_twoHours;


    //预定相关默认值的初始化
    public static final boolean isGymChosen_default = false;
    public static final boolean isRbTwoHoursChosen_default = true;
    public static final String weekDayChosen_default = "星期四";
    public static final String beginTimeChosen_default = "19:00";

    public static String date_startBooking_default;
    public static String time_startBooking_defalut;


    public static boolean isGymChosen = isGymChosen_default;
    public static boolean isRbTwoHoursChosen = isRbTwoHoursChosen_default;
    public static String dayOfWeekChosen = weekDayChosen_default;
    public static String beginTimeChosen = beginTimeChosen_default;

    //控件中用户设置的开抢时间，这部分信息分别只在tv_date_show和tv_time_show两个控件中显示
    public static String date_startBooking;
    public static String time_startBooking;

    //用户成功提交的开抢时间，如果用户“从未成功提交”或者“点击了撤销按钮”，则置null。这部分信息只在tv_current_time_to_trigger_OlympicApp控件中显示
    public static String date_startBooking_submitted;
    public static String time_startBooking_submitted;

    public static String reservationTime;
    public static Calendar timeToStartReservation;
    public static String courtInfoSubmitted;

    //默认选择两小时


    //默认选择周四
    private List<String> weekDayDataList;
    private Spinner spinner_week_day;
    private ArrayAdapter adapter_week_day;



    //选择场地时间段对应的控件
    private List<String> dataList;
    private Spinner spinner;
    private ArrayAdapter adapter;



    private EditText et_passwd;
    private CheckBox checkbox_passwdDisplay;



    private static int year_reservation;
    private static int month_reservation;
    private static int day_reservation;
    private static int hour_reservation;
    private static int minute_reservation;

    private TextView tv_current_time_to_trigger_OlympicApp;
    private TextView tv_court_info;

    private TextView tv_date_show;
    private Button btn_set_date;
    private TextView tv_time_show;
    private Button btn_set_time;

    private TextView tv_passwd_warning;
    public static boolean isPasswdSet;

    //控制：提示用户是否在重复点击“撤销设置”按钮
    public static boolean canCancelBtnBePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate: dingjq2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

        bt_setting_submit = (Button)findViewById(R.id.bt_setting_submit);
        bt_cancel_setting = (Button)findViewById(R.id.bt_cancel_setting);
        bt_action_immediately = (Button)findViewById(R.id.bt_action_immediately);


        bt_setting_submit.setOnClickListener(this);
        bt_cancel_setting.setOnClickListener(this);
        bt_action_immediately.setOnClickListener(this);

        //场馆选择控件初始化
        rg_Venue = (RadioGroup)findViewById(R.id.rg_Venue);
        rb_Swimming = (RadioButton)findViewById(R.id.rb_Swimming);
        rb_Gym = (RadioButton)findViewById(R.id.rb_Gym);

        //预定时长选择控件初始化
        rg_timeLength = (RadioGroup)findViewById(R.id.rg_timeLength);
        rb_oneHour = (RadioButton)findViewById(R.id.rb_oneHour);
        rb_twoHours = (RadioButton)findViewById(R.id.rb_twoHours);

        //场地所在星期数选择控件初始化
        spinner_week_day = (Spinner)findViewById(R.id.spinner_week_day);

        //场地开始时间选择控件初始化
        spinner = (Spinner)findViewById(R.id.spinner);


        //开始抢场地时间显示控件初始化
        tv_current_time_to_trigger_OlympicApp = (TextView)findViewById(R.id.tv_current_time_to_trigger_OlympicApp);
        tv_court_info = (TextView)findViewById(R.id.tv_court_info);

        //抢场地日期选择控件初始化
        tv_date_show = (TextView)findViewById(R.id.tv_date_show);
        btn_set_date = (Button)findViewById(R.id.btn_set_date);
        tv_time_show = (TextView)findViewById(R.id.tv_time_show);
        btn_set_time = (Button)findViewById(R.id.btn_set_time);

        //支付密码设置相关warning信息显示的控件初始化
        tv_passwd_warning = (TextView)findViewById(R.id.tv_passwd_warning);


        //预定场地星期数选择控件初始化


        rg_Venue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_Swimming:
                        isGymChosen = false;
                        Toast.makeText(MainActivity.this, "You Choose Swimming Site", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_Gym:
                        isGymChosen = true;
                        Toast.makeText(MainActivity.this, "You Choose Gym Site", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });

        rg_timeLength.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_oneHour:

                        isRbTwoHoursChosen = false;
                        refreshDataList();
                        break;
                    case R.id.rb_twoHours:

                        isRbTwoHoursChosen = true;
                        refreshDataList();
                        break;
                    default:
                        break;
                }
            }
        });

        weekDayDataList = initWeekDayDataList();
        adapter_week_day = new ArrayAdapter(this, R.layout.time_point_item, R.id.textview, weekDayDataList);
        spinner_week_day.setAdapter(adapter_week_day);
        spinner_week_day.setSelection(4, true);//设置初始显示项
        spinner_week_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = weekDayDataList.get(position).toString();
                dayOfWeekChosen = selected;
                Toast.makeText(MainActivity.this, "You Choosed "+selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dataList = initDataList();
        adapter = new ArrayAdapter(this, R.layout.time_point_item, R.id.textview, dataList);
        spinner.setAdapter(adapter);
        spinner.setSelection(10, true);//设置初始显示项

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = dataList.get(position).toString();
                beginTimeChosen = selected;
                Toast.makeText(MainActivity.this, "You Choose the "+selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences preference = getSharedPreferences("passwd_sharedPref", MODE_PRIVATE);
        String name = preference.getString("passwd", "");
        if(name.length() > 0){
            isPasswdSet = true;
        } else {
            isPasswdSet = false;
        }

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        String bingPic = prefs.getString("bing_pic", null);
//        Log.w(TAG, "onCreate: bingPic = "+bingPic );
//        if(bingPic != null){
//            Glide.with(this).load(bingPic).into(bingPicImg);
//        } else {
//            loadBingPic();
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(date_startBooking_submitted == null){//
            tv_current_time_to_trigger_OlympicApp.setText("开抢时间设置：未提交！");
            tv_court_info.setText("场地信息设置：未提交！");
        } else {
            Calendar c = Calendar.getInstance();
            if(timeToStartReservation.before(c)){
                tv_current_time_to_trigger_OlympicApp.setText("上次开抢时间："+ date_startBooking_submitted + " " + time_startBooking_submitted + " 已过期，请重新设置");
                //如果用户在自动预定未完成状态下，强行退出奥体app，并立即打开本Activity，则终止未完成的自动预定行为，MyAccessibilityService中的onAccessibilityEvent会直接返回
                Log.w(TAG, "onResume: set MyAccessibilityService.isAccessibilityServiceEnabled to be false" );
                //disable MyAccessibilityService
                MyAccessibilityService.isAccessibilityServiceEnabled = false;
            } else {
                tv_current_time_to_trigger_OlympicApp.setText("当前开抢时间： " + date_startBooking_submitted + "   " + time_startBooking_submitted);
            }

            courtInfoSubmitted = (isGymChosen ? "体育馆" : "游泳馆")+ " " + ( isRbTwoHoursChosen ? "两小时" : "一小时") + " " + dayOfWeekChosen + " " + beginTimeChosen;
            tv_court_info.setText("已提交场地信息：" + courtInfoSubmitted);
        }

        //获取当前系统的日期和时间
        if(timeToStartReservation == null){
            timeToStartReservation = Calendar.getInstance();

            Log.w(TAG, "onResume TimerReceiver: timeToStartReservation initialization, timeToStartReservation = "+timeToStartReservation );
            year_reservation = timeToStartReservation.get(Calendar.YEAR);
            month_reservation = timeToStartReservation.get(Calendar.MONTH)+1;
            day_reservation = timeToStartReservation.get(Calendar.DAY_OF_MONTH);
            hour_reservation = timeToStartReservation.get(Calendar.HOUR);
            minute_reservation = timeToStartReservation.get(Calendar.MINUTE);


            String month_string = month_reservation< 10 ? "0" + month_reservation : "" + month_reservation;
            String day_string = day_reservation<10 ? "0"+ day_reservation : "" + day_reservation;

            String hour_string = hour_reservation<10 ? "0" + hour_reservation: "" + hour_reservation;
            String minute_string = minute_reservation<10 ? "0" + minute_reservation: "" + minute_reservation;

            date_startBooking = year_reservation+"-"+month_string+"-"+day_string;
            time_startBooking = hour_string+":"+minute_string+":" + "00";


            //默认预定时间的规则暂定为开启Activity的实时时间
            date_startBooking_default = date_startBooking;
            time_startBooking_defalut = time_startBooking;
        }


        tv_date_show.setText("开抢日期："+date_startBooking);
        tv_time_show.setText("开抢时间：" + time_startBooking);




        btn_set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        timeToStartReservation.set(Calendar.YEAR, year);
                        timeToStartReservation.set(Calendar.MONTH, month);
                        timeToStartReservation.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Log.w(TAG, "onDateSet: TimerReceiver the reservation data is set to : "+timeToStartReservation );

                        String month_string = month < 9 ? "0" + (month +1) : "" + (month+1);
                        String day_string = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;

                        date_startBooking = year+"-"+month_string+"-"+day_string;
                        date_startBooking_default = date_startBooking;

                        tv_date_show.setText("开抢日期："+date_startBooking);
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH)
                        , c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        timeToStartReservation.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        timeToStartReservation.set(Calendar.MINUTE, minute);
                        timeToStartReservation.set(Calendar.SECOND, 0);
                        timeToStartReservation.set(Calendar.MILLISECOND, 0);
                        Log.w(TAG, "onTimeSet: TimerReceiver the reservation data is set to : "+ timeToStartReservation );

                        String month_string = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String day_string = minute < 10 ? "0" + minute : "" + minute;

                        time_startBooking = month_string+":"+day_string+":"+"00";
                        time_startBooking_defalut = time_startBooking;

                        tv_time_show.setText("开抢时间："+time_startBooking);
                    }

                }
                , c.get(Calendar.HOUR_OF_DAY)
                , c.get(Calendar.MINUTE)
                , true).show();
            }
        });


        if(isPasswdSet){
            tv_passwd_warning.setVisibility(View.GONE);
        } else {
            tv_passwd_warning.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.bt_setting_submit:
                if(MyAccessibilityService.isMyAccessibilityServiceAlive){

                    performPasswdNotSetDialog(R.id.bt_setting_submit);

                } else {
                    Toast.makeText(this, "后台 Booking Service 未打开. \n请先到 \"系统设置\" 中开启!", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.bt_cancel_setting:
                if(MyAccessibilityService.isMyAccessibilityServiceAlive){

                    performPasswdNotSetDialog(R.id.bt_cancel_setting);

                } else {
                    Toast.makeText(this, "后台 Booking Service 未打开. \n请先到 \"系统设置\" 中开启!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bt_action_immediately:
                if(MyAccessibilityService.isMyAccessibilityServiceAlive){

                    performPasswdNotSetDialog(R.id.bt_action_immediately);

                } else {
                    Toast.makeText(this, "后台 Booking Service 未打开. \n请先到 \"系统设置\" 中开启!", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }

    }

    public static int static_pressed_button_id;
    private void performPasswdNotSetDialog(int widget_id){

        static_pressed_button_id = widget_id;

        if(isPasswdSet){

            switchButtonActions(widget_id);

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("警告");
            builder.setMessage("支付密码未设置，预定无法彻底完成，是否立刻设置支付密码？");
            builder.setPositiveButton("立刻设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, PasswdActivity.class);
                    startActivity(intent);

                }
            });
            builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switchButtonActions(static_pressed_button_id);

                }
            });
            builder.show();
        }


    }

    void switchButtonActions(int widget_id){
        switch (widget_id){
            case R.id.bt_setting_submit:
                perform_bt_setting_submit();
                break;
            case R.id.bt_cancel_setting:
                perform_bt_cancel_setting();
                break;
            case R.id.bt_action_immediately:
                perform_bt_action_immediately();
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    void perform_bt_setting_submit(){


        MyAccessibilityService.isNewSettingsSet = true;
        reservationTime = date_startBooking + " " + time_startBooking;

        //t当前待提交的开抢时间过期情况下的处理
        Calendar c = Calendar.getInstance();
        if(timeToStartReservation.before(c)){
            tv_current_time_to_trigger_OlympicApp.setText("当前开抢时间： 已过期，请重新设置");
            Toast.makeText(this, "当前开抢时间： 已过期，请重新设置", Toast.LENGTH_SHORT).show();
            return;
        } else {
            date_startBooking_submitted = date_startBooking;
            time_startBooking_submitted = time_startBooking;

            tv_current_time_to_trigger_OlympicApp.setText("当前开抢时间： " + date_startBooking_submitted + "   " + time_startBooking_submitted);
        }

        long period = 7 * 24 * 60 * 60 * 1000;
        //定时启动预定
        Utils.startAppAt(MainActivity.this, "com.citycamel.olympic", /*"2018-08-14 16:49:00"*/reservationTime, "yyyy-MM-dd HH:mm:ss", period);
        Toast.makeText(MainActivity.this, "设置已成功提交! \n 等待开抢！", Toast.LENGTH_SHORT).show();

        //控制：提示用户是否在重复点击“撤销设置”按钮
        canCancelBtnBePressed = true;

        //更新“待提交的场地信息”的显示
        courtInfoSubmitted = (isGymChosen ? "体育馆" : "游泳馆")+ " " + ( isRbTwoHoursChosen ? "两小时" : "一小时") + " " + dayOfWeekChosen + " " + beginTimeChosen;
        tv_court_info.setText("已提交场地信息：" + courtInfoSubmitted);

    }

    /**
     *
     */
    void perform_bt_cancel_setting(){

        //控制：提示用户是否在重复点击“撤销设置”按钮
        if(!canCancelBtnBePressed){
            Toast.makeText(this, "撤销已完成，请勿重复撤销", Toast.LENGTH_SHORT).show();
            return;
        }
        canCancelBtnBePressed = false;
        date_startBooking_submitted = null;
        time_startBooking_submitted = null;

        MyAccessibilityService.isNewSettingsSet = true;
        resetAllBadmintonCourtSettings();
        MyAccessibilityService.resetState();
        MyAccessibilityService.isAccessibilityServiceEnabled = false;


        tv_current_time_to_trigger_OlympicApp.setText("当前开抢时间： 开抢设置已撤销，请重新设置");
        //更新“待提交的场地信息”的显示
        tv_court_info.setText("已提交场地信息：开抢设置已撤销，请重新设置" );

        //取消之前的开抢设置
        Utils.cancelReservationToStartApp(MainActivity.this, "com.citycamel.olympic");

        Toast.makeText(this, "当前开抢时间： 已成功撤销开抢设置", Toast.LENGTH_LONG).show();
    }

    /**
     *
     */
    void perform_bt_action_immediately(){

        MyAccessibilityService.isNewSettingsSet = true;
        MyAccessibilityService.resetState();
        MyAccessibilityService.isAccessibilityServiceEnabled = true;
        Log.w(TAG, "perform_bt_action_immediately: isAccessibilityServiceEnabled = "+MyAccessibilityService.isAccessibilityServiceEnabled );
        Utils.startBookingImmediately(MainActivity.this, "com.citycamel.olympic");

    }

    private void resetAllBadmintonCourtSettings() {

        isGymChosen = isGymChosen_default;
        isRbTwoHoursChosen = isRbTwoHoursChosen_default;
        dayOfWeekChosen = weekDayChosen_default;
        beginTimeChosen = beginTimeChosen_default;

        //恢复相关控件的默认值显示
        rg_Venue.check(rb_Swimming.getId());
        rg_timeLength.check(rb_twoHours.getId());
        spinner_week_day.setSelection(4, true);
        spinner.setSelection(10, true);


    }


    private void refreshDataList(){
        if(isRbTwoHoursChosen){

            if(dataList.size() == 12){
                dataList.remove(dataList.size()-1);
                adapter.notifyDataSetChanged();
            }
        } else {
            if(dataList.size() == 11){
                dataList.add("20:00");
                adapter.notifyDataSetChanged();
            }
        }
    }

    private List<String> initWeekDayDataList() {
        List<String> dataList = new ArrayList<String>();

        dataList.add("星期日");
        dataList.add("星期一");
        dataList.add("星期二");
        dataList.add("星期三");
        dataList.add("星期四");
        dataList.add("星期五");
        dataList.add("星期六");

        return dataList;
    }

    private List<String> initDataList(){
        List<String> dataList = new ArrayList<String>();

        dataList.add("09:00");
        dataList.add("10:00");
        dataList.add("11:00");
        dataList.add("12:00");
        dataList.add("13:00");
        dataList.add("14:00");
        dataList.add("15:00");
        dataList.add("16:00");
        dataList.add("17:00");
        dataList.add("18:00");
        dataList.add("19:00");
        if(isRbTwoHoursChosen == false){
            dataList.add("20:00");
        }
        return dataList;
    }


    private void loadBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();//返回值是图片在服务器上的地址，例：http://cn.bing.com/az/hprichbg/rb/FranceMenton_ZH-CN8996032014_1920x1080.jpg
                Log.w(TAG, "onResponse: bingPic = "+bingPic );
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
        return true;
    }
}
