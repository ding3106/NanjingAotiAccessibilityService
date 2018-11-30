package test.lenovo.com.accessibilityservicedemo.passwd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import test.lenovo.com.accessibilityservicedemo.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswdModifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswdModifyFragment extends Fragment {
    private static final String TAG = "PasswdModifyFragment";


    EditText et_old_passwd, et_new_passwd, et_passwd_confirm;

    Button btn_modify;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     */
    // TODO: Rename and change types and number of parameters
    public static PasswdModifyFragment newInstance() {
        PasswdModifyFragment fragment = new PasswdModifyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_passwd_modify, container, false);

        et_old_passwd = (EditText)view.findViewById(R.id.et_old_passwd);
        et_new_passwd = (EditText)view.findViewById(R.id.et_new_passwd);
        et_passwd_confirm = (EditText)view.findViewById(R.id.et_passwd_confirm);

        Button btn_modify = (Button)view.findViewById(R.id.btn_modify);

        SharedPreferences preference = getActivity().getSharedPreferences("passwd_sharedPref", MODE_PRIVATE);
        final String passwd = preference.getString("passwd", "");

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String old_passwd = et_old_passwd.getText().toString();
                String new_passwd = et_new_passwd.getText().toString();
                String passwd_confirm = et_passwd_confirm.getText().toString();
                //Log.w(TAG, "onClick: old_passed = "+old_passwd+", new_passwd = "+new_passwd+", passed_confirm = "+passwd_confirm );

                if(old_passwd.length() == 0){
                    Toast.makeText(getActivity(), "未输入旧密码，请输入！", Toast.LENGTH_SHORT).show();
                } else if(old_passwd.length() < 6){
                    Toast.makeText(getActivity(), "旧密码输入不完整，请补齐！", Toast.LENGTH_SHORT).show();
                } else if( !old_passwd.equals(passwd)){
                    Toast.makeText(getActivity(), "旧密码输入不正确，请重新输入！", Toast.LENGTH_SHORT).show();
                } else if(new_passwd.length() == 0){
                    Toast.makeText(getActivity(), "未输入新密码，请输入！", Toast.LENGTH_SHORT).show();
                } else if(new_passwd.length() < 6){
                    Toast.makeText(getActivity(), "新密码输入不完整，请补齐！", Toast.LENGTH_SHORT).show();
                }else if(passwd_confirm.length() == 0){
                    Toast.makeText(getActivity(), "未输入确认密码，请输入！", Toast.LENGTH_SHORT).show();
                } else if(passwd_confirm.length() < 6){
                    Toast.makeText(getActivity(), "确认密码输入不完整，请补齐！", Toast.LENGTH_SHORT).show();
                } else if(!new_passwd.equals(passwd_confirm)){
                    Toast.makeText(getActivity(), "确认密码不匹配，请重新输入！", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("passwd_sharedPref", MODE_PRIVATE).edit();
                    editor.putString("passwd", new_passwd);
                    editor.apply();
                    Toast.makeText(getActivity(), "密码修改成功！", Toast.LENGTH_SHORT).show();
                    getActivity().finish();

                }
            }
        });




        return view;
    }



}
