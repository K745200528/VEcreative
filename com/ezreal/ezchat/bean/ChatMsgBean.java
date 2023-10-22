package com.ezreal.ezchat.bean;

import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by wudeng on 2017/9/5.
 */

public class ChatMsgBean {
    private int type;
    private String anchor;
    private IMMessage mMessage;

    // 获取消息锚点
    public String getAnchor() {
        return anchor;
    }

    // 获取消息类型
    public int getType() {
        return type;
    }

    // 设置消息类型
    public void setType(int type) {
        this.type = type;
    }

    // 设置消息锚点
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    // 获取聊天消息
    public IMMessage getMessage() {
        return mMessage;
    }

    // 设置聊天消息
    public void setMessage(IMMessage message) {
        mMessage = message;
    }
}
//    这段代码定义了一个名为 ChatMsgBean 的类，用于表示聊天消息的数据结构。它包含了消息的类型、锚点和消息内容。这个类还提供了一些方法来获取和设置这些属性的值。这通常用于在聊天应用程序中处理和显示消息。
