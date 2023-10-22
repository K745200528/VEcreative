package com.ezreal.ezchat.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Created by wudeng on 2017/8/28.
 */

public abstract class BaseFragment extends Fragment {
    // 定义了一个抽象基础片段类 BaseFragment，继承自 Android 的 Fragment 类

    protected View mRootView;
    // 声明了一个成员变量 mRootView，用于表示片段的根视图

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 在 Android 应用中，onCreate 方法通常用于活动（Activity）或片段（Fragment）的初始化。
        // 这个方法在创建活动时被调用，通常在活动第一次创建时执行。
        // 参数 savedInstanceState 用于恢复之前的状态，如果应用关闭后重新启动，可以从 savedInstanceState 中获取之前保存的数据。
        super.onCreate(savedInstanceState);
        // 调用父类的 onCreate 方法，确保执行父类的初始化操作。
    }
//    这段代码是 Android 开发中的一个生命周期方法 onCreate，通常用于初始化一个活动（Activity）或片段（Fragment）。当应用中的活动被创建时，系统会自动调用这个方法。参数 savedInstanceState 用于恢复之前保存的状态信息。通常，你可以在 onCreate 方法中进行一些初始化工作，例如设置布局、初始化变量、绑定视图等。在这个方法中，调用 super.onCreate(savedInstanceState) 是一种最佳实践，以确保执行父类的初始化操作。

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            // 如果 mRootView 为空（尚未初始化），则执行以下代码块
            mRootView = inflater.inflate(setLayoutID(), container, false);
            // 使用 LayoutInflater 的 inflate 方法加载布局资源，这里使用 setLayoutID() 方法指定要加载的布局文件，
            // container 是加载布局的父容器，false 表示不要立即将布局添加到 container 中，因为在 Fragment 中，系统会负责添加。
            initView(mRootView);
            // 初始化视图，通常是查找和设置布局中的控件，这里使用 initView() 方法完成。
        }

        //缓存的rootView需要判断是否已经被加过parent
        //如果有parent需要从parent删除，要不然会发生这个rootView已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
//    这段代码是 Android 中 Fragment 的生命周期方法 onCreateView 的重写部分。它的主要作用是在 Fragment 第一次创建视图时加载布局和初始化视图元素。

    public abstract int setLayoutID();
// 抽象方法 setLayoutID，用于设置布局资源文件的 ID。

    public abstract void initView(View rootView);
// 抽象方法 initView，用于初始化视图元素，通常在这里进行视图的查找和初始化操作。

//    这段代码定义了一个抽象类，其中包含两个抽象方法：
//
//    setLayoutID 方法是一个抽象方法，用于获取布局资源文件的 ID。这个方法的实现由派生类完成，以指定该抽象类所代表的活动或片段的布局资源。
//
//    initView 方法也是一个抽象方法，用于初始化视图元素。通常，在这个方法内进行视图的查找和初始化操作，以确保与用户界面相关的元素可以被正确使用和操作。派生类需要实现这个方法来执行视图的初始化工作。
//
//    这种抽象方法的使用允许不同的活动或片段类通过实现这些方法来指定它们的布局资源和视图初始化逻辑，使代码更具可扩展性和重用性。
}
