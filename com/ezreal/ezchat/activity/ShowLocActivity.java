package com.ezreal.ezchat.activity;

import android.os.Bundle;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/10/30.
 */

// 引入所需库后，定义 ShowLocActivity 类继承 BaseActivity 类
public class ShowLocActivity extends BaseActivity implements AMapLocationListener {
    // 使用 ButterKnife 进行视图绑定
    @BindView(R.id.map_view)
    MapView mMapView;  // 显示地图的视图
    @BindView(R.id.tv_address)
    TextView mTvAddress;  // 显示地址信息的文本视图

    private IMMessage mIMMessage;  // 存储 IM 消息的变量

    private AMap mAMap;  // 高德地图对象
    private AMapLocationClient mLocationClient;  // 高德定位客户端对象
    private float mZoomLevel = 16.0f;  // 地图缩放级别
//    这段代码定义了一个名为 ShowLocActivity 的类，该类是 Android 应用中的一个活动。它主要用于显示地理位置信息，并包含以下关键元素：
//
//    @BindView: 这是 ButterKnife 库的注解，用于将 XML 布局中的视图元素与 Java 代码中的变量进行绑定。
//
//    IMMessage mIMMessage: 用于存储 IM（即即时消息）的消息对象。
//
//    AMap mAMap: 用于操作高德地图的对象。
//
//    AMapLocationClient mLocationClient: 用于处理高德定位服务的客户端对象。
//
//    float mZoomLevel: 存储地图的缩放级别。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.app_blue_color);
        // 设置状态栏颜色为应用蓝色

        setContentView(R.layout.activity_show_loc);
        // 使用指定的布局文件设置当前 Activity 的内容视图

        ButterKnife.bind(this);
        // 使用 ButterKnife 绑定视图和控件

        setTitleBar("位置信息", true, false);
        // 设置标题栏标题为“位置信息”，并显示返回按钮但不显示更多按钮

        initMap(savedInstanceState);
        // 调用初始化地图的方法

        showMsgLocation();
        // 显示消息中的位置信息
    }
//    这段代码是 onCreate 方法，用于初始化 ShowLocActivity，一个用于显示位置信息的 Android Activity。具体来说：
//
//            super.onCreate(savedInstanceState); 调用基类 onCreate 方法，这是 Android Activity 声明周期的一部分。
//
//    setStatusBarColor(R.color.app_blue_color); 设置状态栏颜色为应用蓝色，这可能是自定义的方法。
//
//    setContentView(R.layout.activity_show_loc); 使用指定的布局文件 activity_show_loc 设置当前 Activity 的内容视图。
//
//            ButterKnife.bind(this); 使用 ButterKnife 框架来绑定视图和控件，简化视图操作。
//
//    setTitleBar("位置信息", true, false); 设置标题栏标题为“位置信息”，并显示返回按钮但不显示更多按钮。
//
//    initMap(savedInstanceState); 调用 initMap 方法，初始化地图，这可能会涉及到地图视图的初始化。
//
//    showMsgLocation(); 调用 showMsgLocation 方法，显示消息中的位置信息，这可能是根据数据显示位置信息的功能。

    // 初始化地图
    private void initMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();

        // 初始位置，设置地图中心点为广州市中心的经纬度坐标
        LatLng latLng = new LatLng(23.13023, 113.253171);

        // 创建一个摄像头更新对象，将地图的视角移动到指定经纬度，并设置缩放级别
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
        mAMap.animateCamera(cameraUpdate);

        // 获取地图的 UI 设置
        UiSettings settings = mAMap.getUiSettings();

        // 启用缩放手势
        settings.setZoomGesturesEnabled(true);

        // 禁用“我的位置”按钮
        settings.setMyLocationButtonEnabled(false);

        // 禁用缩放控件
        settings.setZoomControlsEnabled(false);

        // 创建定位客户端
        mLocationClient = new AMapLocationClient(this);

        // 配置定位选项
        AMapLocationClientOption option = new AMapLocationClientOption();

        // 关闭优先使用 GPS 定位
        option.setGpsFirst(false);

        // 设置定位模式为高精度模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        // 单次定位，只获取一次位置信息
        option.setOnceLocation(true);

        // 将定位选项应用于定位客户端
        mLocationClient.setLocationOption(option);

        // 设置定位监听器
        mLocationClient.setLocationListener(this);
    }
//    这段代码的目的是初始化地图控件，设置地图的初始位置、缩放级别以及用户界面设置。还包括初始化定位功能，包括配置定位选项和设置定位监听器。地图的UI设置允许控制地图操作，包括启用或禁用缩放手势、"我的位置"按钮和缩放控件。


    // 显示消息中的地理位置
    private void showMsgLocation() {
        // 从Intent中获取IMMessage对象
        mIMMessage = (IMMessage) getIntent().getSerializableExtra("IMMessage");

        // 从IMMessage中获取地理位置附件(LocationAttachment)
        LocationAttachment attachment = (LocationAttachment) mIMMessage.getAttachment();

        if (attachment == null) {
            // 若附件为空，显示提示消息并结束活动
            ToastUtils.showMessage(this, "附件获取失败，请重试~");
            finish();
            return;
        }

        // 获取地理坐标的经度和纬度
        double latitude = attachment.getLatitude();
        double longitude = attachment.getLongitude();

        if (latitude < 0.0 || longitude < 0.0) {
            // 若地理坐标无效，显示提示消息
            ToastUtils.showMessage(this, "地理坐标失效，无法显示!");
        } else {
            // 创建一个LatLng对象，表示地理坐标
            LatLng latLng = new LatLng(latitude, longitude);

            // 创建标记选项
            MarkerOptions options = new MarkerOptions();

            // 设置标记的图标
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_center);
            options.icon(descriptor);

            // 设置标记的位置
            options.position(latLng);

            // 在地图上添加标记
            mAMap.addMarker(options);

            // 移动地图视角到标记位置
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
            mAMap.animateCamera(cameraUpdate);
        }

        // 获取地理位置的描述
        String address = attachment.getAddress();
        if (!TextUtils.isEmpty(address)) {
            // 如果有地址描述，设置到文本视图中
            mTvAddress.setText(address);
        } else {
            // 如果没有地址描述，显示默认消息
            mTvAddress.setText("地址描述获取失败……");
        }
    }
