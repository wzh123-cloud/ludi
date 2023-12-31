package com.mk.music.ludi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class zhuce extends AppCompatActivity {

    private String realCode;
    public static final String SP_NAME="data";
    public static final String SP_ACCOUNT="Acount";
    public static final String SP_PASSWORD="Password";
    private Button btn_submit,genghuang;
    private EditText et_name,pwd1,pwd2;
    private EditText mEtloginactivityPhonecodes;
    private ImageView mIvloginactivityShowcode;

    private static final int LONG_DELAY = 3500; // 3.5 seconds

    private static final int SHORT_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);
        btn_submit=findViewById(R.id.btn_submit);
        //genghuang=findViewById(R.id.genghuang);
        et_name=findViewById(R.id.et_name);
        pwd1=findViewById(R.id.pwd1);
        pwd2=findViewById(R.id.pwd2);


        mEtloginactivityPhonecodes = findViewById(R.id.et_loginactivity_phoneCodes);
        mIvloginactivityShowcode = findViewById(R.id.iv_registeractivity_showCode);



        mIvloginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();


        mIvloginactivityShowcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvloginactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String S1 = ((EditText)findViewById(R.id.et_name)).getText().toString();
                String S2 = ((EditText)findViewById(R.id.et_name)).getText().toString();
                String S3 = ((EditText)findViewById(R.id.et_name)).getText().toString();

                String name=et_name.getText().toString().trim();
                String pass=pwd1.getText().toString().trim();
                String pass2=pwd2.getText().toString().trim();
               // Log.i("zhuce", name+"--->"+pass);

                if(pwd1.length()==0){
                    Toast.makeText(zhuce.this,getResources().getString(R.string.zhuce1),Toast.LENGTH_SHORT).show();
                }
                else if(4>=pwd1.length()){
                    Toast.makeText(zhuce.this,getResources().getString(R.string.zhuce2),Toast.LENGTH_SHORT).show();
                }
                else if(pwd1.length()>=18){
                    Toast.makeText(zhuce.this,getResources().getString(R.string.zhuce3),Toast.LENGTH_SHORT).show();
                }
                else if(pwd2.length()>=18){
                    Toast.makeText(zhuce.this,getResources().getString(R.string.zhuce4),Toast.LENGTH_SHORT).show();
                }
                else if((pwd1.getText().toString().trim()).equals(pwd2.getText().toString().trim())){
                    SharedPreferences sp=getSharedPreferences(SP_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString(SP_ACCOUNT,S1);
                    editor.putString(SP_PASSWORD,S1);
                    editor.commit();
                    UserService uService=new UserService(zhuce.this);
                    User user=new User();
                    user.setUsername(name);
                    user.setPassword(pass);
                    uService.register(user);
                   // Toast.makeText(zhuce.this, getResources().getString(R.string.zhuce5), Toast.LENGTH_SHORT).show();

                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.zhuce5), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    //ImageView imageCodeProject = new ImageView(getApplicationContext());
                    //imageCodeProject.setImageResource(R.drawable.success);

                    //toastView.addView(imageCodeProject, 0);
                    showMyToast(toast, 10*300);
                    zhuce.this.finish();
                }
                else {
                    Toast.makeText(zhuce.this,getResources().getString(R.string.zhuce6),Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }
}