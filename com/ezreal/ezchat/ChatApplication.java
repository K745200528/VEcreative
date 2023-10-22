package com.ezreal.ezchat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;

import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.ezreal.ezchat.activity.MainActivity;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.utils.Constant;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.suntek.commonlibrary.utils.FileUtils;
import com.suntek.commonlibrary.utils.SharedPreferencesUtil;

import java.io.File;
import java.util.Stack;

/**
 * Created by wudeng on 2017/8/22.
 */

// ChatApplication 是一个自定义的 Application 类，它用于在整个应用程序生命周期内维护一些全局状态和变量。

public class ChatApplication extends Application {
    // ChatApplication 继承自 Application 类，表示这是一个应用程序级别的类。

    private static ChatApplication instance;
    // 创建一个 ChatApplication 的实例，这里使用了单例模式来确保全局唯一的 ChatApplication 实例。

    private static Stack<Activity> sActivityStack;
    // 创建一个存储 Activity 的栈，用于跟踪应用中的 Activity 实例。
//    这段代码定义了 ChatApplication 类，它用于全局管理应用程序的状态。instance 是 ChatApplication 的单例实例，确保在整个应用程序生命周期内只有一个应用程序实例。sActivityStack 是一个堆栈，用于跟踪应用程序的 Activity 实例，这在管理 Activity 的生命周期和任务导航中非常有用。

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 在应用启动时执行一些初始化操作

        // 解决 Android 7.0 及以上系统中照相机启动失败导致 APP 崩溃的问题
        // 检查当前 Android 版本是否大于或等于 Nougat (Android 7.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 如果是 Nougat 或更高版本，启用严格模式策略以限制应用的 VM 访问权限

            // 创建一个 StrictMode.VmPolicy.Builder 对象，用于配置 VM 策略
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();

            // 使用 builder 构建的策略来设置 VM 策略
            StrictMode.setVmPolicy(builder.build());
        }

        // 创建一个用于存储 Activity 实例的栈
        sActivityStack = new Stack<>();

        // 初始化网易云通讯 SDK
        NIMClient.init(this, getLoginInfo(), getOptions());
    }
//    这段代码在应用启动时执行了一些必要的初始化操作，包括解决了在 Android 7.0 及以上版本中可能出现的照相机启动问题，并初始化了网易云通讯 SDK 以提供聊天功能。

    // 重写attachBaseContext方法，用于分包处理

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 调用MultiDex类的install方法，实现分包处理
    }
//    这段代码是为了实现Android应用的分包处理。在Android开发中，当应用的方法数、类数等超过一定限制时，会出现方法数限制（DEX方法数限制），这时就需要使用Android的MultiDex机制来支持更多的方法数。attachBaseContext方法是Application类的一个回调方法，用于初始化应用的Context。在这里，它被重写以在应用启动时调用MultiDex.install(this)来启用MultiDex。

    private SDKOptions getOptions() {
        // 创建一个 SDKOptions 对象
        SDKOptions options = new SDKOptions();

        // 设置 SDK 存储根目录，用于存储日志、文件、图片、音频、视频和缩略图等文件
        // 如果需要清理缓存，可以清理这些子目录的内容
        if (!new File(Constant.APP_CACHE_PATH).exists()) {
            FileUtils.mkDir(Constant.APP_CACHE_PATH);
        }
        options.sdkStorageRootPath = Constant.APP_CACHE_PATH;

        // 用户资料提供者，主要用于提供用户资料，以在新消息通知中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                // 获取用户信息，通常用于获取用户的头像和昵称
                return NIMClient.getService(UserService.class).getUserInfo(account);
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account,
                                                           String sessionId, SessionTypeEnum sessionType) {
                // 获取消息通知中的消息来源显示名称
                return account;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
                // 获取消息通知中的消息来源头像
                return BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo);
            }
        };

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置状态栏通知的参数
        // 创建 StatusBarNotificationConfig 实例，用于配置通知栏通知的属性
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();

