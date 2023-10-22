package com.ezreal.ezchat.chat;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by wudeng on 2017/9/13.
 */

public class ChatSession {
    // 定义了 ChatSession 类

    private SessionTypeEnum mSessionType;
    // 存储聊天会话的类型，例如单聊、群聊、聊天室等
    private String mSessionId;
    // 存储会话的唯一标识符
    private String mMyAccount;
    // 存储当前用户的帐号信息
    private String mChatAccount;
    // 存储聊天对方的帐号信息
    private String mChatNick;
    // 存储聊天对方的昵称
    private NimUserInfo mMyInfo;
    // 存储当前用户的详细信息
    private NimUserInfo mChatInfo;
    // 存储聊天对方的详细信息
//    这段代码定义了一个 ChatSession 类，用于存储聊天会话的相关信息。这些信息包括会话的类型（单聊、群聊、聊天室等）、会话的唯一标识符、当前用户的帐号信息、聊天对方的帐号信息、聊天对方的昵称以及当前用户和聊天对方的详细信息。
//
//    通常，这个类的实例会在聊天应用中用于管理不同的聊天会话，以便快速访问和处理与这些会话相关的信息。它可以用于构建聊天界面、显示会话列表、管理消息历史记录等。这个类的属性和方法可能会在整个聊天应用中用于跟踪和管理聊天会话。

    public SessionTypeEnum getSessionType() {
        // 获取会话类型（单聊、群聊等）
        return mSessionType;
    }

    public void setSessionType(SessionTypeEnum sessionType) {
        // 设置会话类型
        mSessionType = sessionType;
    }

    public String getSessionId() {
        // 获取会话ID，通常是目标用户或群组的唯一标识
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        // 设置会话ID
        mSessionId = sessionId;
    }

    public String getMyAccount() {
        // 获取自己的账号信息
        return mMyAccount;
    }

    public void setMyAccount(String myAccount) {
        // 设置自己的账号信息
        mMyAccount = myAccount;
    }

    public String getChatAccount() {
        // 获取聊天对象的账号信息
        return mChatAccount;
    }

    public void setChatAccount(String chatAccount) {
        // 设置聊天对象的账号信息
        mChatAccount = chatAccount;
    }

    public String getChatNick() {
        // 获取聊天对象的昵称
        return mChatNick;
    }

    public void setChatNick(String chatNick) {
        // 设置聊天对象的昵称
        mChatNick = chatNick;
    }

    public NimUserInfo getMyInfo() {
        // 获取自己的用户信息
        return mMyInfo;
    }

    public void setMyInfo(NimUserInfo myInfo) {
        // 设置自己的用户信息
        mMyInfo = myInfo;
    }

    public NimUserInfo getChatInfo() {
        // 获取聊天对象的用户信息
        return mChatInfo;
    }

    public void setChatInfo(NimUserInfo chatInfo) {
        // 设置聊天对象的用户信息
        mChatInfo = chatInfo;
    }
//    这段代码定义了一系列的 getter 和 setter 方法，用于获取和设置聊天会话的相关信息，如会话类型、会话ID、账号信息、昵称以及用户信息。这些方法的目的是为了在聊天应用中方便地获取和设置会话相关的数据。例如，getSessionType 方法用于获取会话类型，setSessionType 方法用于设置会话类型。其他方法类似，用于处理不同类型的会话信息。这些信息对于聊天应用的正常运行非常重要，因为它们帮助应用区分不同的聊天会话和用户信息。
}
