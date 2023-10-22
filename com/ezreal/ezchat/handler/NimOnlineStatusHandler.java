package com.ezreal.ezchat.handler;

import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudeng on 2017/8/31.
 */

public class NimOnlineStatusHandler {

    private static final String TAG = NimOnlineStatusHandler.class.getSimpleName();
    // 定义一个字符串常量 TAG 用于日志标记

    private static NimOnlineStatusHandler instance;
    // 声明一个类变量 instance，用于在类中保存唯一的实例

    private List<OnStatusChangeListener> mListeners;
    // 声明一个 List 集合 mListeners，用于存储监听在线状态变化的监听器
//    这段代码定义了 NimOnlineStatusHandler 类，它用于处理在线状态的相关操作。这个类包含了一个日志标记 TAG，一个保存唯一实例的类变量 instance，以及一个用于存储在线状态变化监听器的列表 mListeners。
//
//    在线状态处理器通常用于处理用户的在线状态变化，例如用户上线或下线时触发的事件。mListeners 列表用于存储注册的在线状态监听器，这些监听器会在用户的在线状态发生变化时被通知，从而执行相应的操作。这种设计允许开发人员通过注册监听器来处理在线状态的变化，以满足不同应用的需求。

    public static NimOnlineStatusHandler getInstance() {
        // 获取 NimOnlineStatusHandler 的单例实例

        if (instance == null) {
            // 如果实例为 null，表示尚未初始化

            synchronized (NimOnlineStatusHandler.class) {
                // 进入同步块，确保多线程环境下只有一个线程可以执行下面的代码

                if (instance == null) {
                    // 再次检查实例是否为 null，这是为了防止多个线程同时通过第一层检查进入同步块时，一个线程创建了实例，另一个线程还会创建

                    instance = new NimOnlineStatusHandler();
                    // 创建 NimOnlineStatusHandler 的实例
                }
            }
        }

        return instance;
        // 返回实例，如果已经创建过了，直接返回之前创建的实例，确保只有一个实例存在。
    }
//    这段代码实现了单例模式，用于获取 NimOnlineStatusHandler 类的单一实例。单例模式确保一个类只有一个实例，且提供一个全局的访问点来获取该实例。这在需要共享资源的情况下很有用，以避免多次创建相同的对象，节省内存和系统资源。
//
//    这个方法使用了双重检查锁定模式 (Double-Checked Locking)，以确保在多线程环境下只有一个实例被创建。首先，它检查 instance 是否为 null，如果是，才会尝试进入同步块。进入同步块后，再次检查 instance 是否为 null，以确保没有其他线程已经创建了实例。
//
//    如果没有其他线程创建了实例，当前线程会创建一个新的 NimOnlineStatusHandler 实例，并赋给 instance。如果有其他线程已经创建了实例，当前线程会直接返回已经存在的实例。
//
//    这样，就能确保在多线程环境下只创建一个 NimOnlineStatusHandler 实例，避免了资源浪费和可能的竞态条件。

