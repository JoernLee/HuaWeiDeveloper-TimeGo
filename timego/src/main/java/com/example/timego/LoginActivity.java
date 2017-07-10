package com.example.timego;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by Joern on 2017/7/9.
 */

public class LoginActivity extends AppCompatActivity{
    private EditText account;
    private EditText password;
    private Button login;

    private String nowAccount;
    private String nowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_defendlogin);
        account = (EditText)findViewById(R.id.loginAccount_id);
        password = (EditText)findViewById(R.id.password_id);
        login = (Button) findViewById(R.id.login);
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.login:
                if (Objects.equals(account.getText().toString(),"fxteam")){
                    if (Objects.equals(password.getText().toString(), "123456")){
                        Toast.makeText(LoginActivity.this,"账号密码正确-即将显示子账号App使用信息",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("result_return", "True");
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this,"账号密码错误",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"账号不存在",Toast.LENGTH_SHORT).show();
                }
        }

    }
}
