package test.lenovo.com.accessibilityservicedemo.passwd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import test.lenovo.com.accessibilityservicedemo.MainActivity;
import test.lenovo.com.accessibilityservicedemo.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswdSaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswdSaveFragment extends Fragment {

    private EditText et_passwd;
    private CheckBox checkbox_passwdDisplay;
    private Button btn_save;


    public PasswdSaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PasswdSaveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswdSaveFragment newInstance() {
        PasswdSaveFragment fragment = new PasswdSaveFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_passwd_input, container, false);

        et_passwd = (EditText)view.findViewById(R.id.et_passwd);
        checkbox_passwdDisplay = (CheckBox)view.findViewById(R.id.checkbox_passwdDisplay);
        btn_save = (Button)view.findViewById(R.id.btn_save_passwd);

        //设置密码输入框可见性
        et_passwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        checkbox_passwdDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // 设置EditText文本为可见的
                    et_passwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_passwd.setSelection(et_passwd.getText().length());
                } else {
                    et_passwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_passwd.setSelection(et_passwd.getText().length());

                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_passwd.getText().length() == 6){
                    MainActivity.isPasswdSet = true;
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("passwd_sharedPref", MODE_PRIVATE).edit();
                    editor.putString("passwd", et_passwd.getText().toString());
                    editor.apply();
                    getActivity().finish();
                    Toast.makeText(getActivity(), "密码成功保存！", Toast.LENGTH_SHORT).show();
                } else if(et_passwd.getText().length() == 0){
                    Toast.makeText(getActivity(), "未输入密码，请重新输入！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "输入密码位数不够，请补齐！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }




}
