package com.example.timego;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.nio.ByteBuffer;


// 此代码的原作者为：   阿莫  

/*
 阿莫单片机， 蓝牙开发板专业户
 
 http://amomcu.taobao.com/ 
 
 谢谢客户朋友的支持
 */


public class RgbActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private final String ACTION_NAME_RSSI = "AMOMCU_RSSI";    // 其他文件广播的定义必须一致
    private final String ACTION_CONNECT = "AMOMCU_CONNECT";    // 其他文件广播的定义必须一致

    private TextView txtColor;
    private SeekBar seekBar_brightness;
    private TextView textView_brightness;

    private SeekBar seekBar_red;
    private SeekBar seekBar_green;
    private SeekBar seekBar_blue;
    private TextView textView_red;
    private TextView textView_green;
    private TextView textView_blue;


    int mWhite = 0;            //白色 0~255  ------这个颜色在我们的amomcu的蓝牙灯板子上无效，不过考虑到有些朋友想利用，我这里是留出了这个接口
    int mRed = 255;            //红色 0~255
    int mGreen = 0;            //绿色 0~255
    int mBlue = 0;            //蓝色 0~255

    int mBrightness = 100;                    // 这个是亮度的意思 0~100; 0最黑， 100最亮
    int mBrightness_old = mBrightness;        // 这个是亮度的意思 0~100; 0最黑， 100最亮

    public final static int WIDTH = 256;
    public final static int HEIGHT = 36;
    private Paint mPaint = new Paint();
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
    private Handler mHandler = null;

    // 开关灯
    ToggleButton toggle_onoff;
    int light_onoff = 1;

    // 律动
    ToggleButton toggle_flash;
    int light_flash = 0;
    private final int light_flash_time_ms = 800; //律动间隔

    // 此 文件中一开始就最先执行的函数就是  onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amo_rgb_activity);

        txtColor = (TextView) findViewById(R.id.txt_color);

        seekBar_brightness = (SeekBar) findViewById(R.id.seekbar_brightness);
        textView_brightness = (TextView) findViewById(R.id.txt_brightness);

        seekBar_red = (SeekBar) findViewById(R.id.seekbar_red);
        seekBar_green = (SeekBar) findViewById(R.id.seekbar_green);
        seekBar_blue = (SeekBar) findViewById(R.id.seekbar_blue);
        textView_red = (TextView) findViewById(R.id.txt_color_red);
        textView_green = (TextView) findViewById(R.id.txt_color_green);
        textView_blue = (TextView) findViewById(R.id.txt_color_blue);

        findViewById(R.id.button_about).setOnClickListener(this);

        registerBoradcastReceiver();

        // 操作
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REFRESH) {
                    SetColor2Device(mWhite, mRed, mGreen, mBlue, mBrightness);
                    UpdateProgress();
                    UpdateText();
                }
                super.handleMessage(msg);
            }
        };


        new MyThread().start();

	   /* //  大圆  颜色拾取
        myView.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void onColorChange(int color) {			     	
				String pwm = Integer.toHexString(color).toUpperCase();
				if (Utils.isHexChar(pwm) == true) {					
					byte[] PwmValue = Utils.hexStringToBytes(pwm);
					
					mWhite = PwmValue[0] & 0xff;
					mRed = PwmValue[1] & 0xff;
					mGreen = PwmValue[2] & 0xff;
					mBlue = PwmValue[3] & 0xff;
					
					//drawRgb565(color);
		            UpdateAllParameter();
				}				
			}
		});*/

        // 连读百分比函数
        seekBar_brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            //第一个时OnStartTrackingTouch,在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //第二个方法onProgressChanged是当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = seekBar.getProgress();
                textView_brightness.setText("亮度百分比: " + Integer.toHexString(i).toUpperCase() + "%");
                Log.i(TAG, "brightness = " + Integer.toHexString(i).toUpperCase());
                mBrightness = (byte) i;
                UpdateAllParameter();
            }

            //第三个是onStopTrackingTouch,在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 红色分量 监听
        seekBar_red.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            //第一个时OnStartTrackingTouch,在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //第二个方法onProgressChanged是当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int i = seekBar.getProgress();
                textView_red.setTextColor(0xFFFF0000);
                textView_red.setText("红色分量: " + Integer.toHexString(i).toUpperCase());
                Log.i(TAG, "color red = " + Integer.toHexString(i).toUpperCase());
                mRed = i;
                UpdateAllParameter();
            }

            //第三个是onStopTrackingTouch,在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 绿色分量监听
        seekBar_green.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            //第一个时OnStartTrackingTouch,在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //第二个方法onProgressChanged是当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int i = seekBar.getProgress();
                textView_green.setTextColor(0xFF00FF00);
                textView_green.setText("绿色分量: " + Integer.toHexString(i).toUpperCase());
                Log.i(TAG, "color green = " + Integer.toHexString(i).toUpperCase());
                mGreen = i;

                UpdateAllParameter();
            }

            //第三个是onStopTrackingTouch,在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 蓝色分量监听
        seekBar_blue.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            //第一个时OnStartTrackingTouch,在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //第二个方法onProgressChanged是当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int i = seekBar.getProgress();
                textView_blue.setTextColor(0xFF0000FF);
                textView_blue.setText("蓝色分量: " + Integer.toHexString(i).toUpperCase());
                Log.i(TAG, "color blue = " + Integer.toHexString(i).toUpperCase());
                mBlue = i;

                UpdateAllParameter();
            }

            //第三个是onStopTrackingTouch,在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        toggle_onoff = (ToggleButton) findViewById(R.id.togglebutton_onoff);
        toggle_onoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                Log.i(TAG, "onCheckedChanged  arg1= " + arg1);

                if (arg1 == true) {
                    light_onoff = 1;
                    mBrightness = mBrightness_old;
                    if (mBrightness == 0)// 避免灯不开
                    {
                        mBrightness = 5;
                    }
                } else {
                    light_onoff = 0;
                    mBrightness_old = mBrightness;
                    mBrightness = 0;
                }

                seekBar_brightness.setProgress(mBrightness);
                UpdateAllParameter();
            }
        });

        toggle_flash = (ToggleButton) findViewById(R.id.togglebutton_flash);
        toggle_flash.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                Log.i(TAG, "onCheckedChanged  arg1= " + arg1);
                if (arg1 == true) {
                    light_flash = 1;
                } else {
                    light_flash = 0;
                }
            }
        });


        ReadParameter();    //读出参数
        if (light_onoff == 1) toggle_onoff.setChecked(true);
        else toggle_onoff.setChecked(false);

        if (light_flash == 1) toggle_flash.setChecked(true);
        else toggle_flash.setChecked(false);


        // 更新 参数
        UpdateAllParameter();
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button_about:
//			startActivity(new Intent (RgbActivity.this, AboutActivity.class) );
//			Intent intent = new Intent();
//			intent.setClass(RgbActivity.this, AboutActivity.class);			
//			startActivityForResult(intent, 0);			
                break;
        }
    }

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
    private void UpdateProgress() {
        seekBar_red.setProgress(mRed);
        seekBar_green.setProgress(mGreen);
        seekBar_blue.setProgress(mBlue);
        seekBar_brightness.setProgress(mBrightness);
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

        txtColor.setTextColor(0xFF555555);
        txtColor.setText("当前颜色:    0x" + Utils.bytesToHexString(PwmValue).toUpperCase() + " [白红绿蓝]");
    }

    // 发送消息， 以便更新参数
    private void UpdateAllParameter() {
        Message msg = new Message();
        msg.what = REFRESH;
        mHandler.sendMessage(msg);
    }

    // 线程， 主要用于灯的闪动
    public class MyThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (light_onoff == 1
                        && light_flash == 1) {
                    mRed += (int) (Math.random() * 255);
                    mRed %= 255;

                    mGreen += (int) (Math.random() * 255);
                    mGreen %= 255;

                    mBlue += (int) (Math.random() * 255);
                    mBlue %= 255;

                    mBrightness += (int) (Math.random() * 100);
                    mBrightness %= 255;

                    Message msg = new Message();
                    msg.what = REFRESH;
                    mHandler.sendMessage(msg);
                }
                try {
                    Thread.sleep(light_flash_time_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

    @Override
    protected void onStop() {
        Log.i(TAG, "---> onStop");
        super.onStop();
        writeParameter();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "---> onDestroy");
        super.onDestroy();
        writeParameter();
    }

//	private void drawRgb565(int decLen)
//	{
//		byte[] PwmValue = GetColorByte();
//		
//		for(int i = 0; i < WIDTH*HEIGHT*4; i+=4)
//		{
//			rgb565VideoData[i+0] = PwmValue[0];
//			rgb565VideoData[i+1] = PwmValue[1];
//			rgb565VideoData[i+2] = PwmValue[2];
//			rgb565VideoData[i+3] = PwmValue[3];			
//		}		
//		
//		SurfaceView remoteSurfaceView = (SurfaceView)findViewById(R.id.remoteSurfaceView);
//		
//		Surface surface = remoteSurfaceView.getHolder().getSurface();
//		if(surface == null || !surface.isValid())
//			return;
//		
//		image_byteBuf.rewind();
//		prev_image.copyPixelsFromBuffer(image_byteBuf);
//		
//		Canvas canvas = null;
//		try {  
//			//获取Canvas来绘制界面  
//			try {
//				canvas =  surface.lockCanvas(null);
//			} 
//			catch (Throwable e) {
//				e.printStackTrace();
//				return;
//			}  
//			
//			if(canvas == null)
//				return;
//			
//			//Rect canvasRect = canvas.getClipBounds();
//
//			//int imgW = prev_image.getWidth();
//			//int imgH = prev_image.getHeight();
//
//			//int x = (canvasRect.width() - imgW) / 2;// / 2;
//			//int y = (canvasRect.height()-imgH) / 2;
//			
//			canvas.drawRGB(255, 255, 255);
//			//canvas.drawBitmap(prev_image, new Rect(0,0,prev_image.getWidth(),prev_image.getHeight()), 
//			//		new Rect(x, y, x+imgW, y+imgH), mPaint);			
//			
//	   	    canvas.drawBitmap(prev_image, new Rect(0,0,prev_image.getWidth(),prev_image.getHeight()), 
//				new Rect(0,0, canvas.getWidth(), canvas.getHeight()), mPaint);
//	   	    
//	   	 	//mPaint.setARGB(PwmValue[0], PwmValue[1], PwmValue[2], PwmValue[3]);
//	   	 	//mPaint.setColor(Utils.byteArrayToInt(PwmValue,0));
//			
//
//		} finally {  
//			// 在finally中执行该操作，这样当上面的代码抛出异常的时候  
//			//不会导致Surface出去不一致的状态。  
//			if (canvas != null) {  
//				surface.unlockCanvasAndPost(canvas);  
//			}  
//		}
//	}	
}
