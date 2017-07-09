package com.example.timego;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 04-02  完成sqlite数据库的创建
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private SlideMenu slideMenu;//滑动菜单
    private LineChart mLineChart;//折现图
    private PieChart mPieChart;//折现图
    private BarChart mBarChart;//饼状图
    private PieChart mPieChartAna;//饼状图

    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    private List<AppInfo> appInfoList = new ArrayList<>();
    private DBManager dbManager;
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private ViewPagerAdapter viewPagerAdapter;

    private TextView mLightState;
    private ImageView mLightImage;
    private ViewFlipper mFlipper;
    private GestureDetector mDetector; //手势检测

    public static final Locale LOCALE_CHINESE = Locale.CHINESE;
    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;

    //教学滑动用
    private int[] imageID = {R.drawable.back_2_joern, R.drawable.backleft_joern, R.drawable.icon_teach,
            R.drawable.icon_trend};
    private ViewFlipper viewFlipper = null;
    private GestureDetector gestureDetector = null;


    private Calendar calendar = Calendar.getInstance(Locale.CHINA);
    private ListView lvH;
    private ListView lvM;
    private ListView lvD;
    private SoundPool soundPool;
    private int music;
    //RGB变色相关
    private final static String TAG = "MainActivity";
    private final String ACTION_NAME_RSSI = "AMOMCU_RSSI";    // 其他文件广播的定义必须一致
    private final String ACTION_CONNECT = "AMOMCU_CONNECT";    // 其他文件广播的定义必须一致

    int mWhite = 0;            //白色 0~255  ------这个颜色在我们的amomcu的蓝牙灯板子上无效，不过考虑到有些朋友想利用，我这里是留出了这个接口
    int mRed = 255;            //红色 0~255
    int mGreen = 0;            //绿色 0~255
    int mBlue = 0;            //蓝色 0~255

    int mBrightness = 100;                    // 这个是亮度的意思 0~100; 0最黑， 100最亮
    int mBrightness_old = mBrightness;        // 这个是亮度的意思 0~100; 0最黑， 100最亮

    public final static int WIDTH = 256;
    public final static int HEIGHT = 36;

    private byte[] rgb565VideoData = new byte[WIDTH * HEIGHT * 4];
    private ByteBuffer image_byteBuf = ByteBuffer.wrap(rgb565VideoData);
    private Bitmap prev_image = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);

    // 根据rssi 值计算距离， 只是参考作用， 不准确---amomcu
    static final int rssibufferSize = 10;
    int[] rssibuffer = new int[rssibufferSize];
    int rssibufferIndex = 0;
    boolean rssiUsedFalg = false;

    // 用于消息更新参数
    public static final int REFRESH = 0x000001;
    private Handler mHandlerRGB = null;

    // 开关灯
    ToggleButton toggle_onoff;
    int light_onoff = 1;

    // 律动
    ToggleButton toggle_flash;
    int light_flash = 0;
    private final int light_flash_time_ms = 800; //律动间隔

    /////
    private TextView show_date;
    private int selectStaticsFlag = 2;//selectStaticsFlag==0 年/selectStaticsFlag==1 月/selectStaticsFlag==2 日/
    private Button btn_dailyView, btn_monthView, btn_yearView;
    private ToggleButton mTogBtn;//测试开关按钮

    private MinuteAdapter minuteAdapter;
    private List<String[]> listMinuteItem;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private List<ResolveInfo> resolveInfos;
    private HashMap<Integer, String> mapLoadUrl = new HashMap<>();
    private WebView webView5;//大数据分析
    private WebView webWechart;//日报微信
    private boolean firstOpen = false;
    private int returnWebview = 0;
    private String loginResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (hasPermission()) {
                try {
                    startTimer();
                } catch (Exception e) {
                }

            } else {
                try {
                    stopTimer();
                } catch (Exception e) {
                }
            }
        }
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                loginResult = data.getStringExtra("result_return");
                //Toast.makeText(MainActivity.this,"账号正确",Toast.LENGTH_SHORT).show();
                //TODO 列表清空
                //停止定时器
                stopTimer();
                //清空列表
                DateMain date = new DateMain(0);
                List<String[]> listHourItem = new LinkedList<>();
                //-----初始化主界面小时列表----
                final int hourTopWhiteBlock = 8;
                final int hourEndWhiteBlock = 8;
                boolean init = true;
                listHourItem = date.glideHourDate(init, listHourItem, hourTopWhiteBlock, hourEndWhiteBlock, listHourItem);
                HourAdapter hourAdapter = new HourAdapter(this, listHourItem);
                lvH.setAdapter(hourAdapter);
                //-----初始化主界面分钟列表----使用重写baseadapter方法
                listMinuteItem = new LinkedList<>();
                List<String[]> appTimeColor = new LinkedList<>();
                listMinuteItem = date.glideMinuteDate(init, listMinuteItem, appTimeColor);
                minuteAdapter = new MinuteAdapter(this, listMinuteItem);
                lvM.setAdapter(minuteAdapter);

                //TODO 下载服务器对于子账号数据填充

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*//透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        DataSlideMenu.setLanguageSelection(0);

        initView();//初始化
        initEvents();//事件初始化
        dbManager = new DBManager(this);
        dbManager.tryCreatTable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            } else {
                startTimer();//开始定时记录数据
            }
        }

        registerBoradcastReceiver();

        // 操作
        mHandlerRGB = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REFRESH) {
                    SetColor2Device(mWhite, mRed, mGreen, mBlue, mBrightness);
                    /*UpdateProgress();*/
                    UpdateText();
                }
                super.handleMessage(msg);
            }
        };


        ReadParameter();    //读出参数


        // 更新 参数
        UpdateAllParameter();


    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && DataSlideMenu.getLanguageSelection() == 0 && firstOpen == false) {
            firstOpen = true;
            initHome();
        }
        if (hasFocus && returnWebview != 0) {
            webView5 = (WebView) findViewById(R.id.webView_analyzeC);
            webView5.getSettings().setJavaScriptEnabled(true);
            webView5.getSettings().setBlockNetworkImage(false);
            webView5.loadUrl(mapLoadUrl.get(returnWebview));
        }
    }

    //开始定时器
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override

                public void run() {
                    try {

                        AppInfo appInfo = getForegroundApp();
                        dealAppInfo(appInfoList, appInfo);
                        mHandlerHome.sendMessage(new Message());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 0, 10 * 1000);

    }

    //停止定时器
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    //初始化主页，必须在主页获得焦点后执行，即需要在onWindowFocusChanged中调用
    public void initHome() {
        PackageManager pm = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        resolveInfos = pm.queryIntentActivities(mainIntent, FILTER_ALL_APP);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));


        lvH = (ListView) findViewById(R.id.time_hour);
        lvM = (ListView) findViewById(R.id.time_minute);


        final int hourTopWhiteBlock = 8;
        final int hourEndWhiteBlock = 8;
        final TextView rightAppName = (TextView) findViewById(R.id.right_icon_name);
        final ImageView rightAppIcon = (ImageView) findViewById(R.id.icon_image);
        final View rightLineColor = (View) findViewById(R.id.right_line);
        final View leftLineColor = (View) findViewById(R.id.up_hand);
        final TextView leftHour = (TextView) findViewById(R.id.left_line_hour);
        final TextView leftMinute = (TextView) findViewById(R.id.left_line_minute);
        final TextView leftUsing = (TextView) findViewById(R.id.home_using_level);
        final TextView sumTime = (TextView) findViewById(R.id.sum);


        SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMdd");
        Calendar toDayT = Calendar.getInstance();//当前日期
        List<AppInfo> selectAppListAll = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String todayTableName = nowDate.format(toDayT.getTime()).toString();

            List<AppInfo> selectAppList = dbManager.getAppInfo(todayTableName);
            if (selectAppList.isEmpty()) {

            } else {
                selectAppListAll.addAll(selectAppList);

            }

            toDayT.add(Calendar.DAY_OF_MONTH, -1);//04-17
        }


        int size = selectAppListAll.size();
        /*String appTimeColor[][] = new String[size][4];*/
        List<String[]> appTimeColor = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            String oneItem[] = new String[4];
            AppInfo selectApp = selectAppListAll.get(i);
            oneItem[0] = selectApp.getAppLabel(); //10s扫描读到的appName
            String[] openTime = selectApp.getOpenTime().split(" ");
            oneItem[1] = openTime[0];//10s扫描读到的时间 - 日期
            oneItem[2] = openTime[1]; //10s扫描读到的时间 - 具体时间
            oneItem[3] = selectApp.getAppColor(); //10s扫描读到的app的color
            appTimeColor.add(oneItem);
        }
        selectAppListAll.removeAll(selectAppListAll);

        DateMain date = new DateMain(0);
        setDate(date.month, date.date, date.dayofweek);

        //-----初始化主界面时钟列表
        List<String[]> listHourItem = new LinkedList<>();
        ;/*在数组中存放数据*/
        boolean init = true;
        listHourItem = date.glideHourDate(init, listHourItem, hourTopWhiteBlock, hourEndWhiteBlock, listHourItem);
        HourAdapter hourAdapter = new HourAdapter(this, listHourItem);
        lvH.setAdapter(hourAdapter);
        //-----初始化主界面分钟列表----使用重写baseadapter方法


        listMinuteItem = new LinkedList<>();
        listMinuteItem = date.glideMinuteDate(init, listMinuteItem, appTimeColor);
        minuteAdapter = new MinuteAdapter(this, listMinuteItem);
        lvM.setAdapter(minuteAdapter);
        //5.5Joern更新
        // ---直接跳到打开时候系统时间对应的position
        Calendar c = Calendar.getInstance();
        final int hour, minute;
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        lvH.setSelection(4 * 24 + hour + 1);
        //-----用数据更新分钟列表
        init = false;
        listMinuteItem = date.glideMinuteDate(init, listMinuteItem, appTimeColor);
        // minuteAdapter = new MinuteAdapter(this, listMinuteItem);