//    这段代码用于显示消息中的地理位置信息。首先，从Intent中获取IMMessage对象，然后提取其中的地理位置附件(LocationAttachment)。如果附件为空，会显示提示消息并结束活动。然后，从地理位置附件中获取经度和纬度信息，如果这些信息无效，会再次显示提示消息。接着，将地理坐标在地图上以标记的方式展示，并移动地图视角到标记的位置。最后，获取地理位置的描述信息，如果有描述，将其显示在文本视图中，否则显示默认消息。

    @OnClick(R.id.iv_my_location)
    public void location() {
        // 当点击控件（R.id.iv_my_location）时触发的方法。

        // 启动定位客户端以获取用户当前位置。
        mLocationClient.startLocation();
    }
//    这段代码使用了 @OnClick 注解，当具有 R.id.iv_my_location ID 的控件被点击时，将调用名为 location 的方法。在这个方法中，使用 mLocationClient 启动了定位服务，从而获取用户当前位置的信息。

    // 当地理位置发生改变时被调用
    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            // 检查定位是否成功（没有错误）
            if (location.getErrorCode() == 0) {
                // 获取定位的经纬度坐标
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude);

                // 创建一个标记选项对象
                MarkerOptions options = new MarkerOptions();

                // 从资源文件中获取标记图标
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_loc);

                // 设置标记图标
                options.icon(descriptor);

                // 设置标记的位置
                options.position(latLng);

                // 将标记添加到地图上
                mAMap.addMarker(options);

                // 创建一个摄像头更新对象，将地图的视角移动到定位的经纬度坐标，并设置缩放级别
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);

                // 平滑地将地图视角移动到新位置
                mAMap.animateCamera(cameraUpdate);
            }
        }
    }
//    这段代码实现了 onLocationChanged 方法，该方法是 AMapLocationListener 接口的一部分。它用于处理地理位置发生变化的情况。首先，它检查定位是否成功，如果没有错误，就获取了定位的经纬度坐标。接着，它创建一个标记选项对象，设置标记的图标和位置，最后将标记添加到地图上。然后，通过创建摄像头更新对象，平滑地将地图视角移动到定位的位置，同时设置缩放级别。

    @Override
    protected void onResume() {
        super.onResume();

        // 当 Activity 进入 resumed 状态时，调用地图控件的 onResume 方法，用于恢复地图显示。
        mMapView.onResume();
    }
//    在 Android Activity 的 onResume 方法中，调用地图控件（mMapView）的 onResume 方法。这是为了在 Activity 进入 resumed 状态时，恢复地图的显示和交互。当用户返回到该 Activity 时，地图可以正常工作。

    // 当 Activity 进入暂停状态时执行
    @Override
    protected void onPause() {
        super.onPause();

        // 调用地图控件的 onPause 方法
        mMapView.onPause();
    }
//    这段代码在 Android Activity 的 onPause 方法中，调用了地图控件 mMapView 的 onPause 方法，用于通知地图控件暂停活动，释放相关资源。这是为了确保在 Activity 不可见时地图控件能够正确地释放资源，以避免内存泄漏和不必要的开销。

    // 在 Activity 销毁时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁地图控件
        mMapView.onDestroy();

        // 销毁定位客户端
        mLocationClient.onDestroy();
    }
//    在 Android Activity 销毁时，会调用 onDestroy() 方法。在这段代码中，主要进行了两个操作的销毁：
//
//            mMapView.onDestroy(): 这里是销毁地图控件 mMapView，释放地图相关的资源。在地图控件的生命周期管理中，通常需要在 Activity 或 Fragment 销毁时调用它。
//
//            mLocationClient.onDestroy(): 这里是销毁定位客户端 mLocationClient，释放定位服务相关的资源。如果你在 Activity 中使用了高德地图的定位功能，需要在销毁时调用这个方法，以释放定位服务占用的资源。

    // 注解 @OnClick 表示这是一个点击事件处理方法，对应的视图资源 ID 是 R.id.iv_back_btn。
    @OnClick(R.id.iv_back_btn)
    public void back() {
        finish(); // 当点击对应的视图（这里是返回按钮）时，调用 finish() 方法，关闭当前 Activity。
    }
//    这段代码是一个点击事件处理方法，它在用户点击返回按钮（资源 ID 为 iv_back_btn）时触发。触发后，调用 finish() 方法来关闭当前的 Activity，实现返回操作。这是一个常见的 Android 界面导航操作，当用户点击返回按钮时，通常会关闭当前界面并返回到上一个界面。

    @OnClick(R.id.iv_navigation)
    public void navigation() {
        // 打开地图导航
    }
//    这段代码的目的是为名为 "iv_navigation" 的视图添加点击事件处理方法。当用户点击这个视图时，调用 navigation 方法，通常用于启动地图导航或其他相关操作。
}
