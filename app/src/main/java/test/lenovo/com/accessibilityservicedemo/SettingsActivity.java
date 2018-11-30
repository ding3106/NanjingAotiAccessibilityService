package test.lenovo.com.accessibilityservicedemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import test.lenovo.com.accessibilityservicedemo.passwd.PasswdActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button btn_passwd, btn_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn_passwd = (Button)findViewById(R.id.btn_passwd);
        btn_about = (Button)findViewById(R.id.btn_about);


        btn_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PasswdActivity.class);
                startActivity(intent);
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Designed by Wangqy and Dingjq", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preference = getSharedPreferences("passwd_sharedPref", MODE_PRIVATE);
        String name = preference.getString("passwd", "");
        if(name.length() > 0){
            btn_passwd.setText("修改支付密码");
        } else {
            btn_passwd.setText("设置支付密码");
        }
    }
}
