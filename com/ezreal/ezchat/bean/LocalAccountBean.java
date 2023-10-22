package com.ezreal.ezchat.bean;

import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;

import java.io.Serializable;

/**
 * Created by wudeng on 2017/9/1.
 */

// 定义本地账户信息的数据模型
public class LocalAccountBean implements Serializable {

    private String mHeadImgUrl; // 用户头像的URL
    private String mAccount; // 用户账号
    private String mNick; // 用户昵称
    private GenderEnum mGenderEnum; // 用户性别枚举
    private String mBirthDay; // 用户生日
    private String mLocation; // 用户所在地
    private String mSignature; // 用户个性签名

    // 获取用户头像URL
    public String getHeadImgUrl() {
        return mHeadImgUrl;
    }

    // 设置用户头像URL
    public void setHeadImgUrl(String headImgUrl) {
        mHeadImgUrl = headImgUrl;
    }

    // 获取用户账号
    public String getAccount() {
        return mAccount;
    }

    // 设置用户账号
    public void setAccount(String account) {
        mAccount = account;
    }

    // 获取用户昵称
    public String getNick() {
        return mNick;
    }

    // 设置用户昵称
    public void setNick(String nick) {
        mNick = nick;
    }

    // 获取用户性别枚举
    public GenderEnum getGenderEnum() {
        return mGenderEnum;
    }

    // 设置用户性别枚举
    public void setGenderEnum(GenderEnum genderEnum) {
        mGenderEnum = genderEnum;
    }

    // 获取用户生日
    public String getBirthDay() {
        return mBirthDay;
    }

    // 设置用户生日
    public void setBirthDay(String birthDay) {
        mBirthDay = birthDay;
    }

    // 获取用户所在地
    public String getLocation() {
        return mLocation;
    }

    // 设置用户所在地
    public void setLocation(String location) {
        mLocation = location;
    }

    // 获取用户个性签名
    public String getSignature() {
        return mSignature;
    }

    // 设置用户个性签名
    public void setSignature(String signature) {
        mSignature = signature;
    }
}
//这段代码定义了一个名为LocalAccountBean的类，用于存储本地用户账户的相关信息，包括头像URL、账号、昵称、性别、生日、所在地和个性签名。类中包括了getter和setter方法，用于获取和设置这些用户信息的属性。这些方法用于访问和修改用户的各项信息。

    // 重写 toString() 方法
    @Override
    public String toString() {
        // 创建一个字符串构建器，用于拼接对象的各个属性
        StringBuilder builder = new StringBuilder();

        // 添加 "account = " 和 mAccount 属性的值到字符串构建器
        builder.append("account = ");
        builder.append(mAccount);

        // 添加 ", url = " 和 mHeadImgUrl 属性的值到字符串构建器
        builder.append(", url = ");
        builder.append(mHeadImgUrl);

        // 添加 ", location = " 和 mLocation 属性的值到字符串构建器
        builder.append(", location = ");
        builder.append(mLocation);

        // 将字符串构建器转换为最终的字符串并返回
        return builder.toString();
    }
//    这段代码实现了 toString() 方法的重写。toString() 方法通常用于返回对象的字符串表示，以便在调试或日志记录中使用。在这里，它创建了一个包含对象属性的字符串，用于描述对象的状态。
}
