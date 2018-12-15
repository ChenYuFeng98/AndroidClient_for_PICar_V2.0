package com.yfchen.androidclient_for_picar;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class FlashActivity extends AppCompatActivity {

    android.os.Handler sHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        Toast.makeText(FlashActivity.this, "Welcome..", Toast.LENGTH_SHORT).show();
        //打开界面延迟切换
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //全屏显示
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮事件
                Intent intent = new Intent(FlashActivity.this,MainActivity.class);
                startActivities(new Intent[]{intent});
                finish();
            }
        });

        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FlashActivity.this,MainActivity.class);
                startActivities(new Intent[]{intent});
                finish();

            }
        }, 1500);

    }
}
