package com.example.timego;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Joern on 2017/7/7.
 *
 * 教学界面
 */

public class TeachActivity extends AppCompatActivity {

    private ImageView teachingPage;
    private TextView teachingHead;
    private int nowPage;
    private int totalPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mainteaching);
        teachingPage = (ImageView)findViewById(R.id.teaching_page);
        teachingHead = (TextView)findViewById(R.id.text_teach_head);
        nowPage = 1;
        totalPage = 5;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_teach_next:
                if (nowPage < 6){
                    nowPage += 1;
                    switch (nowPage){
                        case 1:
                            teachingHead.setText(R.string.teach_1);
                            teachingPage.setImageResource(R.drawable.teaching_main);
                            break;
                        case 2:
                            teachingHead.setText(R.string.teach_2);
                            teachingPage.setImageResource(R.drawable.teaching_menu);
                            break;
                        case 3:
                            teachingHead.setText(R.string.teach_3);
                            teachingPage.setImageResource(R.drawable.teaching_right);
                            break;
                        case 4:
                            teachingHead.setText(R.string.teach_4);
                            teachingPage.setImageResource(R.drawable.teaching_statistics);
                            break;
                        case 5:
                            teachingHead.setText(R.string.teach_5);
                            teachingPage.setImageResource(R.drawable.teaching_trend);
                            break;
                        default:
                            nowPage = 5;
                    }
                }else {

                }
                break;
            case R.id.btn_teach_pro:
                if (nowPage > 1){
                    nowPage -= 1;
                    switch (nowPage){
                        case 1:
                            teachingHead.setText(R.string.teach_1);
                            teachingPage.setImageResource(R.drawable.teaching_main);

                            break;
                        case 2:
                            teachingHead.setText(R.string.teach_2);
                            teachingPage.setImageResource(R.drawable.teaching_menu);

                            break;
                        case 3:
                            teachingHead.setText(R.string.teach_3);
                            teachingPage.setImageResource(R.drawable.teaching_right);

                            break;
                        case 4:
                            teachingHead.setText(R.string.teach_4);
                            teachingPage.setImageResource(R.drawable.teaching_statistics);

                            break;
                        case 5:
                            teachingHead.setText(R.string.teach_5);
                            teachingPage.setImageResource(R.drawable.teaching_trend);
                            break;
                    }
                }else {

                }
                break;
        }

    }

}
