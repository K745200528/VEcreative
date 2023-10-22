package com.ezreal.ezchat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.utils.Constant;


public class SplashActivity extends BaseActivity {

    @SuppressLint("HandlerLeak")
    // 创建一个处理程序，用于处理消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 检查消息标识
            if (msg.what == 1) {
                // 若已存在登录信息则直接打开主页面，在主页面中判断用户状态决定下一步操作
                if (NimUserHandler.getInstance().getMyAccount() != null) {
                    // 如果已经存在用户登录信息，启动主页面 MainActivity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    // 若登录信息为空，则启动登录页面 LoginActivity，进入手动登录流程
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        }
    };
//    这段代码创建了一个处理程序，通常用于在 Android 应用中处理异步消息。在 handleMessage 方法中，根据收到的消息标识 (msg.what) 执行相应的操作。在这里，msg.what 的值为1时，检查是否存在登录信息，如果有，则打开主页面，否则启动登录页面。这个处理程序主要用于在应用启动时决定用户进入自动登录流程还是手动登录流程。

    // 当 Activity 创建时调用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置当前的布局视图为R.layout.activity_splash
        setContentView(R.layout.activity_splash);

        // 通过 mHandler 发送一个延时消息，消息标识为1，延时1秒（1000毫秒）
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }
//    这段代码用于初始化和配置 Activity（活动），它的主要功能如下：
//
//    @Override：这是 Java 注解，表示下面的方法是覆盖自父类的方法。
//
//    protected void onCreate(Bundle savedInstanceState)：这是一个 Android 生命周期方法，表示 Activity 创建时要执行的操作。
//
//            super.onCreate(savedInstanceState)：调用父类的 onCreate 方法，确保父类的初始化代码得以执行。
//
//    setContentView(R.layout.activity_splash)：将当前的布局设置为 R.layout.activity_splash，这将显示与该布局相关的 UI 元素。
//
//            mHandler.sendEmptyMessageDelayed(1, 1000)：通过 mHandler 发送一个延时消息，消息的标识为1，延时1秒（1000毫秒）。这通常用于实现延时操作，例如在启动界面后延迟一段时间再执行其他操作。
}
//    这段代码是 SplashActivity 类，用于应用的启动页。在 onCreate 方法中，它设置了视图为 activity_splash，然后使用 Handler 发送一个消息，延迟1秒执行，之后会根据用户登录状态来跳转到主页面或登录页面。如果用户已经登录，则直接跳转到主页面，否则跳转到登录页面。这个延迟的过程给应用提供了时间来初始化和准备好显示内容。