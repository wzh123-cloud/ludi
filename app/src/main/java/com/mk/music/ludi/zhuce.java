package com.mk.music.ludi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

public class zhuce extends AppCompatActivity {
    public static final String SP_NAME="data";
    public static final String SP_ACCOUNT="Acount";
    public static final String SP_PASSWORD="Password";
    private Button btn_submit;
    private EditText et_name,pwd1,pwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuce);
        btn_submit=findViewById(R.id.btn_submit);
        et_name=findViewById(R.id.et_name);
        pwd1=findViewById(R.id.pwd1);
        pwd2=findViewById(R.id.pwd2);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}