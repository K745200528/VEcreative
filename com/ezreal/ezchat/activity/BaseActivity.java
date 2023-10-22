package com.ezreal.ezchat.activity;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezreal.ezchat.ChatApplication;
import com.ezreal.ezchat.R;

/**
 * Created by wudeng on 2017/8/22.
 */

public class BaseActivity extends AppCompatActivity {

    protected ImageView mIvBack;
    // 返回按钮
    protected ImageView mIvMenu;
    // 菜单按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用父类的onCreate方法，确保正确初始化Activity。

        ChatApplication.getInstance().addActivity(this);
        // 将当前Activity添加到ChatApplication的Activity栈中。
    }
//    这段代码定义了一个名为BaseActivity的类，这是所有其他Activity的基类。它包含了两个成员变量，mIvBack和mIvMenu，它们分别代表返回按钮和菜单按钮。在BaseActivity的onCreate方法中，它调用了父类AppCompatActivity的onCreate方法，然后将当前Activity添加到ChatApplication的Activity栈中，以便在需要时管理Activity。这是一个基本的Activity设置，用于构建应用程序的其他Activity。
    @Override
    // 当活动被销毁时调用
    protected void onDestroy() {
        super.onDestroy();

        // 获取 ChatApplication 的实例并通知它关闭（结束）当前活动
        ChatApplication.getInstance().finishActivity(this);
    }
//    这段代码是在活动销毁时调用的方法。它首先调用父类的 onDestroy() 方法以确保正常销毁活动。然后，它获取 ChatApplication 的实例并使用 finishActivity(this) 通知应用程序结束当前的活动。这通常用于管理应用程序中不同活动之间的导航和生命周期。

    /**
     * 设置当前页面状态栏颜色
     * @param color 颜色值 R.color.app_blue_color
     */
    protected void setStatusBarColor(int color) {
        // 这个方法用于设置状态栏的颜色，可根据传入的颜色参数来改变状态栏的外观。
        // 在 Android 中，状态栏是显示在手机屏幕顶部的区域，可以设置其背景颜色，以适应应用的整体主题。

        // 参数color表示要设置的状态栏颜色。
    }
//    这个方法的用途是设置状态栏的颜色，以使其适应应用的主题。在 Android 应用中，状态栏是显示在屏幕顶部的区域，通常包括时间、信号强度、电池电量等信息。设置状态栏颜色可以改变状态栏的外观，以与应用的整体风格一致。

    /**
     * 设置标题栏 需确定 该页面的layout布局文件 include title_layout
     * @param titleName 标题
     * @param showBackIcon 是否显示返回按钮
     * @param showMenuIcon 是否显示菜单按钮
     */
    // 设置标题栏的标题、返回按钮和菜单按钮是否可见
    protected void setTitleBar(String titleName, boolean showBackIcon, boolean showMenuIcon) {
        try {
            // 获取标题视图
            TextView title = findViewById(R.id.tv_title);

            // 设置标题文本为给定的标题名
            title.setText(titleName);

            // 获取返回按钮视图
            mIvBack = findViewById(R.id.iv_back_btn);

            // 获取菜单按钮视图
            mIvMenu = findViewById(R.id.iv_menu_btn);

            // 根据参数决定是否显示返回按钮
            if (showBackIcon) {
                mIvBack.setVisibility(View.VISIBLE);
            }

            // 根据参数决定是否显示菜单按钮
            if (showMenuIcon) {
                mIvMenu.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    这段代码是一个用于设置标题栏的方法。该方法的参数包括标题名、是否显示返回按钮和是否显示菜单按钮。它首先获取标题、返回按钮和菜单按钮的视图，然后根据传入的参数控制它们的可见性。如果showBackIcon为true，则显示返回按钮；如果showMenuIcon为true，则显示菜单按钮。这个方法用于自定义标题栏的显示和操作。
}
