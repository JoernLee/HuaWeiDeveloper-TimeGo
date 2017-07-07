package com.example.timego;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Joern on 2017/7/7.
 *
 * 教学界面
 */

public class TeachActivity extends AppCompatActivity {

    private ImageView teachingPage;
    private int nowPage;
    private int totalPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mainteaching);
    }

    public void onClick(View v) {

    }

}