    public void init() {
        // 初始化方法

        mListeners = new ArrayList<>();
        // 创建一个 ArrayList 用于存储监听器

        NIMClient.getService(AuthServiceObserver.class)
                .observeOnlineStatus(new Observer<StatusCode>() {
                    @Override
                    public void onEvent(StatusCode statusCode) {
                        // 创建一个在线状态观察器

                        if (statusCode == StatusCode.UNLOGIN || statusCode == StatusCode.FORBIDDEN) {
                            // 如果用户状态为未登录或被禁止登录

                            Log.e(TAG, "OnlineObserver---UN_LOGIN");
                            // 在日志中记录未登录状态

                            if (mListeners != null && !mListeners.isEmpty()) {
                                for (OnStatusChangeListener listener : mListeners) {
                                    listener.requestReLogin("UN_LOGIN");
                                }
                            }
                            // 如果监听器列表不为空且非空，遍历监听器列表，调用 requestReLogin 方法通知监听器发起重新登录操作
                        } else if (statusCode == StatusCode.KICK_BY_OTHER_CLIENT
                                || statusCode == StatusCode.KICKOUT) {
                            // 如果用户状态为被其他客户端踢出登录或被踢出登录

                            Log.e(TAG, "OnlineObserver---KICK_OUT");
                            // 在日志中记录被踢出登录状态

                            if (mListeners != null && !mListeners.isEmpty()) {
                                for (OnStatusChangeListener listener : mListeners) {
                                    listener.requestReLogin("KICK_OUT");
                                }
                            }
                            // 如果监听器列表不为空且非空，遍历监听器列表，调用 requestReLogin 方法通知监听器发起重新登录操作
                        } else if (statusCode == StatusCode.NET_BROKEN) {
                            // 如果用户状态为网络断开

                            Log.e(TAG, "OnlineObserver---NET_BROKEN");
                            // 在日志中记录网络断开状态

                            if (mListeners != null && !mListeners.isEmpty()) {
                                for (OnStatusChangeListener listener : mListeners) {
                                    listener.networkBroken();
                                }
                            }
                            // 如果监听器列表不为空且非空，遍历监听器列表，调用 networkBroken 方法通知监听器网络断开
                        }
                    }
                }, true);
        // 注册在线状态观察器，监听用户的在线状态变化
    }
//    这段代码是一个初始化方法，用于设置在线状态的观察器。它包括以下步骤和功能：
//
//    初始化一个空的监听器列表 mListeners 以用于存储监听器对象。
//    使用 NIMClient 的 observeOnlineStatus 方法注册一个在线状态观察器。
//    在在线状态观察器中，根据不同的用户在线状态（如未登录、被踢出、网络断开等）执行不同的操作，包括记录状态信息和通知已注册的监听器。
//    如果用户状态为未登录或被禁止登录，将日志信息记录为 "UN_LOGIN"，然后通知已注册的监听器发起重新登录操作。
//    如果用户状态为被其他客户端踢出登录或被踢出登录，将日志信息记录为 "KICK_OUT"，然后通知已注册的监听器发起重新登录操作。
//    如果用户状态为网络断开，将日志信息记录为 "NET_BROKEN"，然后通知已注册的监听器网络已断开。
//    这段代码的主要作用是监听用户的在线状态变化，当用户的在线状态发生变化时，根据不同状态执行不同的操作，例如记录日志或通知已注册的监听器处理相应的状态变化。

    public void setStatusChangeListener(OnStatusChangeListener listener) {
        // 设置状态改变监听器
        mListeners.add(listener);
        // 将传入的监听器对象添加到监听器列表中
    }
//    setStatusChangeListener 方法用于设置状态改变监听器，即将传入的监听器对象添加到监听器列表 mListeners 中，以便在状态改变时通知这些监听器。

    public void removeStatusChangeListener(OnStatusChangeListener listener) {
        // 移除状态改变监听器
        mListeners.remove(listener);
        // 从监听器列表中移除传入的监听器对象
    }
//    removeStatusChangeListener 方法用于移除状态改变监听器，即将传入的监听器对象从监听器列表 mListeners 中移除，以停止监听状态改变事件。

    public interface OnStatusChangeListener {
        // 定义了一个接口 OnStatusChangeListener
        void requestReLogin(String message);
        // 定义了一个抽象方法 requestReLogin，用于通知状态改变时需要重新登录，同时传递一条消息作为参数
        void networkBroken();
        // 定义了一个抽象方法 networkBroken，用于通知状态改变时网络连接断开
    }
//    OnStatusChangeListener 接口定义了两抽象方法，requestReLogin 和 networkBroken，这些方法用于通知状态改变时需要重新登录或网络连接断开。在使用这个类时，你可以创建实现了 OnStatusChangeListener 接口的类，然后通过 setStatusChangeListener 方法将其添加为状态改变的监听器。当状态发生改变时，相关方法将被调用，执行相应的操作。这种机制通常用于处理用户登录状态和网络连接状态的变化。

}
