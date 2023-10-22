package com.ezreal.ezchat.handler;

import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by wudeng on 2017/8/30.
 */

public class NimSysMsgHandler {

    private static final String TAG = NimSysMsgHandler.class.getSimpleName();
    // 设置 TAG 常量，用于日志输出

    private static NimSysMsgHandler instance;
    // 创建 NimSysMsgHandler 单例实例

    private List<SystemMessageListener> mMessageListener;
    // 声明一个 SystemMessageListener 类型的 List，用于存储系统消息监听器
//    这段代码定义了一个名为 NimSysMsgHandler 的类，它似乎用于处理即时通讯系统的消息。

    public static NimSysMsgHandler getInstance() {
        // 获取 NimSysMsgHandler 的单例实例的静态方法
        if (instance == null) {
            // 如果实例为空（未初始化）
            synchronized (NimSysMsgHandler.class) {
                // 使用同步块确保多线程环境下只有一个线程可以执行下面的代码
                if (instance == null) {
                    // 再次检查实例是否为空，避免多线程竞态条件
                    instance = new NimSysMsgHandler();
                    // 创建 NimSysMsgHandler 的实例
                }
            }
        }
        return instance;
        // 返回 NimSysMsgHandler 的实例
    }
//    这段代码实现了一个获取 NimSysMsgHandler 单例实例的方法。它采用了双重检查锁定（Double-Checked Locking）的方式来确保在多线程环境中仅创建一个实例。首先检查 instance 是否为 null，如果是，进入同步块，再次检查 instance 是否为 null，如果是，就创建一个 NimSysMsgHandler 的实例。
//
//    这种方式能够在多线程环境下保证只有一个实例被创建，提高了性能，避免了每次获取实例都进行同步操作。这是一种常见的单例模式实现方法，用于确保某个类的实例全局唯一。

    public void init() {
        // 初始化方法
        mMessageListener = new ArrayList<>();
        // 创建一个 ArrayList 用于存储消息监听器对象

        NIMClient.getService(SystemMessageObserver.class)
                .observeReceiveSystemMsg(new Observer<SystemMessage>() {
                    @Override
                    public void onEvent(SystemMessage message) {
                        // 当接收到系统消息时触发的事件处理
                        AddFriendNotify notify = (AddFriendNotify) message.getAttachObject();
                        // 获取系统消息的附加对象（AddFriendNotify 类型）
                        if (notify != null) {
                            // 如果附加对象不为空
                            for (SystemMessageListener l : mMessageListener) {
                                l.addFriendNotify();
                                // 遍历消息监听器列表，调用每个监听器的添加好友通知方法
                            }
                        }
                    }
                }, true);
        // 注册一个系统消息观察者，监听接收到的系统消息
    }
//    这段代码是一个初始化方法，用于初始化消息监听器和注册系统消息观察者。在应用中，通常需要监听并响应各种消息，这段代码是 NIM（网易云信） SDK 中处理系统消息的部分。

    public void setMessageListener(SystemMessageListener listener) {
        // 设置消息监听器的方法
        mMessageListener.add(listener);
        // 向消息监听器列表中添加一个监听器
    }

    public interface SystemMessageListener {
        // 定义了一个接口 SystemMessageListener
        void addFriendNotify();
        // 接口中有一个抽象方法 addFriendNotify，用于处理系统消息中添加好友的通知
    }
//    这段代码包括两部分：
//
//    setMessageListener(SystemMessageListener listener) 方法：这是一个公共方法，用于设置消息监听器，通常用于添加处理系统消息的监听器。当系统消息到来时，会调用已注册的监听器，以便在应用中执行相应的操作。在这个方法中，将传入的 SystemMessageListener 添加到消息监听器列表 mMessageListener 中。
//
//    SystemMessageListener 接口：这是一个接口，定义了一个抽象方法 addFriendNotify，用于处理系统消息中添加好友的通知。你可以创建实现了这个接口的类，并在实现的 addFriendNotify 方法中定义具体的操作，以响应添加好友的系统消息。这种模式使得你可以动态注册不同的系统消息处理程序，以实现消息的多样化处理。
}
