package com.mk.music.ludi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Button btn_register;
    ImageButton login;
    TextView et_name,et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String S1="",S2="";
        Intent intent=getIntent();
        et_name=(EditText)findViewById(R.id.et_name);
        et_password=(EditText)findViewById(R.id.et_password);
        ((EditText)findViewById(R.id.et_name)).setText(S1);
        ((EditText)findViewById(R.id.et_password)).setText(S2);
        btn_register=findViewById(R.id.btn_register);

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //EditText输入状态改变，Button背景颜色也改变
                if ("".equals(et_password.getText().toString().trim())) {
                    login.setBackgroundColor(Color.GRAY);
                    login.setEnabled(false);
                } else {
                    //设置selector来控制Button背景颜色
                    login.setBackgroundColor(Color.BLUE);
                    login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        Log.i("MainActivity", "onClick ---> login");
        login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString();
                System.out.println(name);
                String pass=et_password.getText().toString();
                System.out.println(pass);
                Log.i("MainActivity",name+"_"+pass);
                UserService uService=new UserService(MainActivity.this);
                boolean flag=uService.login(name, pass);
                if(flag){
                    Log.i("TAG","登录成功");
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    startActivity(intent);
                }else {
                    Log.i("TAG", "登录失败");
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i("MainActivity", "onClick ---> btn_register");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,zhuce.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i("MainActivity","onStart ---> SharedPreferences");
        SharedPreferences sp;
        sp = getSharedPreferences(zhuce.SP_NAME,MODE_PRIVATE);
        //SharedPreferences sp = getSharedPreferences(MainActivity2.SP_NAME,MODE_PRIVATE);
        String S1 = sp.getString(zhuce.SP_ACCOUNT,"");
        String S2 = sp.getString(zhuce.SP_PASSWORD,"");
        et_name.setText(S1);
        et_password.setText(S2);

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("<--","调用onResume()");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("<--","调用onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("<--","调用onStop()");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("<--","调用onDestroy()");

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i("<--","调用onRestart()");
    }


}