//
//
        minuteAdapter.notifyDataSetChanged();
        //-----用数据更新小时列表
        listHourItem = date.glideHourDate(init, listHourItem, hourTopWhiteBlock, hourEndWhiteBlock, listMinuteItem);
        hourAdapter = new HourAdapter(this, listHourItem);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                lvM.setSelection(4 * 24 * 60 + hour * 60 + minute - 8);
            }
        }, 10);
        //  hourAdapter.notifyDataSetChanged();
        //-----选择中间的小时块
        //小时块滑动监听器
        /*5.3-5.4 Joern更新
        1. 小时块滑倒一单位的一半时，右边对应分钟块跳转到当前小时30min处(最好能在触发时候关闭分钟块的监听器)
        2. 分钟块顶端超过0，自动跳转到上一个小时块的下半部分，超过30，跳转到上一个小时的上半部分（必须关闭小时监听器，不然混乱）
        */

        lvH.setOnScrollListener(new AbsListView.OnScrollListener() {
            int hourCenter = 0;

            int minuteStart = 0;
            int middleHeight = 0;
            int hourBias = 8;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    //listview滑动后停止
                    case SCROLL_STATE_IDLE:

                        DataSlideMenu.setMA(true);
                        leftHour.setTextSize(22);
                        leftHour.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

                        //得到中间时钟块的position
                        double itemY;
                        double firstHourItem = lvH.getFirstVisiblePosition() / 2;
                        double lastHourItem = lvH.getLastVisiblePosition() / 2;
                        int flag = 0;
                        hourCenter = (int) (firstHourItem + lastHourItem) - hourBias;
                        //得到中间线的高度
                        middleHeight = lvH.getHeight() / 2;
                        //得到中间Item距离顶部的距离
                        View childView = lvH.getChildAt(hourCenter - lvH.getFirstVisiblePosition() + hourBias);
                        itemY = childView.getTop();
                        //通过中间和item和上一个item距离顶部距离得到一个item的高度
                        View tempView = lvH.getChildAt(hourCenter - lvH.getFirstVisiblePosition() - 1 + hourBias);
                        double oneItemHeight = itemY - tempView.getTop();
                        //根据一个item的高度去修正中间item的位置
                        if (itemY + oneItemHeight < middleHeight) {
                            hourCenter = hourCenter + 1;
                        }
                        //txcase.setText(String.valueOf(hourCenter));
                        //滚动对应的分钟块开头
                        /*minuteStart = hourCenter * 60;
                        lvM.setSelection(minuteStart);*/
                        //强制触发分钟块选取（移动小时块产生）---下面那个是滑动分钟块产生

                        break;
                    //滑动后松手的惯性滑动过程
                    case SCROLL_STATE_FLING:
                        break;
                    //按住滑动的过程
                    case SCROLL_STATE_TOUCH_SCROLL:
                        DataSlideMenu.setMA(false);
                        leftHour.setTextSize(28);
                        leftHour.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        break;
                }
            }

            @Override

            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //得到中间时钟块的position
                String currentAppInfo[] = new String[4];
                String[] centerItem;
                double itemY;
                double firstHourItem = lvH.getFirstVisiblePosition() / 2;
                double lastHourItem = lvH.getLastVisiblePosition() / 2;
                int flag = 0;
                if (firstVisibleItem != 0 && visibleItemCount != 0) {
                    hourCenter = (int) (firstHourItem + lastHourItem) - hourBias;
                    //得到中间线的高度
                    middleHeight = lvH.getHeight() / 2;
                    //得到中间Item距离顶部的距离
                    View childView = lvH.getChildAt(hourCenter - lvH.getFirstVisiblePosition() + hourBias);
                    itemY = childView.getTop();
                    centerItem = (String[]) lvH.getItemAtPosition(hourCenter + 8);
                    //通过中间和item和上一个item距离顶部距离得到一个item的高度
                    View tempView = lvH.getChildAt(hourCenter - lvH.getFirstVisiblePosition() - 1 + hourBias);
                    double oneItemHeight = itemY - tempView.getTop();
                    if (itemY + oneItemHeight < middleHeight) {
                        childView = lvH.getChildAt(hourCenter - lvH.getFirstVisiblePosition() + hourBias + 1);
                        itemY = childView.getTop();
                        hourCenter += 1;
                        centerItem = (String[]) lvH.getItemAtPosition(hourCenter + 8);
                    }
                    if (itemY + oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[0];
                        currentAppInfo[1] = centerItem[1];
                        currentAppInfo[2] = centerItem[2];
                        minuteStart = hourCenter * 60 - 20 + 30;
                    }
                    //落在10-20s
                    else if (itemY + 2 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[3];
                        currentAppInfo[1] = centerItem[4];
                        currentAppInfo[2] = centerItem[5];
                        minuteStart = hourCenter * 60 - 10 + 30;
                    } else if (itemY + 3 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[6];
                        currentAppInfo[1] = centerItem[7];
                        currentAppInfo[2] = centerItem[8];
                        minuteStart = hourCenter * 60 + 30;
                    } else if (itemY + 4 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[9];
                        currentAppInfo[1] = centerItem[10];
                        currentAppInfo[2] = centerItem[11];
                        minuteStart = hourCenter * 60 + 10 + 30;
                    } else if (itemY + 5 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[12];
                        currentAppInfo[1] = centerItem[13];
                        currentAppInfo[2] = centerItem[14];
                        minuteStart = hourCenter * 60 + 20 + 30;
                    } else if (itemY + 6 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[15];
                        currentAppInfo[1] = centerItem[16];
                        currentAppInfo[2] = centerItem[17];
                        minuteStart = hourCenter * 60 + 30 + 30;
                    }

                    //根据一个item的高度去修正中间item的位置
                   /* if (itemY + oneItemHeight < middleHeight) {
                        hourCenter = hourCenter + 1;
                    }*/

                 /*   if (itemY + 3 * oneItemHeight / 6 > middleHeight) {

                    } else {
                        minuteStart = hourCenter * 60 + 30;
                    }*/
                    //txcase.setText(String.valueOf(hourCenter));
                    //滚动对应的分钟块开头
                    if (DataSlideMenu.getHA()) {
                        lvM.setSelection(minuteStart - 7);
                    }

                    if (currentAppInfo[0] != "") {
                        if (currentAppInfo[1] != null) {
                            if (currentAppInfo[1] == "#27CDF2") {
                                leftUsing.setText(R.string.light_using_1);
                            } else if (currentAppInfo[1] == "#27B9F2") {
                                leftUsing.setText(R.string.light_using_2);
                            } else if (currentAppInfo[1] == "#27A4F2") {
                                leftUsing.setText(R.string.medium_using_1);
                            } else if (currentAppInfo[1] == "#3B7FD9") {
                                leftUsing.setText(R.string.medium_using_2);
                            } else if (currentAppInfo[1] == "#3D71D9") {
                                leftUsing.setText(R.string.weight_using);
                            } else if (currentAppInfo[1] == "#66696969") {
                                leftUsing.setText(R.string.no_using);
                            }
                        }
                    }


                    // leftHour.setText(String.valueOf(hourCenter%24));
                }


                if (lvH.getFirstVisiblePosition() >= 0 && lvH.getFirstVisiblePosition() <= 24) {
                    DateMain date = new DateMain(-4);
                    setDate(date.month, date.date, date.dayofweek);
                } else if (lvH.getFirstVisiblePosition() > 24 && lvH.getFirstVisiblePosition() <= 48) {
                    DateMain date = new DateMain(-3);
                    setDate(date.month, date.date, date.dayofweek);
                } else if (lvH.getFirstVisiblePosition() > 48 && lvH.getFirstVisiblePosition() <= 72) {
                    DateMain date = new DateMain(-2);
                    setDate(date.month, date.date, date.dayofweek);
                } else if (lvH.getFirstVisiblePosition() > 72 && lvH.getFirstVisiblePosition() <= 96) {
                    DateMain date = new DateMain(-1);
                    setDate(date.month, date.date, date.dayofweek);
                } else if (lvH.getFirstVisiblePosition() > 96 && lvH.getFirstVisiblePosition() <= 120) {
                    DateMain date = new DateMain(0);
                    setDate(date.month, date.date, date.dayofweek);
                }
            }

        });


        //分钟块滑动监听器
        lvM.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                String currentAppInfo[] = new String[4];
                int minCenter = 0;
                int minBias = 0;
                int middleHeight = 0;
                String[] centerItem;
                double itemY;
                double firstMinItem;
                double lastMinItem;
                double oneItemHeight;
                View childView;
                View tempView;

                switch (scrollState) {

                    //listview滑动后停止
                    case SCROLL_STATE_IDLE:
                        //计算中间分钟块的position
                        DataSlideMenu.setHA(true);
                        leftMinute.setTextSize(22);
                        leftMinute.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

                        firstMinItem = lvM.getFirstVisiblePosition() / 2.0;
                        if (firstMinItem == 0.0) {
                            firstMinItem = 0.5;
                        }
                        lastMinItem = lvM.getLastVisiblePosition() / 2.0;
                        double tempCenter = firstMinItem + lastMinItem - minBias;
                        minCenter = (int) (tempCenter);
                        //取得对应的item---一个字符串链表（存着10s间隔的对应的app名称、颜色、icon标号）
                        centerItem = (String[]) lvM.getItemAtPosition(minCenter);
                        //得到中间线的高度
                        middleHeight = lvM.getHeight() / 2;
                        //得到中间Item距离顶部的距离
                        childView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition());
                        itemY = childView.getTop();
                        //通过中间和item和上一个item距离顶部距离得到一个item的高度
                        tempView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition() - 1);
                        oneItemHeight = itemY - tempView.getTop();
                        //根据一个item的高度去修正中间item的位置
                        if (itemY + oneItemHeight < middleHeight) {
                            childView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition() + 1);
                            itemY = childView.getTop();
                        }
                        //通过计算oneItemHeight的六分之一去估计中间线对应的秒块取值
                        //落在0-10s区间
                        if (itemY + oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[0];
                            currentAppInfo[1] = centerItem[1];
                            currentAppInfo[2] = centerItem[2];
                        }
                        //落在10-20s
                        else if (itemY + 2 * oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[3];
                            currentAppInfo[1] = centerItem[4];
                            currentAppInfo[2] = centerItem[5];
                        } else if (itemY + 3 * oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[6];
                            currentAppInfo[1] = centerItem[7];
                            currentAppInfo[2] = centerItem[8];
                        } else if (itemY + 4 * oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[9];
                            currentAppInfo[1] = centerItem[10];
                            currentAppInfo[2] = centerItem[11];
                        } else if (itemY + 5 * oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[12];
                            currentAppInfo[1] = centerItem[13];
                            currentAppInfo[2] = centerItem[14];
                        } else if (itemY + 6 * oneItemHeight / 6 > middleHeight) {
                            currentAppInfo[0] = centerItem[15];
                            currentAppInfo[1] = centerItem[16];
                            currentAppInfo[2] = centerItem[17];
                        }

                        //mxcase.setText(String.valueOf(minCenter));

                        break;

                    case SCROLL_STATE_TOUCH_SCROLL:
                        DataSlideMenu.setHA(false);
                        leftMinute.setTextSize(28);
                        leftMinute.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        break;
                    case SCROLL_STATE_FLING:

                        break;
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                String currentAppInfo[] = new String[4];

                int minCenter;
                int minBias = 0;
                int middleHeight;
                String[] centerItem;
                double itemY;
                double firstMinItem;
                double lastMinItem;
                double oneItemHeight;
                View childView;
                View tempView;
                if (firstVisibleItem != 0 && visibleItemCount != 0) {
                    firstMinItem = lvM.getFirstVisiblePosition() / 2;
                    if (firstMinItem == 0.0) {
                        firstMinItem = 0.5;
                    }
                    lastMinItem = lvM.getLastVisiblePosition() / 2;
                    minCenter = (int) (firstMinItem + lastMinItem) - minBias;
                    //取得对应的item---一个字符串链表（存着10s间隔的对应的app名称、颜色、icon标号）
                    centerItem = (String[]) lvM.getItemAtPosition(minCenter);
                    //得到中间线的高度
                    middleHeight = lvM.getHeight() / 2;
                    //得到中间Item距离顶部的距离
                    childView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition());
                    itemY = childView.getTop();
                    //通过中间和item和上一个item距离顶部距离得到一个item的高度
                    tempView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition() - 1);
                    oneItemHeight = itemY - tempView.getTop();
                    //根据一个item的高度去修正中间item的位置
                    if (itemY + oneItemHeight < middleHeight) {
                        childView = lvM.getChildAt(minCenter - lvM.getFirstVisiblePosition() + 1);
                        itemY = childView.getTop();
                        minCenter += 1;
                        centerItem = (String[]) lvM.getItemAtPosition(minCenter);
                    }
                    leftHour.setText(String.valueOf(((minCenter / 60) % 24)));
                    leftMinute.setText(String.valueOf(minCenter % 60));
                    //通过计算oneItemHeight的六分之一去估计中间线对应的秒块取值
                    //落在0-10s区间
                    if (itemY + oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[0];
                        currentAppInfo[1] = centerItem[1];
                        currentAppInfo[2] = centerItem[2];
                    }
                    //落在10-20s
                    else if (itemY + 2 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[3];
                        currentAppInfo[1] = centerItem[4];
                        currentAppInfo[2] = centerItem[5];
                    } else if (itemY + 3 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[6];
                        currentAppInfo[1] = centerItem[7];
                        currentAppInfo[2] = centerItem[8];
                    } else if (itemY + 4 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[9];
                        currentAppInfo[1] = centerItem[10];
                        currentAppInfo[2] = centerItem[11];
                    } else if (itemY + 5 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[12];
                        currentAppInfo[1] = centerItem[13];
                        currentAppInfo[2] = centerItem[14];
                    } else if (itemY + 6 * oneItemHeight / 6 > middleHeight) {
                        currentAppInfo[0] = centerItem[15];
                        currentAppInfo[1] = centerItem[16];
                        currentAppInfo[2] = centerItem[17];
                    }
                    if (DataSlideMenu.getMA()) {
                        //根据当前分钟position进行小时position跳转
                        double nowHourPosition = (minCenter + 1) / 60.0;
                        if (nowHourPosition % 1 == 0) {// 是这个整数，小数点后面是0
                            lvH.setSelection((int) nowHourPosition);
                        }

                    }
                    if (currentAppInfo[0] != "") {
                        rightAppName.setText(currentAppInfo[0]);
                        if (currentAppInfo[1] != null) {
                            int sum = singleTime(minCenter, lvM, currentAppInfo);
                            sumTime.setText(String.valueOf(sum));
                            rightLineColor.setBackgroundColor(Color.parseColor(currentAppInfo[1]));
                            //leftLineColor.setBackgroundColor(Color.parseColor(currentAppInfo[1]));
                            //0515---蓝牙颜色变换
                            String lightColor[] = new String[3];
                            lightColor[0] = currentAppInfo[1].substring(1, 3);
                            lightColor[1] = currentAppInfo[1].substring(3, 5);
                            lightColor[2] = currentAppInfo[1].substring(5, 7);

                            int redColor = Integer.valueOf(lightColor[0], 16);
                            int greenColor = Integer.valueOf(lightColor[1], 16);
                            int blueColor = Integer.valueOf(lightColor[2], 16);
                            SetColor2Device(0, redColor, greenColor, blueColor, 100);

                            //计算总的时间
                        }
                        Drawable drawable = getAppIcon(currentAppInfo[0]);
                        //Drawable drawable = getDrawable(R.drawable.left_hand_2);
                        if (drawable != null) {
                            rightAppIcon.setImageDrawable(drawable);
                        } else {
                            //系统桌面等无图标应用的图标添加---0519
                            if (currentAppInfo[0] != "") {
                                SetColor2Device(0, 143, 143, 143, 100);
                                rightAppIcon.setImageResource(R.drawable.nousing);
                            }
                        }
                    } else {
                        rightLineColor.setBackgroundColor(Color.parseColor("#66696969"));
                        rightAppName.setText("");
                        sumTime.setText("");
                        /*Drawable drawable = MainActivity.this.getResources().getDrawable(R.drawable.nousing);*/
                        rightAppIcon.setImageDrawable(null);
                    }


                }
            }

        });

        //必须要这样才能得到lastpostition








        /*lvH.setSelection(200);  */ //选择初始位置可以用
        //4.11 - 12 把小时块6等分，每等分显示10min中内时间最多的

        //初始化步骤：主界面
        //1.初始位置选择好（主界面显示的小时块位置）
        //2.数据库加载所有的分钟块---5*24*60*6（六等分）
        //3.根据加载好的分钟块，加载小时块，配色规则按照上面的---5*24*6(六等分)

        //扫描步骤：主界面-10s
        //1.扫描的app数据录入数据库
        //2.读数据库时间，填充对应的分钟块，同时更新小时块

        //主进程滑动小时块：
        //1.通过读当前的Y坐标得到小时item的position
        //2.通过小时item的position得到分钟item的position
        //3.分钟list进行setSelection操作


    }

    private int singleTime(int centerPosition, ListView lv, String[] centerLine) {
        int sumTime = 10;
        int noequal = 0;
        String nowColor;
        String[] centerItem;
        //得到中间对齐的Item
        centerItem = (String[]) lv.getItemAtPosition(centerPosition);
        //得到扫到的color
        nowColor = centerLine[1];
        //进行循环累加，首先向前累加
        for (int i = 0; i < 120; i++) {
            if (centerPosition > 120) {
                centerItem = (String[]) lv.getItemAtPosition(centerPosition - i);
                for (int j = 0; j < 6; j++) {
                    if (centerItem[1 + (3 * j)].equals(nowColor)) {
                        sumTime += 10;
                    } else {
                        noequal += 1;

                    }
                }
                if (noequal > 20) {
                    break;
                }
            }
        }

        noequal = 0;
        if (centerPosition < 60 * 5 * 24 - 120)
            for (int i = 0; i < 120; i++) {
                centerItem = (String[]) lv.getItemAtPosition(centerPosition + i);
                for (int j = 0; j < 6; j++) {
                    if (centerItem[1 + (3 * j)].equals(nowColor)) {
                        sumTime += 10;
                    } else {
                        noequal += 1;

                    }
                }
                if (noequal > 20) {
                    break;
                }
            }
        sumTime = sumTime / 60;
        return sumTime;
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dailypaper_show", R.drawable.daily_1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("dailypaper_show", R.drawable.daily_2);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("dailypaper_show", R.drawable.daily_3);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("dailypaper_show", R.drawable.daily_4);
        list.add(map);

        return list;
    }


    private Drawable getAppIcon(final String appName) {
        Drawable appIcon = null;
        PackageManager pm = this.getPackageManager();
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        // 通过查询，获得所有ResolveInfo对象.
//        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, FILTER_ALL_APP);
//        // 调用系统排序 ， 根据name排序
//        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
//        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            if (appLabel.equals(appName)) {
                appIcon = reInfo.loadIcon(pm); // 获得应用程序图标
                return appIcon;
            }

        }
        return appIcon;
    }

    //界面控件初始化
    public void initView() {
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);
        //查找布局文件用LayoutInflater.inflate
        //获取app内部所有应用信息
        mapLoadUrl.put(1, "http://101.227.242.90:11125/timego/activeChart.html");
        mapLoadUrl.put(3, "http://101.227.242.90:11125/timego/habitChart.html");
        mapLoadUrl.put(2, "http://101.227.242.90:11125/timego/averTChart.html");
        mapLoadUrl.put(4, "http://101.227.242.90:11125/timego/trendChart.html");
        mapLoadUrl.put(5, "http://101.227.242.90:11125/timego/appHabitChart.html");
        mapLoadUrl.put(6, "http://101.227.242.90:11125/timego/appTrendChart.html");
        mapLoadUrl.put(7, "http://101.227.242.90:11125/timego/relationChart.html");

        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.layout_home, null);
        View view2 = inflater.inflate(R.layout.layout_statistics, null);
        View view3 = inflater.inflate(R.layout.layout_trend, null);
        View view4 = inflater.inflate(R.layout.layout_teach, null);
        View view5 = inflater.inflate(R.layout.layout_dailypaper_2, null);

       /* try{
            view5 = inflater.inflate(R.layout.layout_dailypaper, null);
        }catch (Exception e){
            view5 = inflater.inflate(R.layout.layout_dailypaper_2, null); ;
        }*/
        View view6 = inflater.inflate(R.layout.layout_analyze_c, null);
        View view7 = inflater.inflate(R.layout.layout_analyze_u, null);
        View view8 = inflater.inflate(R.layout.layout_about, null);

        View view9 = inflater.inflate(R.layout.layout_settings, null);


        //将view装入数组
        pageview = new ArrayList<View>();
        pageview.add(view1);//主页面
        pageview.add(view2);//统计页面
        pageview.add(view3);//趋势页面
        pageview.add(view4);//教学页面
        pageview.add(view5);//日报页面
        pageview.add(view6);//企业数据分析页面
        pageview.add(view7);//用户数据页面
        pageview.add(view8);//关于页面
        pageview.add(view9);//设置界面
        viewPagerAdapter = new ViewPagerAdapter(pageview);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int arg0) {                                 //当滑动式，顶部的imageView是通过animation缓慢的滑动
                // TODO Auto-generated method stub
                DataSlideMenu.setA(arg0);
                switch (arg0) {
                    case 0: {
                        break;
                    }
                    case 1: {//统计
                        show_date = (TextView) findViewById(R.id.show_date);
                        mPieChart = (PieChart) findViewById(R.id.spread_pie_chart);
                        mBarChart = (BarChart) findViewById(R.id.spread_bar_chart);
                        DataChartClass.initPieChart(mPieChart);
                        DataChartClass.initBarChart(mBarChart);
                        updataStaisticsChart();
                        break;
                    }
                    case 2: {//趋势
                        mLineChart = (LineChart) findViewById(R.id.spread_line_chart);
                        btn_dailyView = (Button) findViewById(R.id.btn_dailyView);
                        btn_monthView = (Button) findViewById(R.id.btn_monthView);
                        btn_yearView = (Button) findViewById(R.id.btn_yearView);
                        DataChartClass.initLineChart(mLineChart);
                        String tableName;
                        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd");
                        Calendar cal = Calendar.getInstance();
                        tableName = nowDateFormat.format(cal.getTime()).toString();

                        dailyAppInfoMapT = dbManager.selectAppInfoDaily(tableName);
                        if (!dailyAppInfoMapT.isEmpty()) {
                            Set<String> appNameSet = dailyAppInfoMapT.keySet();
                            DataChartClass.showLineChartDaily(mLineChart, dailyAppInfoMapT, appNameSet);
                            btn_dailyView.setBackgroundColor(Color.parseColor("#12B7F5"));
                            btn_dailyView.setTextColor(Color.WHITE);
                            btn_monthView.setBackgroundColor(Color.WHITE);
                            btn_monthView.setTextColor(Color.parseColor("#99101010"));
                            btn_yearView.setBackgroundColor(Color.WHITE);
                            btn_yearView.setTextColor(Color.parseColor("#99101010"));
                            selectDMY = 1;
                            selectStatisticsApp(appNameSet);
                        }

                        break;
                    }
                    //教学界面
                    case 3:




                        WebView webView3 = (WebView) findViewById(R.id.teach_web);
                        webView3.getSettings().setJavaScriptEnabled(true);
                        webView3.getSettings().setBlockNetworkImage(false);
                        webView3.loadUrl("http://mp.weixin.qq.com/s/pVbplHmvvjDxuQHpcd0P6w");
                        webView3.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                // TODO Auto-generated method stub
                                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                                view.loadUrl(url);
                                return true;
                            }
                        });
                        break;
                    //日报界面
                    case 4:
                       /* WebView webView4 = (WebView) findViewById(R.id.webView_daily);
                        webView4.getSettings().setJavaScriptEnabled(true);
                        webView4.getSettings().setBlockNetworkImage(false);
                        webView4.loadUrl("http://mp.weixin.qq.com/s?__biz=MzUzODA2NDM0Mw==&mid=2247483655&idx=2&sn=eb315107300d33432dc84ee5c3a7b6e5&chksm=fadc25bfcdabaca9bf57452e103fbbeff23903de5b1197be2a97c1509210d0083a142a603a93&mpshare=1&scene=23&srcid=042820EsY37lHlcMpFmHhy81#rd");
                        webView4.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                // TODO Auto-generated method stub
                                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                                view.loadUrl(url);
                                return true;
                            }
                        });*/
                        break;
                    case 5://企业数据分析
                        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        webView5 = (WebView) findViewById(R.id.webView_analyzeC);
                        webView5.getSettings().setJavaScriptEnabled(true);
                        webView5.getSettings().setBlockNetworkImage(false);
                        webView5.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                // TODO Auto-generated method stub
                                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                                view.loadUrl(url);
                                return true;
                            }
                        });
                        webView5.loadUrl(mapLoadUrl.get(1));
                        returnWebview = 1;
                        break;
                    case 6://用户数据分析
                        mPieChartAna = (PieChart) findViewById(R.id.analyze_chart);

                        DataChartClass.initPieChart(mPieChartAna);
                        updatAanalyzeChart();
                        break;

                    case 7:
                        break;
                    default:
                        break;
                }
            }

        });


    }


    private final Handler mHandlerHome = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            minuteAdapter.notifyDataSetChanged();
        }
    };

    //事件初始化
    public void initEvents() {

        mTogBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    ifFillTestData();


                } else {
                    //未选中
                }
            }
        });// 添加监听事件

    }

    //监听返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() == 0) {
                // 创建退出对话框
                new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("请单击Home键退出，以记录您的手机使用情况")//设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                // finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                            }
                        }).show();//在按键响应事件中显示此对话框
            } else {


                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

                viewPager.setCurrentItem(0);
            }
        }

        return false;

    }

    //处理按钮单击事件
    //处理按钮单击事件
    public void onClick(View v) {
        mLightState = (TextView) findViewById(R.id.txt_light_state);
        mLightImage = (ImageView) findViewById(R.id.image_light_state);  // 获取到控件
        switch (v.getId()) {
            case R.id.txt_S_return://统计界面返回按钮
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_T_return://趋势界面返回按钮
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_teach_return:
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_D_return://日报界面返回按钮
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_ANC_return://企业分析界面返回按钮
                viewPager.setCurrentItem(0);
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

                break;
            case R.id.txt_ANU_return://用户分析界面返回按钮
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_AB_return://关于界面返回按钮
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_settings_return:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_dailyView://日趋势图
                selectDailyTime();
                break;
            case R.id.btn_monthView://月趋势图
                selectMonthTime();
                break;
            case R.id.btn_yearView://年趋势图
                selectYearTime();
                break;
            case R.id.txt_statistics_select://统计类型选择按钮
                statistics_select();
                break;
            case R.id.txt_analyzeC_select:
                analyze_select();
                break;
            case R.id.txt_statistics_change:
                if (DataSlideMenu.getStatisticsState() == 1)
                    DataSlideMenu.setStatisticsState(2);
                else
                    DataSlideMenu.setStatisticsState(1);
                updataStaisticsChart();

                break;
            case R.id.btn_pro://统计界面日期向前
                btn_switch_click(-1);
                break;
            case R.id.btn_next://统计界面日期向后
                btn_switch_click(1);
                break;
            case R.id.txt_choose://趋势界面选择
                try {
                    selectAppBuilder.show();
                } catch (Exception e) {
                }
                break;
            case R.id.menu:
                slideMenu.switchMenu();
                break;
            //定位时间
            case R.id.decide_time:
                Calendar c = Calendar.getInstance();
                final int hour, minute;
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                lvH.setSelection(4 * 24 + hour + 1);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        lvM.setSelection(4 * 24 * 60 + hour * 60 + minute - 7);
                    }
                }, 10);
                break;
            //权限按钮
            case R.id.settings_right_icon:
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                break;
            //设置语言
            case R.id.settings_language_icon:
                Dialog dialog = simpleDialog();
                dialog.setOwnerActivity(MainActivity.this);
                dialog.show();
              /*  if (DataSlideMenu.getLanguageSelection() == 1){
                    selectLanguage("cn");
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (DataSlideMenu.getLanguageSelection() == 2){
                    selectLanguage("en");
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }*/
                break;
            //蓝牙小灯
            case R.id.image_light_state:
                Toast.makeText(MainActivity.this, "无配套硬件支持-暂时无法使用", Toast.LENGTH_LONG).show();
                //0522改
                //如果是关闭状态，则打开
               /* if (mLightState.getText().toString().equals(this.getString(R.string.settings_close))) {
                    mLightState.setText(this.getResources().getString(R.string.settings_open));
                    Intent i = new Intent(MainActivity.this, DeviceScanActivity.class);
                    startActivity(i);
                } else {
                    mLightState.setText(this.getResources().getString(R.string.settings_close));
                }*/
                break;
            case R.id.image_defend_state:
                //Toast.makeText(MainActivity.this, "该服务未出售-暂时无法使用", Toast.LENGTH_LONG).show();
                //TODO 账号匹配-(后面的onActivityResult里面处理)清空列表-下载对应账号的云端数据
                //账号匹配
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
               /* if (loginResult.equals("True")){
                    //清空列表
                    Toast.makeText(MainActivity.this,"账号正确",Toast.LENGTH_SHORT).show();

                }else {

                }*/
                break;
            //该服务未出售-暂时无法使用

            //分享测试

            //这里的图片路径要改对
              /*  String imagePath = Environment.getExternalStorageDirectory() + File.separator + "1483879222709.jpg";
                //由文件得到uri
                Uri imageUri = Uri.fromFile(new File(imagePath));
                Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image*//*");
                startActivity(Intent.createChooser(shareIntent, "分享到"));*/
            //这里点击取消之后会闪退，在Activity初始化时候可能需要考虑一些东西

            //截屏保存测试0519--成功
            case R.id.jieping:
                try {
                    saveToSD(myShot(MainActivity.this), "/storage/emulated/0/", "shot.png");
                    //Toast.makeText(MainActivity.this, "保存至/storage/emulated/0/shot.png---欢迎分享！", Toast.LENGTH_LONG).show();
                    //这里的图片路径要改对
                    String imagePath = Environment.getExternalStorageDirectory() + File.separator + "shot.png";
                    //由文件得到uri
                    Uri imageUri = Uri.fromFile(new File(imagePath));
                    Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    //0521--之前这里的image/*  不知道怎么就变成 image  导致无法出现分享的应用
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "分享到"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.test_share:
                try {
                    saveToSD(myShot(MainActivity.this), "/storage/emulated/0/", "shot.png");
                    Toast.makeText(MainActivity.this, "保存至/storage/emulated/0/shot.png---欢迎分享！", Toast.LENGTH_LONG).show();
                    //这里的图片路径要改对
                    String imagePath = Environment.getExternalStorageDirectory() + File.separator + "shot.png";
                    //由文件得到uri
                    Uri imageUri = Uri.fromFile(new File(imagePath));
                    Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image");
                    startActivity(Intent.createChooser(shareIntent, "分享到"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            //主界面的教学按钮
            case R.id.teaching_button:
                Intent teachIntent = new Intent(MainActivity.this,TeachActivity.class);
                startActivity(teachIntent);
            default:
                break;
        }

    }

    //当前屏幕截图0519
    public Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    //保存到手机SD卡，供分享0519
    private void saveToSD(Bitmap bmp, String dirName, String fileName) throws IOException {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(dirName);
            // 判断文件夹是否存在，不存在则创建
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dirName + fileName);
            // 判断文件是否存在，不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // 用完关闭
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void onClickMenu(View v) {
        //清空所有点击变换的布局颜色
        findViewById(R.id.left_main).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_statics).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_trend).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_setting).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_teach).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_newspaper).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_analyzeC).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_analyzeU).setBackgroundColor(Color.parseColor("#FFFFFF"));
        findViewById(R.id.left_about).setBackgroundColor(Color.parseColor("#FFFFFF"));

        //清空所有点击变化的字体颜色
        TextView temp11 = (TextView) findViewById(R.id.txt_mainmenu);
        temp11.setTextColor(Color.parseColor("#de000000"));
        TextView temp21 = (TextView) findViewById(R.id.txt_statistics);
        temp21.setTextColor(Color.parseColor("#de000000"));
        TextView temp31 = (TextView) findViewById(R.id.txt_trend);
        temp31.setTextColor(Color.parseColor("#de000000"));
        TextView temp41 = (TextView) findViewById(R.id.txt_setting);
        temp41.setTextColor(Color.parseColor("#de000000"));
        TextView temp51 = (TextView) findViewById(R.id.txt_daily);
        temp51.setTextColor(Color.parseColor("#de000000"));
        TextView temp61 = (TextView) findViewById(R.id.txt_analyzeC);
        temp61.setTextColor(Color.parseColor("#de000000"));
        TextView temp71 = (TextView) findViewById(R.id.txt_about);
        temp71.setTextColor(Color.parseColor("#de000000"));
        TextView temp81 = (TextView) findViewById(R.id.txt_teach);
        temp81.setTextColor(Color.parseColor("#de000000"));
        TextView temp91 = (TextView) findViewById(R.id.txt_analyzeU);
        temp91.setTextColor(Color.parseColor("#de000000"));
        TextView temp101 = (TextView) findViewById(R.id.txt_setting);
        temp101.setTextColor(Color.parseColor("#de000000"));
        //还原图片
        ImageView temp12 = (ImageView) findViewById(R.id.icon_mainmenu);
        temp12.setImageDrawable(getResources().getDrawable(R.drawable.icon_home));
        ImageView temp22 = (ImageView) findViewById(R.id.icon_statistics);
        temp22.setImageDrawable(getResources().getDrawable(R.drawable.icon_statistics));
        ImageView temp32 = (ImageView) findViewById(R.id.icon_trend);
        temp32.setImageDrawable(getResources().getDrawable(R.drawable.icon_trend));
        ImageView temp42 = (ImageView) findViewById(R.id.icon_setting);
        temp42.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting));
        ImageView temp52 = (ImageView) findViewById(R.id.icon_daily);
        temp52.setImageDrawable(getResources().getDrawable(R.drawable.icon_newspaper));
        ImageView temp62 = (ImageView) findViewById(R.id.icon_analyzeC);
        temp62.setImageDrawable(getResources().getDrawable(R.drawable.icon_analyze_com));
        ImageView temp72 = (ImageView) findViewById(R.id.icon_about);
        temp72.setImageDrawable(getResources().getDrawable(R.drawable.icon_about));
        ImageView temp82 = (ImageView) findViewById(R.id.icon_teach);
        temp82.setImageDrawable(getResources().getDrawable(R.drawable.icon_teach));
        ImageView temp92 = (ImageView) findViewById(R.id.icon_analyzeU);
        temp92.setImageDrawable(getResources().getDrawable(R.drawable.icon_analyze_user));
        ImageView temp102 = (ImageView) findViewById(R.id.icon_setting);
        temp102.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting));

        switch (v.getId()) {
            case R.id.txt_statistics://统计按钮
                LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.left_statics);
                linearLayout1.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView1 = (TextView) findViewById(R.id.txt_statistics);
                textView1.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView = (ImageView) findViewById(R.id.icon_statistics);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_statistics2));
                viewPager.setCurrentItem(1);
                slideMenu.switchMenu();
                break;
            case R.id.txt_trend://趋势按钮
                LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.left_trend);
                linearLayout2.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView2 = (TextView) findViewById(R.id.txt_trend);
                textView2.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView2 = (ImageView) findViewById(R.id.icon_trend);
                imageView2.setImageDrawable(getResources().getDrawable(R.drawable.icon_trend2));
                viewPager.setCurrentItem(2);
                slideMenu.switchMenu();
                break;
            case R.id.txt_daily://日报按钮
                LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.left_newspaper);
                linearLayout3.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView3 = (TextView) findViewById(R.id.txt_daily);
                textView3.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView3 = (ImageView) findViewById(R.id.icon_daily);
                imageView3.setImageDrawable(getResources().getDrawable(R.drawable.icon_newspaper2));
                viewPager.setCurrentItem(4);
                slideMenu.switchMenu();

                // newspaper的崩溃--尝试修改图片尺寸或者改变显示方式 - 0707修复
                lvD = (ListView) findViewById(R.id.dailypaper_list);
                //初始化日报list
                SimpleAdapter mAdapterDaily = new SimpleAdapter(this, getData(), R.layout.dailypaper_item,
                        new String[]{"dailypaper_show"},
                        new int[]{R.id.dailypaper_show});
                lvD.setAdapter(mAdapterDaily);

                lvD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    //list点击事件
                    @Override
                    public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                        // TODO: Implement this method
                        switch (p3) {
                            case 0://第一个item
                                DataSlideMenu.setDailyWechart("http://mp.weixin.qq.com/s/pPH0B6ToIxPBz94e0FHcNA");
                                Intent intent = new Intent(MainActivity.this, WechartAcitivity.class);
                                startActivity(intent);
                                break;
                            case 1://第二个item
                                DataSlideMenu.setDailyWechart("http://mp.weixin.qq.com/s/G3bGvvE4edsmp_7gYhgPiw");
                                Intent intent2 = new Intent(MainActivity.this, WechartAcitivity.class);
                                startActivity(intent2);
                                break;
                            case 2://第三个item
                                DataSlideMenu.setDailyWechart("http://mp.weixin.qq.com/s/tZ__jBxydel83evsH-noag");
                                Intent intent3 = new Intent(MainActivity.this, WechartAcitivity.class);
                                startActivity(intent3);
                                break;
                            case 3:
                                DataSlideMenu.setDailyWechart("http://mp.weixin.qq.com/s/BVAW17MO2OggTFRpzJvDvQ");
                                Intent intent4 = new Intent(MainActivity.this, WechartAcitivity.class);
                                startActivity(intent4);
                                break;
                        }
                    }
                });

                break;
            case R.id.txt_analyzeC://企业数据分析按钮
                LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.left_analyzeC);
                linearLayout4.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView4 = (TextView) findViewById(R.id.txt_analyzeC);
                textView4.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView4 = (ImageView) findViewById(R.id.icon_analyzeC);
                imageView4.setImageDrawable(getResources().getDrawable(R.drawable.icon_analyze_com2));
                viewPager.setCurrentItem(5);
                slideMenu.switchMenu();
                break;
            case R.id.txt_analyzeU://企业数据分析按钮
                LinearLayout linearLayout8 = (LinearLayout) findViewById(R.id.left_analyzeU);
                linearLayout8.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView8 = (TextView) findViewById(R.id.txt_analyzeU);
                textView8.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView8 = (ImageView) findViewById(R.id.icon_analyzeU);
                imageView8.setImageDrawable(getResources().getDrawable(R.drawable.icon_analyze_user2));
                viewPager.setCurrentItem(6);
                slideMenu.switchMenu();
                break;
            case R.id.txt_about://关于按钮
                LinearLayout linearLayout5 = (LinearLayout) findViewById(R.id.left_about);
                linearLayout5.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView5 = (TextView) findViewById(R.id.txt_about);
                textView5.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView5 = (ImageView) findViewById(R.id.icon_about);
                imageView5.setImageDrawable(getResources().getDrawable(R.drawable.icon_about2));

                viewPager.setCurrentItem(7);
                slideMenu.switchMenu();
                break;
            case R.id.txt_mainmenu://主界面按钮
                LinearLayout linearLayout6 = (LinearLayout) findViewById(R.id.left_main);
                linearLayout6.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView6 = (TextView) findViewById(R.id.txt_mainmenu);
                textView6.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView6 = (ImageView) findViewById(R.id.icon_mainmenu);
                imageView6.setImageDrawable(getResources().getDrawable(R.drawable.icon_home2));
                viewPager.setCurrentItem(0);
                slideMenu.switchMenu();
                break;
            case R.id.txt_teach://教学按钮
                LinearLayout linearLayout7 = (LinearLayout) findViewById(R.id.left_teach);
                linearLayout7.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView7 = (TextView) findViewById(R.id.txt_teach);
                textView7.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView7 = (ImageView) findViewById(R.id.icon_teach);
                imageView7.setImageDrawable(getResources().getDrawable(R.drawable.icon_teach2));
                viewPager.setCurrentItem(3);
                slideMenu.switchMenu();

              /*  //0520任务
                mFlipper = (ViewFlipper) findViewById(R.id.teach_flipper);
                mDetector = new GestureDetector(new simpleGestureListener());
                mFlipper.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        return mDetector.onTouchEvent(motionEvent);

                    }


                });*/
                break;
            case R.id.txt_setting://设置按钮
                LinearLayout linearLayout9 = (LinearLayout) findViewById(R.id.left_setting);
                linearLayout9.setBackgroundColor(Color.parseColor("#448f8f8f"));
                TextView textView9 = (TextView) findViewById(R.id.txt_setting);
                textView9.setTextColor(Color.parseColor("#12B7F5"));
                ImageView imageView9 = (ImageView) findViewById(R.id.icon_setting);
                imageView9.setImageDrawable(getResources().getDrawable(R.drawable.icon_setting2));
                viewPager.setCurrentItem(8);
                slideMenu.switchMenu();
                /*startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);*/
                break;

        }

    }

    //处理获得的APP信息，存入数据库
    private void dealAppInfo(List<AppInfo> appInfolist, AppInfo app) {

        if (app == null) return;

        SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMdd");
        String tableName = nowDate.format(new Date()).toString();
        dbManager.addAppInfo(tableName, app);//存入每日数据库
        dbManager.saveAppInfoCloud(tableName,app);//存入云端数据库
        appInfolist.add(app);

        String oneItem[] = new String[4];
        oneItem[0] = app.getAppLabel(); //10s扫描读到的appName
        String[] openTime = app.getOpenTime().split(" ");
        oneItem[1] = openTime[0];//10s扫描读到的时间 - 日期
        oneItem[2] = openTime[1]; //10s扫描读到的时间 - 具体时间
        oneItem[3] = app.getAppColor(); //10s扫描读到的app的color
        List<String[]> appTimeColorT = new ArrayList<>();
        appTimeColorT.add(oneItem);

        DateMain date = new DateMain(0);
        listMinuteItem = date.glideMinuteDate(false, listMinuteItem, appTimeColorT);


        nowDate = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        String column_name = nowDate.format(cal.getTime()).toString();
        nowDate = new SimpleDateFormat("yyyyMM");
        tableName = nowDate.format(new Date()).toString();

        List returnInfo1 = dbManager.judgeAppExist(tableName, app.getAppLabel(), column_name);
        if (!returnInfo1.isEmpty())//存在
        {
            double runtime = (double) returnInfo1.get(0);
            runtime += 10.0;
            dbManager.updateAppInfoMonth(tableName, app.getAppLabel(), column_name, runtime);
        } else {
            dbManager.addAppInfoMonth(tableName, app.getAppLabel(), column_name, (double) app.getRunTime());
        }
    }

    //获取正在运行的前台应用程序
    private AppInfo getForegroundApp() {
        //适用于Android 5.0之后的读取方法

        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
//        List<UsageStats> queryUsageStats =
//                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 60 * 1000, ts);

        List<UsageStats> queryUsageStats =
                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 5 * 60 * 60 * 1000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }

        String packageName = recentStats.getPackageName();
        PackageManager pm = this.getPackageManager();
        String name = null;
        Drawable appIcon = null;
        String openTime;
        try {
            name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            appIcon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        openTime = df.format(new Date());
        AppInfo appInfo = new AppInfo();
        appInfo.setAppIcon(appIcon);
        appInfo.setAppLabel(name);
        int hashCode = Math.abs(name.hashCode());

        AppColors appColors = new AppColors();
        String appColor = appColors.myAppColors[hashCode % 22];

        appInfo.setAppColor(appColor);
        appInfo.setOpenTime(openTime);
        appInfo.setRunTime(10);


        return appInfo;


    }

    //0520--让滑动生效
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mDetector != null && viewPager.getCurrentItem() == 3) {
            if (mDetector.onTouchEvent(event)) {
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // 判断本应用是否获得用户权限
    private boolean hasPermission() {

        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    //获得app使用情况月信息
    private int selectDMY = 0;
    //趋势页面，获取选择的日
    private Map<String, List> dailyAppInfoMapT;

    private void selectDailyTime() {

        new DailyPickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                btn_dailyView.setBackgroundColor(Color.parseColor("#12B7F5"));
                btn_dailyView.setTextColor(Color.WHITE);
                btn_monthView.setBackgroundColor(Color.WHITE);
                btn_monthView.setTextColor(Color.parseColor("#99101010"));
                btn_yearView.setBackgroundColor(Color.WHITE);
                btn_yearView.setTextColor(Color.parseColor("#99101010"));

                String tableName;
                if (monthOfYear < 9) {
                    if (dayOfMonth < 10) {
                        tableName = "" + year + "0" + (monthOfYear + 1) + "0" + dayOfMonth;
                    } else {
                        tableName = "" + year + "0" + (monthOfYear + 1) + dayOfMonth;
                    }

                } else {
                    if (dayOfMonth < 10) {
                        tableName = "" + year + (monthOfYear + 1) + "0" + dayOfMonth;
                    } else {
                        tableName = "" + year + (monthOfYear + 1) + dayOfMonth;
                    }

                }
                dailyAppInfoMapT = dbManager.selectAppInfoDaily(tableName);
                if (!dailyAppInfoMapT.isEmpty()) {
                    Set<String> appNameSet = dailyAppInfoMapT.keySet();
                    DataChartClass.showLineChartDaily(mLineChart, dailyAppInfoMapT, appNameSet);
                    selectDMY = 1;
                    selectStatisticsApp(appNameSet);
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();

    }

    //趋势页面，获取选择的月份//趋势页面，获取选择的月份
    private Map<String, double[]> monthAppInfoMapT;

    private void selectMonthTime() {
        new MonPickerDialog(this, android.R.style.Theme_Holo_Light_Panel, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                btn_monthView.setBackgroundColor(Color.parseColor("#12B7F5"));
                btn_monthView.setTextColor(Color.WHITE);
                btn_dailyView.setBackgroundColor(Color.WHITE);
                btn_dailyView.setTextColor(Color.parseColor("#99101010"));
                btn_yearView.setBackgroundColor(Color.WHITE);
                btn_yearView.setTextColor(Color.parseColor("#99101010"));
                String tableName;
                if (monthOfYear < 9) {
                    tableName = "" + year + "0" + (monthOfYear + 1);
                } else {
                    tableName = "" + year + (monthOfYear + 1);
                }
                monthAppInfoMapT = dbManager.selectAppInfoMonth(tableName);
                if (!monthAppInfoMapT.isEmpty()) {
                    Set<String> appNameSet = monthAppInfoMapT.keySet();
                    DataChartClass.showLineChartMonth(mLineChart, monthAppInfoMapT, appNameSet);
                    selectDMY = 2;
                    selectStatisticsApp(appNameSet);
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();

    }

    //趋势页面，获取选择的年份
    private Map<String, double[]> yearAppInfoMapT;

    private void selectYearTime() {

        new YearPickerDialog(this, android.R.style.Theme_Holo_Light_Panel, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                btn_yearView.setBackgroundColor(Color.parseColor("#12B7F5"));
                btn_yearView.setTextColor(Color.WHITE);
                btn_monthView.setBackgroundColor(Color.WHITE);
                btn_monthView.setTextColor(Color.parseColor("#99101010"));
                btn_dailyView.setBackgroundColor(Color.WHITE);
                btn_dailyView.setTextColor(Color.parseColor("#99101010"));
                String tableName;
                tableName = "" + year;
                yearAppInfoMapT = dbManager.selectAppInfoYear(tableName);
                if (!yearAppInfoMapT.isEmpty()) {
                    Set<String> appNameSet = yearAppInfoMapT.keySet();

                    DataChartClass.showLineChartYear(mLineChart, yearAppInfoMapT, appNameSet);
                    selectDMY = 3;
                    selectStatisticsApp(appNameSet);
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }


            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();

    }

    //统计页面，选择统计类型。年统计、月统计、日统计
    public void statistics_select() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择统计类型");
//    指定下拉列表的显示数据
        final String[] cities = {"年统计", "月统计", "日统计"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectStaticsFlag = which;
                SimpleDateFormat dateFormat;
                Calendar cal = Calendar.getInstance();
                String dateStr;
                switch (which) {
                    case 0:
                        dateFormat = new SimpleDateFormat("yyyy年");
                        dateStr = dateFormat.format(cal.getTime()).toString();
                        show_date.setText(String.valueOf(dateStr));
                        break;
                    case 1:
                        dateFormat = new SimpleDateFormat("yyyy年MM月");
                        dateStr = dateFormat.format(cal.getTime()).toString();
                        show_date.setText(String.valueOf(dateStr));
                        break;
                    case 2:
                        show_date.setText("今天");
                        break;
                }

                updataStaisticsChart();


            }
        });
        builder.show();
    }

    //
    public void analyze_select() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择分析图表");
//    指定下拉列表的显示数据
        final String[] cities = {"目前活跃人群分布", "各地区平均使用时间分布", "用户APP全天使用习惯分析",
                "用户APP使用变化趋势图", "各类型软件使用习惯分析", "各类型软件使用变化趋势图", "AI大数据智能分析"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnWebview = which + 1;


            }
        });
        builder.show();
    }

    //统计页面，前后切按钮
    private void btn_switch_click(int i) {
        String mydate = show_date.getText().toString();

        if (mydate.equals("今天")) {
            SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Calendar cal = Calendar.getInstance();
            mydate = nowDateFormat.format(cal.getTime()).toString();

        }
        Calendar calendar = Calendar.getInstance();
        Date date;
        SimpleDateFormat dateFormat;
        String dateStr = null;
        switch (selectStaticsFlag) {
            case 0://年
                dateFormat = new SimpleDateFormat("yyyy年");
                try {
                    date = dateFormat.parse(mydate);
                    calendar.setTime(date);
                    calendar.add(Calendar.YEAR, i);
                    dateStr = dateFormat.format(calendar.getTime());
                    show_date.setText(String.valueOf(dateStr));
                } catch (Exception e) {
                }
                break;
            case 1://月
                dateFormat = new SimpleDateFormat("yyyy年MM月");
                try {
                    date = dateFormat.parse(mydate);
                    calendar.setTime(date);
                    calendar.add(Calendar.MONTH, i);
                    dateStr = dateFormat.format(calendar.getTime());
                    show_date.setText(String.valueOf(dateStr));
                } catch (Exception e) {
                }
                break;
            case 2:
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                try {
                    date = dateFormat.parse(mydate);
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    dateStr = dateFormat.format(calendar.getTime());
                    SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    Calendar cal = Calendar.getInstance();
                    mydate = nowDateFormat.format(cal.getTime()).toString();
                    if (dateStr.equals(mydate)) {
                        dateStr = "今天";

                    }
                    show_date.setText(String.valueOf(dateStr));
                } catch (Exception e) {
                }

            default:
                break;
        }
        try {

            updataStaisticsChart();
        } catch (Exception e) {
        }

    }

    //更新统计饼图
    private void updataStaisticsChart() {

        String strDate = show_date.getText().toString();
        if (strDate.equals("今天")) {
            SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Calendar cal = Calendar.getInstance();
            strDate = nowDateFormat.format(cal.getTime()).toString();
        }
        String tableName = null;

        switch (selectStaticsFlag) {
            case 0://年
                if (strDate.indexOf("年") != -1) {
                    strDate = strDate.replaceAll("年", "");
                }
                if (strDate.indexOf("月") != -1) {
                    strDate = strDate.replaceAll("月", "");
                }
                if (strDate.indexOf("日") != -1) {
                    strDate = strDate.replaceAll("日", "");
                }
                tableName = strDate;
                Map<String, double[]> yearAppInfoMapS = dbManager.selectAppInfoMonth(tableName);

                if (!yearAppInfoMapS.isEmpty()) {
                    if (DataSlideMenu.getStatisticsState() == 1) {
                        mPieChart.setVisibility(View.VISIBLE);
                        mBarChart.setVisibility(View.INVISIBLE);
                        DataChartClass.showPieChartYear(mPieChart, yearAppInfoMapS);
                    } else {
                        mPieChart.setVisibility(View.INVISIBLE);
                        mBarChart.setVisibility(View.VISIBLE);
                        DataChartClass.showBarChartYear(mBarChart, yearAppInfoMapS);
                    }


                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }

                break;
            case 1://月

                if (strDate.indexOf("年") != -1) {
                    strDate = strDate.replaceAll("年", "");
                }
                if (strDate.indexOf("月") != -1) {
                    strDate = strDate.replaceAll("月", "");
                }
                if (strDate.indexOf("日") != -1) {
                    strDate = strDate.replaceAll("日", "");
                }
                tableName = strDate;
                Map<String, double[]> monthAppInfoMapS = dbManager.selectAppInfoMonth(tableName);

                if (!monthAppInfoMapS.isEmpty()) {
                    if (DataSlideMenu.getStatisticsState() == 1) {
                        mPieChart.setVisibility(View.VISIBLE);
                        mBarChart.setVisibility(View.INVISIBLE);
                        DataChartClass.showPieChartMonth(mPieChart, monthAppInfoMapS);
                    } else {
                        mPieChart.setVisibility(View.INVISIBLE);
                        mBarChart.setVisibility(View.VISIBLE);
                        DataChartClass.showBarChartMonth(mBarChart, monthAppInfoMapS);
                    }


                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
                break;
            case 2://日
                if (strDate.indexOf("年") != -1) {
                    strDate = strDate.replaceAll("年", "");
                }
                if (strDate.indexOf("月") != -1) {
                    strDate = strDate.replaceAll("月", "");
                }
                if (strDate.indexOf("日") != -1) {
                    strDate = strDate.replaceAll("日", "");
                }
                tableName = strDate;
                Map<String, List> dailyAppInfoMapS = dbManager.selectAppInfoDaily(tableName);
                if (!dailyAppInfoMapS.isEmpty()) {
                    if (DataSlideMenu.getStatisticsState() == 1) {
                        mPieChart.setVisibility(View.VISIBLE);
                        mBarChart.setVisibility(View.INVISIBLE);
                        DataChartClass.showPieChartDaily(mPieChart, dailyAppInfoMapS);
                    } else {
                        mPieChart.setVisibility(View.INVISIBLE);
                        mBarChart.setVisibility(View.VISIBLE);
                        DataChartClass.showBarChartDaily(mBarChart, dailyAppInfoMapS);
                    }


                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("所选日期数据为空，请重新选择！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }
                            }).show();//在按键响应事件中显示此对话框
                }


                break;
        }

    }

    private void setDate(int month, int date, int day) {

        TextView toMonth, toDate, toDay;
        toMonth = (TextView) findViewById(R.id.today_month);
        toDate = (TextView) findViewById(R.id.today_date);
        toDay = (TextView) findViewById(R.id.today_day);
        try {
            toMonth.setText(ChangtoChinese.toChinese(String.valueOf(month)) + "月");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            toDate.setText(String.valueOf(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            toDay.setText("周" + ChangtoChinese.toChinese(String.valueOf(day)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //判断是否填充测试数据

    private Map<String, AppInfo> testAppInfo = new HashMap<>();

    private void ifFillTestData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示！");
        builder.setMessage("测试模式开启后，请耐心等待，待提示“测试数据写入成功！”后再进行其他操作");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int which) {

                if (!hasPermission()) {
                    Toast.makeText(MainActivity.this, "没有用户权限，请进入设置打开权限！", Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "没有用户权限，请进入设置打开权限！", Toast.LENGTH_LONG).show();
                    mTogBtn.setChecked(false);
                } else {
                    testAppInfo = queryAppInfo();
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fillTestDataYear();
                            fillTestDataMonth();
                            fillTestDataDaily();
                            Message msg1 = new Message();
                            msg1.what = 1;
                            mHandler.sendMessage(msg1);
                        }
                    });
                    thread.start();
                }

            }

        });
        builder.setNegativeButton("取消", null);
        builder.show();


    }

    //写入测试数据（年、月、日）
    private void fillTestDataYear() {
        //写入年数据
        if (dbManager.judgeTableExist("2017")) {

        } else {
            dbManager.creatTableYear("2017");//创建年数据总表
        }
        String[] column_nameY = {"C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10", "C11", "C12"};
        Random r = new Random();
        int num = 0;
        for (Map.Entry<String, AppInfo> entry : testAppInfo.entrySet()) {
            if (num > 15) break;
            double[] sumTime = new double[12];
            for (int i = 0; i < 12; i++) {
                sumTime[i] = r.nextInt(10000);
            }
            dbManager.addAppInfoYear("2017", entry.getKey(), column_nameY, sumTime);
            num++;
        }


    }

    private void fillTestDataMonth() {

        //写入月数据
        ////////////////////////////////////
        //3月
        String[] column_nameM = new String[31];
        for (int i = 1; i < 32; i++) {
            if (i < 10) {
                column_nameM[i - 1] = "2017030" + i;
            } else {
                column_nameM[i - 1] = "201703" + i;
            }
        }
        if (dbManager.judgeTableExist("201703")) {
        } else {
            dbManager.creatTableMonth("201703", column_nameM, 31);//创建月数据总表
        }
        int num = 0;

        Random r = new Random();
        for (Map.Entry<String, AppInfo> entry : testAppInfo.entrySet()) {
            if (num > 15) break;
            dbManager.addAppInfoMonth("201703", entry.getKey(), column_nameM[0], r.nextInt(10000));
            for (int i = 1; i < 31; i++) {
                double sumTime = r.nextInt(10000);
                dbManager.updateAppInfoMonth("201703", entry.getKey(), column_nameM[i], sumTime);
            }
            num++;
        }

        ////////////////////////////////////////////
        //2月

        column_nameM = new String[28];
        for (int i = 1; i < 29; i++) {
            if (i < 10) {
                column_nameM[i - 1] = "2017020" + i;
            } else {
                column_nameM[i - 1] = "201702" + i;
            }
        }

        if (dbManager.judgeTableExist("201702")) {

        } else {
            dbManager.creatTableMonth("201702", column_nameM, 28);//创建月数据总表
        }
        num = 0;
        for (Map.Entry<String, AppInfo> entry : testAppInfo.entrySet()) {
            if (num > 15) break;
            dbManager.addAppInfoMonth("201702", entry.getKey(), column_nameM[0], r.nextInt(10000));
            for (int i = 1; i < 28; i++) {
                double sumTime = r.nextInt(10000);
                dbManager.updateAppInfoMonth("201702", entry.getKey(), column_nameM[i], sumTime);
            }
            num++;
        }

        ///////////////////////////////////////
        //1月

        column_nameM = new String[31];
        for (int i = 1; i < 32; i++) {
            if (i < 10) {
                column_nameM[i - 1] = "2017010" + i;
            } else {
                column_nameM[i - 1] = "201701" + i;
            }
        }
        if (dbManager.judgeTableExist("201701")) {
        } else {
            dbManager.creatTableMonth("201701", column_nameM, 31);//创建月数据总表
        }
        num = 0;
        for (Map.Entry<String, AppInfo> entry : testAppInfo.entrySet()) {
            if (num > 15) break;
            dbManager.addAppInfoMonth("201701", entry.getKey(), column_nameM[0], r.nextInt(10000));
            for (int i = 1; i < 31; i++) {
                double sumTime = r.nextInt(10000);
                dbManager.updateAppInfoMonth("201701", entry.getKey(), column_nameM[i], sumTime);
            }
            num++;
        }
    }

    private void fillTestDataDaily() {
/*
        dbManager.deleteTable("2017");
        dbManager.deleteTable("201701");
        dbManager.deleteTable("201702");
        dbManager.deleteTable("201703");*/

        //////////////////////////////////////
        //写入日数据


        Calendar toDayT = Calendar.getInstance();//04-17
        for (int i = 0; i < 3; i++) {
            toDayT.add(Calendar.DAY_OF_MONTH, -1);//04-16
            addTestDayData(toDayT);
        }


    }

    //单独的函数，写入日数据
    private boolean addTestDayData(final Calendar mydate) {
        AppColors appColors = new AppColors();

        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateTable = nowDateFormat.format(mydate.getTime()).toString();
        if (dbManager.judgeTableExist(dateTable)) {
        } else {
            dbManager.creatTableDaily(dateTable);//建表
        }

        Calendar lastDay;
        Calendar nextDay;
        Calendar toDay = (Calendar) mydate.clone();

        toDay.set(Calendar.HOUR_OF_DAY, 0);
        toDay.set(Calendar.MINUTE, 0);
        toDay.set(Calendar.SECOND, 0);

        lastDay = (Calendar) toDay.clone();
        Calendar changeCalendar;

        toDay.add(Calendar.DAY_OF_MONTH, +1);
        nextDay = (Calendar) toDay.clone();

        int num = 0;
        for (Map.Entry<String, AppInfo> entry : testAppInfo.entrySet()) {
            if (num > 15) break;
            changeCalendar = (Calendar) lastDay.clone();
            changeCalendar.add(Calendar.SECOND, 2);
            for (int i = 0; i < 500; i++) {
                if (changeCalendar.compareTo(lastDay) == 1 && changeCalendar.compareTo(nextDay) == -1) {
                    String openTime = timeFormat.format(changeCalendar.getTime());
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(entry.getKey());
                    int hashCode = Math.abs(entry.getKey().hashCode());

                    String appColor = appColors.myAppColors[hashCode % 22];
                    appInfo.setAppColor(appColor);
                    appInfo.setOpenTime(openTime);
                    appInfo.setRunTime(10);
                    dbManager.addAppInfo(dateTable, appInfo);//存入每日数据库
                    Random rand = new Random();
                    changeCalendar.add(Calendar.SECOND, 10000 - rand.nextInt(20000));
                } else {
                    Random rand = new Random();
                    changeCalendar.add(Calendar.SECOND, 10000 - rand.nextInt(20000));
                }

            }
            num++;
        }
        return true;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "测试数据写入成功！", Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "测试数据写入成功！", Toast.LENGTH_LONG).show();
                startTimer();
                mTogBtn.setChecked(false);
            } else if (msg.what == 2) {
                stopTimer();
            }
        }
    };

    private AlertDialog.Builder selectAppBuilder;

    //统计界面，条目选择，选择要统计的app名称
    private void selectStatisticsApp(Set<String> appNameSet) {
        selectAppBuilder = new AlertDialog.Builder(MainActivity.this);
        selectAppBuilder.setTitle("选择查询名称");

//    指定下拉列表的显示数据


        final String[] appname = appNameSet.toArray(new String[]{});

        final boolean checkedItems[] = new boolean[appname.length];
        for (int i = 0; i < checkedItems.length; i++) checkedItems[i] = true;

        //  StringBuffer sb = new StringBuffer(100);
        //    设置一个下拉的列表选择项
        selectAppBuilder.setMultiChoiceItems(appname, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                if(isChecked&&which==0)
//                {
//                    for(int i=1;i<checkedItems.length;i++){
//                        checkedItems[i]=true;
//
//
//                    }
                        // sb.append(appname[which] + ", ");

                    }

                }

        );

        selectAppBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Set<String> checkedSet = new HashSet<String>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                checkedSet.add(appname[i]);
                            }
                        }
                        switch (selectDMY) {
                            case 1://日
                                DataChartClass.showLineChartDaily(mLineChart, dailyAppInfoMapT, checkedSet);
                                break;
                            case 2://月
                                DataChartClass.showLineChartMonth(mLineChart, monthAppInfoMapT, checkedSet);
                                break;
                            case 3://年
                                DataChartClass.showLineChartYear(mLineChart, yearAppInfoMapT, checkedSet);
                                break;
                            default:
                                break;
                        }

                    }
                }

        );
        selectAppBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }

        );

    }

    //查询获取手机安装的所有应用信息
    public Map<String, AppInfo> queryAppInfo() {
        Map<String, AppInfo> appInfoMap = new HashMap<>();
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        // 通过查询，获得所有ResolveInfo对象.
//        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, FILTER_ALL_APP);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        // Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            // 创建一个AppInfo对象，并赋值
            AppInfo appInfo = new AppInfo();
            appInfo.setAppLabel(appLabel);
            appInfo.setAppIcon(icon);
            appInfoMap.put(appLabel, appInfo);
        }
        return appInfoMap;
    }

    //修改语言
    protected void selectLanguage(String language) {
        //设置语言类型
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language) {
            case "en":
                configuration.locale = Locale.ENGLISH;
                break;
            case "cn":
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            default:
                configuration.locale = Locale.getDefault();
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);

        //保存设置语言的类型
        SPUtils.put(this, "language", language);
    }

    //语言单选框
    private Dialog simpleDialog() {
        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.settings_language_title);
        final ChoiceOnClickListener choiceListener =
                new ChoiceOnClickListener();

        builder.setSingleChoiceItems(R.array.hobby, 0, choiceListener);

        DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                int choiceWhich = choiceListener.getWhich();
                String hobbyStr =
                        getResources().getStringArray(R.array.hobby)[choiceWhich];
                if (hobbyStr.equals("中文")) {
                    DataSlideMenu.setLanguageSelection(1);
                }
                if (hobbyStr.equals("English")) {
                    DataSlideMenu.setLanguageSelection(2);
                }

                if (DataSlideMenu.getLanguageSelection() == 1) {
                    selectLanguage("cn");
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (DataSlideMenu.getLanguageSelection() == 2) {
                    selectLanguage("en");
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        };
        builder.setPositiveButton("确定", btnListener);
        dialog = builder.create();
        return dialog;
    }

    //语言选择监听器
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "---> onStop");
        super.onStop();
        writeParameter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应用的最后一个Activity关闭时应释放DB
        writeParameter();
        dbManager.closeDB();
    }

    /* @Override
     public void onConfigurationChanged(Configuration newConfig) {
         super.onConfigurationChanged(newConfig);
         Object mLanguage = SPUtils.get(this,"language", "cn");
         if (mLanguage != null){
             Resources resources = getResources();
             Configuration configuration = resources.getConfiguration();
             DisplayMetrics displayMetrics = resources.getDisplayMetrics();
             switch ((String)mLanguage) {
                 case "en":
                     configuration.locale = Locale.ENGLISH;
                     break;
                 case "cn":
                     configuration.locale = Locale.SIMPLIFIED_CHINESE;
                     break;
                 default:
                     configuration.locale = Locale.getDefault();
                     break;
             }
             resources.updateConfiguration(configuration, displayMetrics);
         }
     }*/
    //////////////////////////////////////////////////////
    //android5.0之前获得前台运行的app方法
    private AppInfo getTopActivityBeforeL() {
        //适用于Android4.4之后,5.0之前的读取方法
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> taskInfo = activityManager.getRunningAppProcesses();
        String packageName = taskInfo.get(0).processName;
        PackageManager pm = this.getPackageManager();
        String name = null;
        Drawable appIcon = null;
        String openTime;
        try {
            name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            appIcon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        openTime = df.format(new Date());
        AppInfo appInfo = new AppInfo();
        appInfo.setAppIcon(appIcon);
        appInfo.setAppLabel(name);
        appInfo.setAppColor("#FF4081FF");
        appInfo.setOpenTime(openTime);
        appInfo.setRunTime(10);
        return appInfo;
    }

    //获取app统计信息
    private List<AppInfo> statisticsAppInfo() {
        List<AppInfo> statisticsApplist = new ArrayList<>();
        Calendar beginCal = Calendar.getInstance();
        beginCal.add(Calendar.DAY_OF_YEAR, -1);
        Calendar endCal = Calendar.getInstance();
        UsageStatsManager manager = (UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        for (UsageStats us : stats) {
            try {
                PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                //applicationInfo.flags & applicationInfo.FLAG_SYSTEM)> 0 表示为系统程序
                if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) <= 0) {
                    String name = null;
                    Drawable appIcon = null;
                    long runTime = 0;
                    try {
                        name = pm.getApplicationLabel(applicationInfo).toString();
                        appIcon = pm.getApplicationIcon(us.getPackageName());
                        runTime = us.getTotalTimeInForeground();
                        if (runTime == 0) continue;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppIcon(appIcon);
                    appInfo.setAppLabel(name);
                    appInfo.setRunTime(runTime);
                    statisticsApplist.add(appInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return statisticsApplist;

    }

    //蓝牙变色相关

    // 接收 rssi 的广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ACTION_NAME_RSSI)) {
                int rssi = intent.getIntExtra("RSSI", 0);
                int rssi_avg = 0;
                int distance_cm_min = 10; // 距离cm -30dbm
                int distance_cm_max_near = 1500; // 距离cm -90dbm
                int distance_cm_max_middle = 5000; // 距离cm -90dbm
                int distance_cm_max_far = 10000; // 距离cm -90dbm
                int near = -72;
                int middle = -80;
                int far = -88;
                double distance = 0.0f;

                if (true) {
                    rssibuffer[rssibufferIndex] = rssi;
                    rssibufferIndex++;

                    if (rssibufferIndex == rssibufferSize)
                        rssiUsedFalg = true;

                    rssibufferIndex = rssibufferIndex % rssibufferSize;

                    if (rssiUsedFalg == true) {
                        int rssi_sum = 0;
                        for (int i = 0; i < rssibufferSize; i++) {
                            rssi_sum += rssibuffer[i];
                        }

                        rssi_avg = rssi_sum / rssibufferSize;

                        if (-rssi_avg < 35)
                            rssi_avg = -35;

                        if (-rssi_avg < -near) {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-near - 35))
                                    * distance_cm_max_near;
                        } else if (-rssi_avg < -middle) {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-middle - 35))
                                    * distance_cm_max_middle;
                        } else {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-far - 35))
                                    * distance_cm_max_far;
                        }
                    }
                }

            } else if (action.equals(ACTION_CONNECT)) {
                int status = intent.getIntExtra("CONNECT_STATUC", 0);
                if (status == 0) {
                    finish();
                } else {
                }
            }
        }
    };

    // 注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME_RSSI);
        myIntentFilter.addAction(ACTION_CONNECT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    // 设置参数到蓝牙设备中
    private void SetColor2Device(int white, int red, int green, int blue, int brightness) {
        byte[] PwmValue = new byte[4];

        // 特别注意以下的语句转换， amo 在这里调试了好久  ------！！！！！！！！！
        PwmValue[0] = (byte) (((white & 0xFF) * brightness / 100) & 0xff);
        PwmValue[1] = (byte) (((red & 0xFF) * brightness / 100) & 0xff);
        PwmValue[2] = (byte) (((green & 0xFF) * brightness / 100) & 0xff);
        PwmValue[3] = (byte) (((blue & 0xFF) * brightness / 100) & 0xff);

        Log.i(TAG, "----------mRed " + mRed);

        DeviceScanActivity.WriteCharX(
                DeviceScanActivity.gattCharacteristic_charA,
                PwmValue);
    }

    // 获取颜色值的字节数组
    private byte[] GetColorByte() {
        byte[] PwmValue = new byte[4];

        PwmValue[0] = (byte) (mWhite & 0xff);
        PwmValue[1] = (byte) (mRed & 0xff);
        PwmValue[2] = (byte) (mGreen & 0xff);
        PwmValue[3] = (byte) (mBlue & 0xff);

        return PwmValue;
    }

    // 获取整形值
    private int GetColorInt() {
        byte[] PwmValue = GetColorByte();
        int color = Utils.byteArrayToInt(PwmValue, 0);
        return color;
    }


    // 更新颜色分量进度
    private void UpdateText() {
        byte[] PwmValue = new byte[4];
//		int color = GetColorInt();
        // 特别注意以下的语句转换， amo 在这里调试了好久  ------！！！！！！！！！
        PwmValue[0] = (byte) (((mWhite & 0xFF) * mBrightness / 100) & 0xff);
        PwmValue[1] = (byte) (((mRed & 0xFF) * mBrightness / 100) & 0xff);
        PwmValue[2] = (byte) (((mGreen & 0xFF) * mBrightness / 100) & 0xff);
        PwmValue[3] = (byte) (((mBlue & 0xFF) * mBrightness / 100) & 0xff);

    }

    // 发送消息， 以便更新参数
    private void UpdateAllParameter() {
        Message msg = new Message();
        msg.what = REFRESH;
        mHandlerRGB.sendMessage(msg);
    }


    // 写参数到存储器
    private void writeParameter() {
        SharedPreferences.Editor sharedata = getSharedPreferences("data", 0).edit();
        sharedata.putInt("mWhite", mWhite);
        sharedata.putInt("mRed", mRed);
        sharedata.putInt("mGreen", mGreen);
        sharedata.putInt("mBlue", mBlue);
        sharedata.putInt("mBrightness", mBrightness);
        sharedata.putInt("mBrightness_old", mBrightness_old);

        sharedata.putInt("light_onoff", light_onoff);
        sharedata.putInt("light_flash", light_flash);

        sharedata.commit();
    }

    // 读出参数
    private void ReadParameter() {
        SharedPreferences sharedata = getSharedPreferences("data", 0);
        mWhite = sharedata.getInt("mWhite", 0);
        mRed = sharedata.getInt("mRed", 255);
        mGreen = sharedata.getInt("mGreen", 0);
        mBlue = sharedata.getInt("mBlue", 0);
        mBrightness = sharedata.getInt("mBrightness", 0);
        mBrightness_old = sharedata.getInt("mBrightness_old", 0);

        light_onoff = sharedata.getInt("light_onoff", 1);
        light_flash = sharedata.getInt("light_flash", 0);
    }

    //手势监听器 看开发者收藏架--0520任务
    private class simpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // Fling left
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

                mFlipper.showNext();

                // Toast.makeText(MainActivity.this, "Fling Left", Toast.LENGTH_SHORT).show();
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                // Fling right

                mFlipper.showPrevious();

                //Toast.makeText(MainActivity.this, "Fling Right", Toast.LENGTH_SHORT).show();
            }/*else if (e1.getX() - e2.getX() < 1 || e2.getX() - e1.getX() < 1 ){
                viewPager.setCurrentItem(0);
            }*/
            return true;
        }

        //不知道为什么，不加上onDown函数的话，onFling就不会响应，真是奇怪
        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            //Toast.makeText(MainActivity.this, "请按返回键返回主界面", Toast.LENGTH_SHORT).show();
            return true;
        }


    }

    private void updatAanalyzeChart() {


        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String strDate = nowDateFormat.format(cal.getTime()).toString();
        String tableName = null;
        tableName = strDate;
        Map<String, List> dailyAppInfoMapS = dbManager.selectAppInfoDaily(tableName);
        if (!dailyAppInfoMapS.isEmpty()) {

            HashMap<String, String> result = DataChartClass.showPieChartAna(mPieChartAna, dailyAppInfoMapS);
            TextView time1 = (TextView) findViewById(R.id.time1);
            TextView time2 = (TextView) findViewById(R.id.time2);
            TextView numberReport = (TextView) findViewById(R.id.numberReport);
            TextView type1 = (TextView) findViewById(R.id.type1);
            TextView type2 = (TextView) findViewById(R.id.type2);
            TextView analyzeApp = (TextView) findViewById(R.id.analyzeApp);
            time1.setText(result.get("c"));
            double sumtime = Double.valueOf(result.get("c"));
            int sumtime1 = (int) sumtime;
            time2.setText(result.get("a"));
            analyzeApp.setText(result.get("b"));


            List<String> listType1 = new ArrayList<>();
            listType1.add("社交达人");
            listType1.add("游戏大师");
            listType1.add("自拍狂人");
            listType1.add("稀有学霸");
            listType1.add("TimeGo开发者");
            listType1.add("追剧狂人");
            listType1.add("猜不透");
            List<String> listType2 = new ArrayList<>();
            listType2.add("倔强玩手机");
            listType2.add("秩序玩手机");
            listType2.add("荣耀玩手机");
            listType2.add("永恒玩手机");
            listType2.add("超凡玩手机");
            listType2.add("最强玩手机");
            java.util.Random random = new java.util.Random();// 定义随机类
            int i = random.nextInt(8);// 返回[0,10)集合中的整数，注意不包括
            type1.setText(listType1.get(i));

            if (sumtime1 >= 6)
                type2.setText(listType2.get(5));
            else type2.setText(listType2.get(sumtime1));
            numberReport.setText(String.valueOf(random.nextInt(500)));
        } else {
            Toast.makeText(MainActivity.this, "没有可用数据，暂时不能生成分析报告！", Toast.LENGTH_LONG).show();
        }


    }
}









