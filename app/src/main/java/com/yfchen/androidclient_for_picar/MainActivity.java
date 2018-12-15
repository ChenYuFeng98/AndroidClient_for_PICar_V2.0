package com.yfchen.androidclient_for_picar;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button btnUP;
    Button btnDN;
    Button btnL;
    Button btnR;
    EditText IP_Text;
    EditText Port_Text;
    EditText PText;
    EditText IText;
    EditText DText;
    EditText InitVText;
    EditText KVText;
    EditText DVText;
    Switch SwitchCtrl;
    SeekBar seekBarL;
    SeekBar seekBarR;
    TextView PWML;
    TextView PWMR;
    public boolean isConnect = false;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public static String RV = "70";
    public static String LV = "70";
    private void findview(){
        btnUP = (Button) findViewById(R.id.btnUP);
        btnDN = (Button) findViewById(R.id.btnDN);
        btnL = (Button) findViewById(R.id.btnL);
        btnR = (Button) findViewById(R.id.btnR);
        IP_Text = (EditText) findViewById(R.id.IP_Text);
        Port_Text = (EditText) findViewById(R.id.Port_Text);
        PText = (EditText) findViewById(R.id.PText);
        IText = (EditText) findViewById(R.id.IText);
        DText = (EditText) findViewById(R.id.DText);
        InitVText = (EditText) findViewById(R.id.InitVText);
        KVText = (EditText) findViewById(R.id.KVText);
        DVText = (EditText) findViewById(R.id.DVText);
        SwitchCtrl = (Switch) findViewById(R.id.switch1);
        seekBarL = (SeekBar) findViewById(R.id.seekBarL);
        seekBarR = (SeekBar) findViewById(R.id.seekBarR);
        PWML = (TextView)findViewById(R.id.PWML);
        PWMR = (TextView)findViewById(R.id.PWMR);
    }
    public void getIPandPort()//获取编辑框
    {
        wifiModuleIp = IP_Text.getText().toString();
        wifiModulePort = Integer.valueOf(Port_Text.getText().toString());
    }
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        /**
         * 设置为shu屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }
    private void ConnectPI()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int PORT = Integer.valueOf(Port_Text.getText().toString());
                InetAddress addr = null;
                Socket socket = new Socket();
                try {
                    addr = InetAddress.getByName(IP_Text.getText().toString());//此处是树莓派的IP地址。
                    socket.connect(new InetSocketAddress(addr, PORT), 3000);
                    isConnect = true;
                } catch (SocketException e) {
                    isConnect = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isConnect = false;
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        isConnect = false;
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void sendMSG(final String msg)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int PORT = Integer.valueOf(Port_Text.getText().toString());
                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName(IP_Text.getText().toString());//此处是树莓派的IP地址。
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress(addr, PORT), 30000);
                    socket.setSendBufferSize(100);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    isConnect = true;
                    out.write(msg);
                    out.flush();
                } catch (SocketException e) {
                    isConnect = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isConnect = false;
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        isConnect = false;
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
    private void notconnect(){
        if (!isConnect)
        {
            Toast.makeText(MainActivity.this, "连接错误，请检查网络是否正常！",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findview();

        SwitchCtrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sendMSG("AUTO:"+PText.getText().toString()+":"+IText.getText().toString()+":"+DText.getText().toString()+":"+InitVText.getText().toString()+":"+KVText.getText().toString()+":"+DVText.getText().toString()+":");
                    //发送了“P:I:D:初始速度:减速系数:转弯差速”
                }else {
                    sendMSG("CTRL");
                }
                notconnect();
            }
        });

        seekBarL.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LV = Integer.toString(progress);
                PWML.setText("左轮PWM:" + LV);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("CHEN", "开始滑动！");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("CHEN", "停止滑动！");

                sendMSG("LV:"+LV);
                notconnect();
            }
        });

        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RV = Integer.toString(progress);
                PWMR.setText("右轮PWM:" + RV);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("CHEN", "开始滑动！");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("CHEN", "停止滑动！");
                sendMSG("RV:"+RV);
                notconnect();
            }
        });

        //监听
        btnUP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendMSG("UP");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendMSG("STOP");
                }
                notconnect();
                return false;

            }
        });
        btnDN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendMSG("DOWN");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendMSG("STOP");
                }
                notconnect();
                return false;
            }
        });
        btnL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendMSG("LEFT");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendMSG("RESET");
                }
                notconnect();
                return false;
            }
        });
        btnR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sendMSG("RIGHT");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sendMSG("RESET");
                }
                notconnect();
                return false;
            }
        });


        //初始化连接按钮事件
        findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "尝试连接中...",Toast.LENGTH_SHORT).show();
                ConnectPI();
                SystemClock.sleep(30);
                if (isConnect){
                    Toast.makeText(MainActivity.this, "连接成功！",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "连接失败！请检查是否连接同一WIFI！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //再按一次返回关闭
    private long lastExitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                // 判断2次点击事件时间
                if ((System.currentTimeMillis() - lastExitTime) > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出",Toast.LENGTH_SHORT).show();
                    lastExitTime = System.currentTimeMillis();
                } else {

                    finish();
                }

            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
