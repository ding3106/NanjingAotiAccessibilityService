package test.lenovo.com.accessibilityservicedemo.passwd;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import test.lenovo.com.accessibilityservicedemo.R;

public class PasswdActivity extends AppCompatActivity {

    private boolean hasOldPasswd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwd);

        SharedPreferences preference = getSharedPreferences("passwd_sharedPref", MODE_PRIVATE);
        String name = preference.getString("passwd", "");
        if(name.length() > 0){
            hasOldPasswd = true;
        }

        if(hasOldPasswd){
            replaceFragment(PasswdModifyFragment.newInstance());
        } else {
            replaceFragment(PasswdSaveFragment.newInstance());
        }



    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_root, fragment);
        transaction.commit();
    }

}
