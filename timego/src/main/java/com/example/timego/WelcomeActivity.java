package com.example.timego;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @{# GuideActivity.java Create on 2013-5-2 下午10:59:08
 *
 *     class desc: 引导界面
 *
 *     <p>
 *     Copyright: Copyright(c) 2013
 *     </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 *
 *
 */
public class WelcomeActivity extends AppCompatActivity implements OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter2 vpAdapter;
    private List<View> views;

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;
    Boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_dot);

        // 初始化页面
        initViews();

        // 初始化底部小点
        //initDots();
    }

    private void initViews() {

        SharedPreferences pref = getSharedPreferences("joern", Activity.MODE_PRIVATE);
        isFirst = pref.getBoolean("status",true);

        if(!isFirst){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        //定义一个setting记录APP是几次启动！！！



        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout guideFive = (RelativeLayout) inflater.inflate(R.layout.guide_five, null);
        guideFive.findViewById(R.id.jump_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        views = new ArrayList<View>();
        // 初始化引导图片列表
       /* views.add(inflater.inflate(R.layout.guide_one, null));
        views.add(inflater.inflate(R.layout.guide_two, null));
        views.add(inflater.inflate(R.layout.guide_three, null));
        views.add(inflater.inflate(R.layout.guide_four,null));*/
        views.add(guideFive);
        // 初始化Adapter
        vpAdapter = new ViewPagerAdapter2(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[views.size()];

        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色*/
            dots[i].setBackground(getDrawable(R.drawable.welcome_dot_1));
        }

        currentIndex = 0;
        dots[currentIndex].setBackground(getDrawable(R.drawable.welcome_dot_2));// 设置为白色，即选中状态*/
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setBackground(getDrawable(R.drawable.welcome_dot_2));
        dots[currentIndex].setBackground(getDrawable(R.drawable.welcome_dot_1));

        currentIndex = position;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        /*setCurrentDot(arg0);*/
    }

}