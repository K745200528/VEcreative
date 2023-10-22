package com.ezreal.ezchat.bean;

import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;


/**
 * Created by wudeng on 2017/8/30.
 */

public class AddFriendNotify {
    private SystemMessage mMessage;
    // 存储系统消息的对象，通常表示好友请求等
    private NimUserInfo mUserInfo;
    // 存储云信用户信息的对象，通常表示用户的详细信息
    private boolean isMyFriend;
    // 标识是否为我的好友

    // 获取系统消息对象
    public SystemMessage getMessage() {
        return mMessage;
    }

    // 设置系统消息对象
    public void setMessage(SystemMessage message) {
        mMessage = message;
    }

    // 获取云信用户信息对象
    public NimUserInfo getUserInfo() {
        return mUserInfo;
    }

    // 设置云信用户信息对象
    public void setUserInfo(NimUserInfo userInfo) {
        mUserInfo = userInfo;
    }

    // 检查是否为我的好友
    public boolean isMyFriend() {
        return isMyFriend;
    }

    // 设置是否为我的好友
    public void setMyFriend(boolean myFriend) {
        isMyFriend = myFriend;
    }
}
//    这段代码定义了一个名为 AddFriendNotify 的类，该类用于存储好友请求相关的信息。具体包括以下三个字段和相应的 getter 和 setter 方法：
//
//        mMessage：存储系统消息对象，通常用于表示好友请求等系统级消息。
//        mUserInfo：存储云信用户信息对象，通常表示用户的详细信息。
//        isMyFriend：标识是否为我的好友，是一个布尔值，用于表示该用户是否已成为好友。
//        这些 getter 和 setter 方法用于获取和设置类内部的字段值，以便其他部分的代码可以访问和修改这些信息。