// 设置是否开启通知的铃声
        config.ring = false;

// 设置通知点击的入口 Activity，当用户点击通知时会启动 MainActivity
        config.notificationEntrance = MainActivity.class;

// 设置是否开启通知的震动
        config.vibrate = true;

        // 将状态栏通知配置设置到 SDKOptions 中
        options.statusBarNotificationConfig = config;

        // 返回配置好的 SDKOptions
        return options;
    }
//    这段代码的目的是配置 SDK 的一些选项。其中包括存储根目录、用户资料提供者、是否预下载附件缩略图、状态栏通知配置等。这些选项用于定制和配置 SDK 的行为和功能。

    // 获取登录信息的方法
    private LoginInfo getLoginInfo() {
        // 从本地存储中获取用户账号和令牌
        // 通过 SharedPreferencesUtil 从本地存储中获取用户账户信息
        String account = SharedPreferencesUtil.getStringSharedPreferences(this,
                Constant.LOCAL_LOGIN_TABLE, Constant.LOCAL_USER_ACCOUNT);

// 通过 SharedPreferencesUtil 从本地存储中获取用户令牌信息
        String token = SharedPreferencesUtil.getStringSharedPreferences(this,
                Constant.LOCAL_LOGIN_TABLE, Constant.LOCAL_USER_TOKEN);


        // 如果账号和令牌都不为空
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            // 创建登录信息对象
            LoginInfo info = new LoginInfo(account, token);

            // 将账号设置为当前用户的账号
            NimUserHandler.getInstance().setMyAccount(account);

            // 返回登录信息
            return info;
        }

        // 如果账号或令牌为空，返回空
        return null;
    }
//    这段代码用于获取用户的登录信息。它首先从本地存储中获取用户的账号和令牌。如果账号和令牌都不为空，它会创建一个LoginInfo对象，将账号和令牌传递给该对象，并设置当前用户的账号。最后，它返回LoginInfo对象。如果账号或令牌为空，它返回null。这是用于在初始化用户登录时检查用户是否已经登录的方法。

    // 获取 ChatApplication 的实例
    public static ChatApplication getInstance() {
        return instance;
    }

    // 将活动添加到活动栈中
    public void addActivity(Activity activity) {
        sActivityStack.add(activity);
    }

    // 结束指定活动，从活动栈中移除并关闭活动
    public void finishActivity(Activity activity) {
        sActivityStack.remove(activity);
        activity.finish();
    }
//    这些方法是 ChatApplication 类的公共方法，用于获取应用程序的实例、将活动添加到活动栈并结束（关闭）活动。这些方法可以用于管理应用程序的活动栈，通常在应用程序的生命周期中调用，例如在活动启动和销毁时。 getInstance() 用于获取应用程序的实例，addActivity() 用于添加活动到活动栈，finishActivity() 用于从栈中移除并关闭指定的活动。

    // 关闭应用程序中的所有活动
    public void finishAllActivity() {
        // 遍历活动堆栈中的所有活动
        for (int i = 0; i < sActivityStack.size(); i++) {
            if (sActivityStack.get(i) != null) {
                // 关闭当前活动
                sActivityStack.get(i).finish();
            }
        }
    }

    // 退出应用程序
    public void AppExit() {
        try {
            // 首先关闭所有活动
            finishAllActivity();

            // 结束当前应用程序的进程
            Process.killProcess(Process.myPid());

            // 退出虚拟机
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    这段代码定义了两个方法，用于关闭应用程序中的所有活动和退出应用程序。finishAllActivity() 方法遍历活动堆栈中的所有活动并关闭它们。AppExit() 方法首先调用 finishAllActivity() 关闭所有活动，然后结束应用程序的进程和退出虚拟机。这是一种强制性退出应用程序的方法，通常用于处理异常情况。

}
