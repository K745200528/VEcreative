package com.ezreal.ezchat.bean;

import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

/**
 * Created by wudeng on 2017/9/11.
 */

// 定义一个类叫做 RecentContactBean
public class RecentContactBean {
    // 用于存储 RecentContact 对象，包含最近联系人的信息
    private RecentContact mRecentContact;

    // 用于存储 UserInfo 对象，包含用户信息
    private UserInfo mUserInfo;

    // 获取最近联系人对象的方法
    public RecentContact getRecentContact() {
        return mRecentContact;
    }

    // 设置最近联系人对象的方法
    public void setRecentContact(RecentContact recentContact) {
        mRecentContact = recentContact;
    }

    // 获取用户信息对象的方法
    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    // 设置用户信息对象的方法
    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }
}
//    这段代码定义了一个名为 RecentContactBean 的类，该类用于封装最近联系人（RecentContact）和用户信息（UserInfo）。它包括了获取和设置最近联系人对象和用户信息对象的方法，以便在应用程序中使用这些数据。这种封装可以用于更方便地管理和操作最近联系人和用户信息的数据。
