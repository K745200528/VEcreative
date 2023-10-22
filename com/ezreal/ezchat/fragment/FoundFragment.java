package com.ezreal.ezchat.fragment;

import android.view.View;
import android.view.ViewGroup;

import com.ezreal.ezchat.R;

/**
 * Created by wudeng on 2017/8/28.
 */

public class FoundFragment extends BaseFragment {
    // 创建 FoundFragment 类，它继承自 BaseFragment 类

    @Override
    public int setLayoutID() {
        // 重写 BaseFragment 中的 setLayoutID 方法
        // 该方法用于设置该 Fragment 使用的布局资源的 ID
        return R.layout.fragment_found;
        // 返回布局资源的 ID，通常会在对应的 XML 布局文件中定义 Fragment 的界面
    }

    @Override
    public void initView(View rootView) {
        // 重写 BaseFragment 中的 initView 方法
        // 该方法用于初始化界面控件和执行界面相关的操作
        // 参数 rootView 是该 Fragment 的根视图，可以在此视图上查找和操作界面控件
    }
}
//    这段代码定义了一个 FoundFragment 类，它是一个 Android Fragment，用于在应用中显示特定的界面。这个类继承自 BaseFragment，并重写了两个方法：
//
//        setLayoutID(): 这个方法返回了该 Fragment 所使用的布局资源的 ID，通常对应一个 XML 布局文件，用于定义 Fragment 的界面。
//
//        initView(View rootView): 这个方法用于初始化界面控件和执行与界面相关的操作。它接收一个 View 对象作为参数，代表该 Fragment 的根视图，你可以在这个视图上查找和操作界面控件。
//
//        这个代码段中，虽然 initView 方法是空的，但通常你会在这个方法中初始化和处理该 Fragment 的界面。这个 Fragment 将在应用的 UI 中显示，由 setLayoutID() 方法指定的布局资源定义了其外观。
