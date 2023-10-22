package com.ezreal.ezchat.bean;

import com.amap.api.services.core.LatLonPoint;

/**
 * 分享位置，单个位置点信息
 * Created by wudeng on 2017/9/19.
 */

public class LocationPoint {

    private String mId;
    // 地点的唯一标识符

    private String mName;
    // 地点的名称

    private String mAddress;
    // 地点的地址

    private boolean mSelected;
    // 地点是否被选中的状态

    private LatLonPoint mPoint;
    // 地点的经纬度坐标

    public String getId() {
        return mId;
    }
    // 获取地点的唯一标识符

    public void setId(String id) {
        mId = id;
    }
    // 设置地点的唯一标识符

    public String getName() {
        return mName;
    }
    // 获取地点的名称

    public void setName(String name) {
        mName = name;
    }
    // 设置地点的名称

    public String getAddress() {
        return mAddress;
    }
    // 获取地点的地址

    public void setAddress(String address) {
        mAddress = address;
    }
    // 设置地点的地址

    public boolean isSelected() {
        return mSelected;
    }
    // 检查地点是否被选中

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
    // 设置地点的选中状态

    public LatLonPoint getPoint() {
        return mPoint;
    }
    // 获取地点的经纬度坐标

    public void setPoint(LatLonPoint point) {
        mPoint = point;
    }
    // 设置地点的经纬度坐标
}
//    这段代码定义了一个 LocationPoint 类，表示地点信息，包括唯一标识符、名称、地址、选中状态和经纬度坐标。类中包括了获取和设置这些属性的方法。这个类通常用于表示地图上的点标记信息。
