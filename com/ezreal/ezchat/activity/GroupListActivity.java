package com.ezreal.ezchat.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;

import butterknife.ButterKnife;

/**
 * 群组列表页
 * created by wudeng on 2019/01/19.
 */
public class GroupListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用基类的onCreate方法，初始化活动
        setStatusBarColor(R.color.app_blue_color);
        // 设置状态栏颜色为应用蓝色
        setContentView(R.layout.activity_group_list);
        // 使用activity_group_list布局文件设置当前活动的用户界面
        ButterKnife.bind(this);
        // 使用ButterKnife库绑定视图元素
    }
}
//这段代码是GroupListActivity类的onCreate方法，用于初始化活动。它执行了以下操作：
//
//        调用基类的onCreate方法，以确保活动的正确初始化。
//        使用setStatusBarColor方法设置状态栏的颜色为应用蓝色。
//        使用setContentView方法设置活动的用户界面，使用activity_group_list布局文件定义界面布局。
//        使用ButterKnife库的bind方法绑定视图元素，以便后续可以直接访问和操作它们。
