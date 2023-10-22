package com.ezreal.ezchat.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.ImageUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天设置 页面
 * Created by wudeng on 2017/11/10.
 */

// 导入了 ChatSettingActivity 类所需的包

public class ChatSettingActivity extends BaseActivity {
    // ChatSettingActivity 是 BaseActivity 的子类，用于设置聊天的一些参数和选项

    @BindView(R.id.user_list)
    TagFlowLayout mListView;
    // 使用 ButterKnife 的注解 @BindView 将一个布局中的控件 user_list 与成员变量 mListView 绑定

    private List<NimUserInfo> mUserList;
    // 创建一个存储 NimUserInfo 类型对象的列表，用于存储用户信息

    private TagAdapter<NimUserInfo> mListAdapter;
    // 创建一个 TagAdapter 类型对象 mListAdapter，用于将用户信息绑定到标签视图上

    private LayoutInflater mInflater;
    // 创建一个 LayoutInflater 类型对象 mInflater，用于在界面中加载布局
//    这段代码是 ChatSettingActivity 类的成员变量声明部分，它定义了该类中所使用的成员变量和控件。这些变量和控件用于在聊天设置界面中显示用户列表和相关选项。注释解释了每个变量的作用和用途。

    @Override
    // 在活动创建时执行
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色为应用蓝色
        setStatusBarColor(R.color.app_blue_color);

        // 设置活动的布局视图为activity_chat_setting.xml
        setContentView(R.layout.activity_chat_setting);

        // 使用ButterKnife库将控件绑定到活动
        ButterKnife.bind(this);

        // 设置标题栏的标题为"聊天信息"，并启用返回按钮
        setTitleBar("聊天信息", true, false);

        // 初始化视图
        initView();
    }
//    这段代码是在活动创建时执行的，它主要用于设置状态栏颜色、加载活动的布局、绑定视图元素、设置标题栏的标题和初始化视图。

    // 初始化视图
    private void initView() {
        // 创建用户列表
        mUserList = new ArrayList<>();
        // 获取布局填充器
        mInflater = LayoutInflater.from(this);

        // 创建并设置用户列表适配器
        mListAdapter = new TagAdapter<NimUserInfo>(mUserList) {
            @Override
            public View getView(FlowLayout parent, int position, NimUserInfo nimUserInfo) {
                // 填充用户列表中的每个用户视图
                // 创建一个视图，将指定的布局文件（item_user）与该视图相关联，不将其附加到任何父视图
                View view = mInflater.inflate(R.layout.item_user, null, false);

// 从该视图中查找一个 TextView 控件，用于显示用户名称
                TextView name = (TextView) view.findViewById(R.id.tv_user_name);

// 从该视图中查找一个 ImageView 控件，用于显示用户的头像图片
                ImageView headImage = (ImageView) view.findViewById(R.id.iv_head_picture);


                // 设置用户头像
                ImageUtils.setImageByUrl(ChatSettingActivity.this, headImage, nimUserInfo.getAvatar(), R.mipmap.bg_img_defalut);

                // 设置用户姓名
                name.setText(nimUserInfo.getName());
                return view;
            }
        };

        // 设置列表视图的适配器
        mListView.setAdapter(mListAdapter);

        // 为标签视图设置标签点击监听器
        mListView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                // 这是标签点击事件的处理方法。
                // 参数view是被点击的标签视图，position是标签在标签列表中的位置，parent是包含标签的FlowLayout。
                return false;
            }
        });
    }
//    这段代码的目的是初始化一个用户列表的视图。首先，它创建一个用户列表（mUserList）和一个布局填充器（mInflater）。接下来，它创建用户列表适配器 mListAdapter，该适配器用于填充用户信息到视图中。适配器的 getView 方法将每个用户的信息呈现为视图，包括用户的头像和姓名。然后，它将适配器设置给 mListView，这是用户列表的视图组件。最后，为 mListView 添加标签点击监听器，但目前这里只返回 false。这段代码主要是为了初始化和配置用户列表的显示。

}
